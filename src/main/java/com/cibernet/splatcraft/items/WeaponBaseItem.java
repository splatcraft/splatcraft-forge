package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WeaponBaseItem extends Item
{
	public WeaponBaseItem()
	{
		super(new Properties().maxStackSize(1).group(SplatcraftItemGroups.GROUP_WEAPONS));
		ColorHandler.inkColoredItems.add(this);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		
		if(entity instanceof PlayerEntity && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity))
			ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity player, Hand p_77659_3_)
	{
		ColorUtils.setPlayerColor(player, ColorUtils.getRandomStarterColor());
		return super.onItemRightClick(p_77659_1_, player, p_77659_3_);
	}
}
