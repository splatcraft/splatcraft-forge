package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WeaponBaseItem extends Item
{
	public WeaponBaseItem()
	{
		super(new Properties().maxStackSize(1).group(SplatcraftItemGroups.GROUP_WEAPONS));
		SplatcraftItems.inkColoredItems.add(this);
		SplatcraftItems.weapons.add(this);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		
		if(entity instanceof PlayerEntity && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity))
			ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		player.setActiveHand(hand);
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
	{
		entity.resetActiveHand();
		super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
	}
	
	public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
	{
	
	}
}
