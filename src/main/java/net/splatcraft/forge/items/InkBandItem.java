package net.splatcraft.forge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkBandItem extends Item
{
	public InkBandItem()
	{
		super(new Item.Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_GENERAL));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltip, flags);
		tooltip.add(new TranslatableComponent(stack.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
	}
}
