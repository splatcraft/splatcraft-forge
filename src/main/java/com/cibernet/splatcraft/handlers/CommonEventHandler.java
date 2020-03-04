package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.PacketPlayerData;
import com.cibernet.splatcraft.network.SplatCraftChannelHandler;
import com.cibernet.splatcraft.network.SplatCraftPacket;
import com.cibernet.splatcraft.network.tutorial.PacketPlayerSetColor;
import com.cibernet.splatcraft.network.tutorial.SplatCraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler
{

	public static final AttributeModifier IN_USE_SPEED_BOOST = (new AttributeModifier( "Weapon use speed boost", 4D, 2)).setSaved(false);

	private static final AttributeModifier SQUID_LAND_SPEED = (new AttributeModifier( "Squid in land speed boost", -0.4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_SWIM_SPEED = (new AttributeModifier( "Squid swim speed boost", 1.25D, 2)).setSaved(false);

	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		IAttributeInstance attributeInstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);


		if(attributeInstance.hasModifier(SQUID_LAND_SPEED))
			attributeInstance.removeModifier(SQUID_LAND_SPEED);
		if(attributeInstance.hasModifier(SQUID_SWIM_SPEED))
			attributeInstance.removeModifier(SQUID_SWIM_SPEED);
		if(attributeInstance.hasModifier(IN_USE_SPEED_BOOST))
			attributeInstance.removeModifier(IN_USE_SPEED_BOOST);

		AttributeModifier weaponMod = getWeaponMod(attributeInstance);
		if(weaponMod != null)
			attributeInstance.removeModifier(weaponMod);

		if(SplatCraftPlayerData.getIsSquid(player))
		{
			SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);

			if(SplatCraftUtils.canSquidHide(player.world, player))
			{
				if(!attributeInstance.hasModifier(SQUID_SWIM_SPEED))
					attributeInstance.applyModifier(SQUID_SWIM_SPEED);
			}
			else if(!attributeInstance.hasModifier(SQUID_LAND_SPEED))
				attributeInstance.applyModifier(SQUID_LAND_SPEED);

			if(player.world.getBlockState(pos.down()).getBlock().equals(SplatCraftBlocks.inkwell))
				if(player.world.getTileEntity(pos.down()) instanceof TileEntityColor)
				{
					TileEntityColor te = (TileEntityColor) player.world.getTileEntity(pos.down());

					if(SplatCraftPlayerData.getInkColor(player) != te.getColor()) {
						SplatCraftPacketHandler.instance.sendToServer(new PacketPlayerSetColor(player.getUniqueID(), te.getColor()));
					}
				}
		}

		ItemStack weapon = player.getActiveItemStack();
		if(weapon.getItem() instanceof ItemWeaponBase)
		{
			ItemWeaponBase item = (ItemWeaponBase) weapon.getItem();


			//if(item.getSpeedModifier() != null && attributeInstance.hasModifier(item.getSpeedModifier()))
			//	attributeInstance.removeModifier(item.getSpeedModifier());

			if(player.getItemInUseCount() > 0)
			{
				item.onItemTickUse(player.world, player, weapon, player.getItemInUseCount());
				if(!attributeInstance.hasModifier(IN_USE_SPEED_BOOST))
					attributeInstance.applyModifier(IN_USE_SPEED_BOOST);
				if(item.getSpeedModifier() != null && !attributeInstance.hasModifier(item.getSpeedModifier()))
					attributeInstance.applyModifier(item.getSpeedModifier());
			}

		}

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
			else System.out.println("main is null!");
		}
		else if(name.equals(LootTableList.GAMEPLAY_FISHING_TREASURE))
		{
			LootEntry entry = new LootEntryTable(new ResourceLocation(SplatCraft.MODID, "inject/fishing_treasure"), 2, 5,
					new LootCondition[0], SplatCraft.MODID+":fishing_treasure");
			LootPool main = event.getTable().getPool("main");

			if(main != null)
				main.addEntry(entry);
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
