package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.*;
import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler
{

	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
		ItemStack weapon = player.getActiveItemStack();
		
		//if(player.getActivePotionEffect(MobEffects.INVISIBILITY) == null)
		//	player.setInvisible(false);
		
		if(SplatCraftUtils.onEnemyInk(player.world, player) && player.ticksExisted % 20 == 0 && player.getHealth() > 4 && player.world.getDifficulty() != EnumDifficulty.PEACEFUL)
		{
			player.attackEntityFrom(new DamageSource("enemyInk"), 1f);
		}
		
		if(SplatCraftPlayerData.getIsSquid(player))
		{
			if(!player.isRiding())
			{
				SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);
				player.eyeHeight = 0.4f;
				
				if(SplatCraftUtils.canSquidHide(player.world, player))
				{
					player.fallDistance = 0;
					//player.setInvisible(true);
					
					if((Math.abs(player.posX - player.prevPosX) > 0.14 || Math.abs(player.posY - player.prevPosY) > 0.14 || Math.abs(player.posZ - player.prevPosZ) > 0.14) && player.world.isRemote)
						SplatCraftParticleSpawner.spawnInkParticle(player.posX, player.posY, player.posZ, 0, 0, 0, SplatCraftPlayerData.getInkColor(player), 4f);
					
					if(player.ticksExisted % 5 == 0)
						player.heal(0.5f);
				}
				
				if(player.world.getBlockState(pos.down()).getBlock().equals(SplatCraftBlocks.inkwell) && !player.world.isRemote)
					if(player.world.getTileEntity(pos.down()) instanceof TileEntityColor)
					{
						TileEntityColor te = (TileEntityColor) player.world.getTileEntity(pos.down());
						
						if(SplatCraftPlayerData.getInkColor(player) != te.getColor())
						{
							SplatCraftPlayerData.setInkColor(player, te.getColor());
							SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnColor(player.getUniqueID(), te.getColor()), player.dimension);
						}
					}
			}
			SplatCraftPlayerData.dischargeWeapon(player);
			SplatCraftPlayerData.setCanDischarge(player.getUniqueID(), true);
		}
		else if(!player.isDead && !player.isSpectator())
		{
			player.eyeHeight = player.getDefaultEyeHeight();
			if(weapon.getItem() instanceof ItemWeaponBase)
			{
				ItemWeaponBase item = (ItemWeaponBase) weapon.getItem();
				
				
				//if(item.getSpeedModifier() != null && attributeInstance.hasModifier(item.getSpeedModifier()))
				//	attributeInstance.removeModifier(item.getSpeedModifier());
				
				if(player.getItemInUseCount() > 0)
					item.onItemTickUse(player.world, player, weapon, player.getItemInUseCount());
				
			}
			else if(SplatCraftPlayerData.canDischarge(player)) SplatCraftPlayerData.dischargeWeapon(player);
		}

	}

	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event)
	{
		if(!(event.getEntity() instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.getEntity();

		SplatCraftPlayerData.PlayerData data = SplatCraftPlayerData.getPlayerData(player.getUniqueID());

		if(!event.getWorld().isRemote)
		{
			SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnColor(player.getUniqueID(), data.inkColor), player.dimension);
			SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnTransformed(player.getUniqueID(), data.isSquid), player.dimension);
		}
		else
			SplatCraftPacketHandler.instance.sendToServer(new PacketGetPlayerData());

	}
	
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event)
	{
		if(SplatCraftPlayerData.getIsSquid(event.getEntityPlayer()) && event.isCancelable())
			event.setCanceled(true);
	}

	//Loot Table Injector
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event)
	{
		ResourceLocation name = event.getName();

		if(name.equals(LootTableList.GAMEPLAY_FISHING_FISH))
		{
			LootEntry entry = new LootEntryTable(new ResourceLocation(SplatCraft.MODID, "inject/fishing_fish"), 5, 2,
					new LootCondition[0], SplatCraft.MODID+":fishing_fish");

			LootPool main = event.getTable().getPool("main");

			if(main != null)
				main.addEntry(entry);
			else SplatCraft.logger.info("The main fish loot pool is null, probably due to another mod, so you won't be able to get power eggs from fishing");
		}
		else if(name.equals(LootTableList.GAMEPLAY_FISHING_TREASURE))
		{
			LootEntry entry = new LootEntryTable(new ResourceLocation(SplatCraft.MODID, "inject/fishing_treasure"), 2, 5,
					new LootCondition[0], SplatCraft.MODID+":fishing_treasure");
			LootPool main = event.getTable().getPool("main");

			if(main != null)
				main.addEntry(entry);
			else SplatCraft.logger.info("The main fishing treasure loot pool is null, probably due to another mod, so you won't be able to get sunken crates from fishing");
		}

	}

	private AttributeModifier getWeaponMod(IAttributeInstance instance)
	{
		for(ItemWeaponBase item : ItemWeaponBase.weapons)
		{
			if(item.getSpeedModifier() == null)
				continue;
			if(instance.hasModifier(item.getSpeedModifier()))
				return item.getSpeedModifier();
		}
		return null;
	}
}
