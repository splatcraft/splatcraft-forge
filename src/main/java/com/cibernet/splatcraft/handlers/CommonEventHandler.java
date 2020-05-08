package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.IBattleItem;
import com.cibernet.splatcraft.items.ItemDualieBase;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.*;
import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftStats;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import static com.cibernet.splatcraft.utils.ColorItemUtils.*;

public class CommonEventHandler
{

	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.phase == TickEvent.Phase.START)
			return;
		
		EntityPlayer player = event.player;
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
		ItemStack weapon = player.getActiveItemStack();
		
		//if(player.getActivePotionEffect(MobEffects.INVISIBILITY) == null)
		//	player.setInvisible(false);
		
		SplatCraftPlayerData.PlayerData data = SplatCraftPlayerData.getPlayerData(player);
		if(data.isSquid == 1)
			data.isSquid = 0;
		
		if(SplatCraftUtils.onEnemyInk(player.world, player) && player.ticksExisted % 20 == 0 && player.getHealth() > 4 && player.world.getDifficulty() != EnumDifficulty.PEACEFUL)
		{
			player.attackEntityFrom(new DamageSource("enemyInk"), 1f);
		}
		
		if(SplatCraftPlayerData.getIsSquid(player))
		{
			if(!player.isRiding())
			{
				SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);
				if(!SplatCraft.disableEyeHeight)
					player.eyeHeight = 0.4f;
				
				if(SplatCraftUtils.canSquidHide(player.world, player))
				{
					player.fallDistance = 0;
					//player.setInvisible(true);
					
					if((Math.abs(player.posX - player.prevPosX) > 0.14 || Math.abs(player.posY - player.prevPosY) > 0.14 || Math.abs(player.posZ - player.prevPosZ) > 0.14) && player.world.isRemote)
						SplatCraftParticleSpawner.spawnSquidSwimParticle(player.posX, player.posY, player.posZ, 0, 0, 0, SplatCraftPlayerData.getInkColor(player), 4f);
					
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
			
			player.addStat(SplatCraftStats.SQUID_TIME);
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
				
				if(item instanceof ItemDualieBase)
				{
					int maxRolls = ((ItemDualieBase) item).maxRolls;
					
					ItemStack offhandStack = player.getHeldItem(player.getHeldItemMainhand().equals(weapon) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
					if(offhandStack.getItem() instanceof ItemDualieBase)
					{
						maxRolls += ((ItemDualieBase) offhandStack.getItem()).maxRolls;
						item.onItemTickUse(player.world, player, offhandStack, player.getItemInUseCount() + ((ItemDualieBase) offhandStack.getItem()).offhandFiringOffset);
					}
					
					int rollCount = ItemDualieBase.getRollString(weapon);
					CooldownTracker cooldownTracker = player.getCooldownTracker();
					
					if(player.world.isRemote && player instanceof EntityPlayerSP && cooldownTracker.getCooldown(item, 0) <= 0)
					{
						MovementInput input = ((EntityPlayerSP) player).movementInput;
						if(input.jump && (input.moveStrafe != 0 || input.moveForward != 0))
						{
							ItemStack activeDualie = weapon;
							
							if(offhandStack.getItem() instanceof ItemDualieBase)
							{
								ItemStack dualieA = ((ItemDualieBase) item).maxRolls >= ((ItemDualieBase) offhandStack.getItem()).maxRolls ? weapon : offhandStack;
								ItemStack dualieB = dualieA.equals(weapon) ? offhandStack : weapon;
								activeDualie = maxRolls % 2 == 0 ? dualieA : dualieB;
							}
							
							if(ItemWeaponBase.hasInk(player, ColorItemUtils.getInkColor(weapon), ((ItemDualieBase) item).rollConsumption))
							{
								player.motionY = 0;
								player.moveRelative(input.moveStrafe, -0.2f, input.moveForward, ((ItemDualieBase)activeDualie.getItem()).rollSpeed);
								
								int cooldown = rollCount >= maxRolls-1 ? 30 : 8;
								SplatCraftPacketHandler.instance.sendToServer(new PacketDodgeRoll(activeDualie.equals(offhandStack), cooldown));
								
								
								cooldownTracker.setCooldown(weapon.getItem(), cooldown);
								if(offhandStack.getItem() instanceof ItemDualieBase)
									cooldownTracker.setCooldown(offhandStack.getItem(), cooldown);
								
								
								ItemDualieBase.setRollString(weapon, rollCount + 1);
								ItemDualieBase.setRollCooldown(weapon, 20);
								
								ItemWeaponBase.reduceInk(player, ((ItemDualieBase) activeDualie.getItem()).rollConsumption);
							} else player.sendStatusMessage(new TextComponentTranslation("status.noInk").setStyle(new Style().setColor(TextFormatting.RED)), true);
						}
					}
					
					
				}
				
			}
			else if(SplatCraftPlayerData.canDischarge(player)) SplatCraftPlayerData.dischargeWeapon(player);
		}

	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.phase == TickEvent.Phase.START)
		{
			List<Entity> entityItems = new ArrayList<>(event.world.loadedEntityList);
			entityItems.removeIf(entity -> !(entity instanceof EntityItem));
			for(Entity entity : entityItems)
			{
				EntityItem entityItem = (EntityItem) entity;
				
				BlockPos pos = new BlockPos(entityItem.posX, entityItem.posY-1, entityItem.posZ);
				
				ItemStack stack = entityItem.getItem();
				
				if((stack.getItem().equals(Item.getItemFromBlock(SplatCraftBlocks.inkedWool)) || stack.isItemEqual(new ItemStack(Item.getItemFromBlock(Blocks.WOOL),1,0)))
						&& entityItem.world.getBlockState(pos).getBlock().equals(SplatCraftBlocks.inkwell))
				{
					
					if(entityItem.world.getTileEntity(pos) instanceof TileEntityColor)
					{
						TileEntityColor te = (TileEntityColor) entityItem.world.getTileEntity(pos);
						
						//if(InkColors.getByColor(te.getColor()).getDyeColor() != null)
						//	stack = new ItemStack(Blocks.WOOL, stack.getCount(), InkColors.getByColor(te.getColor()).getDyeColor().getMetadata());
						//else
						stack = new ItemStack(SplatCraftBlocks.inkedWool, stack.getCount());
						entityItem.setItem(stack);
						setInkColor(stack, te.getColor());
					}
				}
				else if (stack.getItem().equals(Item.getItemFromBlock(SplatCraftBlocks.inkedWool)) && entityItem.world.getBlockState(pos.up()).getMaterial().equals(Material.WATER))
				{
					entityItem.setItem(new ItemStack(Blocks.WOOL, stack.getCount()));
				}
			
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDropsEvent event) {
		if (event.getEntityPlayer() != null && !(event.getEntityPlayer() instanceof FakePlayer) && !event.isCanceled()) {
			if (!event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory") && SplatCraftGamerules.getGameruleValue("keepWeaponsOnDeath")) {
				ListIterator iter = event.getDrops().listIterator();
				
				while(iter.hasNext()) 
				{
					EntityItem ei = (EntityItem)iter.next();
					ItemStack item = ei.getItem();
					if (item.getItem() instanceof ItemWeaponBase && addToPlayerInventory(event.getEntityPlayer(), item))
						iter.remove();
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerClone(PlayerEvent.Clone event) {
		if (event.isWasDeath() && !event.isCanceled()) {
			if (event.getOriginal() != null && event.getEntityPlayer() != null && !(event.getEntityPlayer() instanceof FakePlayer)) {
				if (!event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory") && SplatCraftGamerules.getGameruleValue("keepWeaponsOnDeath")) {
					if (event.getOriginal() != event.getEntityPlayer() && event.getOriginal().inventory != event.getEntityPlayer().inventory && (event.getOriginal().inventory.armorInventory != event.getEntityPlayer().inventory.armorInventory || event.getOriginal().inventory.mainInventory != event.getEntityPlayer().inventory.mainInventory)) {
						int i;
						ItemStack item;
						
						for(i = 0; i < event.getOriginal().inventory.mainInventory.size(); ++i)
						{
							item = event.getOriginal().inventory.mainInventory.get(i);
							if (item.getItem() instanceof IBattleItem && addToPlayerInventory(event.getEntityPlayer(), item))
								event.getOriginal().inventory.mainInventory.set(i, ItemStack.EMPTY);
							
						}
						
					}
				}
			}
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
			SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnTransformed(player.getUniqueID(), data.isSquid == 2), player.dimension);
			NBTTagCompound ruleNBT = new NBTTagCompound();
			SplatCraftGamerules.writeToNBT(ruleNBT);
			SplatCraftPacketHandler.instance.sendTo(new PacketUpdateGamerule(ruleNBT), (EntityPlayerMP) player);
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

	@SubscribeEvent
	public void onPlayerAttack(AttackEntityEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(SplatCraftPlayerData.getIsSquid(player))
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
	
	private static boolean addToPlayerInventory(EntityPlayer entityPlayer, ItemStack item)
	{
		if (item != null && entityPlayer != null)
		{
			int i;
			if (item.getItem() instanceof ItemArmor)
			{
				ItemArmor arm = (ItemArmor)item.getItem();
				i = arm.armorType.getIndex();
				if ((entityPlayer.inventory.armorInventory.get(i)).isEmpty())
				{
					entityPlayer.inventory.armorInventory.set(i, item);
					return true;
				}
			}
			
			InventoryPlayer inv = entityPlayer.inventory;
			
			for(i = 0; i < inv.mainInventory.size(); ++i)
			{
				if ((inv.mainInventory.get(i)).isEmpty())
				{
					inv.mainInventory.set(i, item.copy());
					return true;
				}
			}
			
			return false;
		} else 	return false;
		
	}
}
