package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.PacketPlayerSetColor;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler
{

	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);


		if(SplatCraftPlayerData.getIsSquid(player))
		{
			SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);

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
				item.onItemTickUse(player.world, player, weapon, player.getItemInUseCount());

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
