package net.splatcraft.forge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.client.handlers.JumpLureHudHandler;
import net.splatcraft.forge.commands.SuperJumpCommand;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidS2CPacket;
import net.splatcraft.forge.network.s2c.SendJumpLureDataPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JumpLureItem extends Item implements IColoredItem
{
	public JumpLureItem()
	{
		super(new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flags)
	{
		super.appendHoverText(stack, level, tooltip, flags);

		if (I18n.exists(getDescriptionId() + ".tooltip"))
			tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));

		boolean inverted = ColorUtils.isInverted(stack);
		if (ColorUtils.isColorLocked(stack))
		{
			tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
			if(inverted)
				tooltip.add(new TranslatableComponent("item.splatcraft.tooltip.inverted").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.DARK_PURPLE)));
		}
		else tooltip.add(new TranslatableComponent( "item.splatcraft.tooltip.matches_color" + (inverted ? ".inverted" : "")).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public int getUseDuration(ItemStack p_41454_) {
		return 72000;

	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand)
	{
		if(level.isClientSide)
			return super.use(level, player, hand);

		int color = ColorUtils.getInkColorOrInverted(player.getItemInHand(hand));
		ArrayList<UUID> players = new ArrayList<>(getAvailableCandidates(player, color).stream().map(player1 -> player1.getGameProfile().getId()).toList());

		BlockPos spawnPadPos = SuperJumpCommand.getSpawnPadPos((ServerPlayer) player);

		if(!SplatcraftGameRules.getLocalizedRule(level, player.blockPosition(), SplatcraftGameRules.GLOBAL_SUPERJUMPING) && !Stage.targetsOnSameStage(level, player.position(), new Vec3(spawnPadPos.getX(), spawnPadPos.getY(), spawnPadPos.getZ())))
			spawnPadPos = null;

		SplatcraftPacketHandler.sendToPlayer(new SendJumpLureDataPacket(color, spawnPadPos != null,
				players), (ServerPlayer) player);

		player.startUsingItem(hand);
		return super.use(level, player, hand);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int useTime)
	{
		super.releaseUsing(stack, level, entity, useTime);

		if(level.isClientSide && entity.equals(Minecraft.getInstance().player))
			JumpLureHudHandler.releaseLure();
	}

	@Override
	public UseAnim getUseAnimation(ItemStack p_41452_) {
		return UseAnim.SPEAR;
	}

	public static void activate(ServerPlayer player, UUID targetUUID, int color)
	{
		Vec3 target;
		if(targetUUID == null)
		{
			BlockPos spawnPos = SuperJumpCommand.getSpawnPadPos(player);
			if(spawnPos != null)
			{
				target = new Vec3(spawnPos.getX(), spawnPos.getY() + SuperJumpCommand.blockHeight(spawnPos, player.level), spawnPos.getZ());
				if(!SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.GLOBAL_SUPERJUMPING) && !Stage.targetsOnSameStage(player.level, player.position(), target))
				{
					player.sendMessage(new TextComponent("Spawn Pad outside of stage bounds!"), player.getUUID()); //TODO better feedback
					return;
				}
			}
			else
			{
				player.sendMessage(new TextComponent("No valid Spawn Pad was found!"), player.getUUID()); //TODO better feedback
				return;
			}
		}
		else
		{
			Player targetPlayer = player.level.getPlayerByUUID(targetUUID);

			if(targetPlayer == null || !hasMatchingLure(targetPlayer, color) || (!SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.GLOBAL_SUPERJUMPING)
					&& !Stage.targetsOnSameStage(player.level, player.position(), targetPlayer.position())))
			{
				player.sendMessage(new TextComponent("A communication error has occurred."), player.getUUID()); //TODO better feedback
				return;
			}
			else target = targetPlayer.position();
		}

		SuperJumpCommand.superJump(player, target);
	}



	public static boolean hasMatchingLure(Player targetPlayer, int color)
	{
		for(int i = 0; i < targetPlayer.getInventory().getContainerSize(); i++)
			if(targetPlayer.getInventory().getItem(i).getItem() instanceof JumpLureItem &&
					ColorUtils.colorEquals(targetPlayer.level, targetPlayer.blockPosition(), color, ColorUtils.getInkColorOrInverted(targetPlayer.getInventory().getItem(i))))
				return true;
		return false;
	}

	public static List<? extends Player> getAvailableCandidates(Player player, int color)
	{
		ArrayList<Player> players = new ArrayList<>();

		if(SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.GLOBAL_SUPERJUMPING))
			players.addAll(player.level.players());
		else
		{
			ArrayList<Stage> stages = Stage.getStagesForPosition(player.level, player.position());
			for(Stage stage : stages)
				players.addAll(player.level.getEntitiesOfClass(Player.class, stage.getBounds()));
		}


		players.removeIf(target ->
				player.equals(target) || !hasMatchingLure(target, color)
						&& !Stage.targetsOnSameStage(player.level, player.position(), target.position()));

		return players;
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

}
