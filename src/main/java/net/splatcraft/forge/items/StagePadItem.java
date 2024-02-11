package net.splatcraft.forge.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.blocks.InkedBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.client.gui.stagepad.StageSelectionScreen;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.SendStageWarpDataToPadPacket;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;

public class StagePadItem extends Item implements IColoredItem
{
	public StagePadItem()
	{
		super(new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1));
		SplatcraftItems.inkColoredItems.add(this);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		player.awardStat(Stats.ITEM_USED.get(this));

		if(level.isClientSide)
			Minecraft.getInstance().setScreen(new StageSelectionScreen(itemstack.getDisplayName()));
		else SplatcraftPacketHandler.sendToPlayer(SendStageWarpDataToPadPacket.compile(player), (ServerPlayer) player);

		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, level, entity, itemSlot, isSelected);

		if (entity instanceof Player player) {
			if (!ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor(player)
					&& PlayerInfoCapability.hasCapability(player))
				ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor(player));
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		BlockPos pos = entity.blockPosition().below();

		if (entity.level.getBlockState(pos).getBlock() instanceof InkwellBlock) {
			InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos);

			if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColorOrInverted(entity.level, pos)) {
				ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColorOrInverted(entity.level, pos));
				ColorUtils.setColorLocked(entity.getItem(), true);
			}
		} else if ((stack.getItem() instanceof SubWeaponItem && !SubWeaponItem.singleUse(stack) || !(stack.getItem() instanceof SubWeaponItem))
				&& InkedBlock.causesClear(entity.level, pos, entity.level.getBlockState(pos)) && ColorUtils.getInkColor(stack) != 0xFFFFFF) {
			ColorUtils.setInkColor(stack, 0xFFFFFF);
			ColorUtils.setColorLocked(stack, false);
		}

		return false;
	}
}
