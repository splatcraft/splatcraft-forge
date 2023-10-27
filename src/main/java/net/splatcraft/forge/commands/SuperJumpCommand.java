package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidS2CPacket;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;
import net.splatcraft.forge.util.PlayerCooldown;

public class SuperJumpCommand
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("superjump").requires(commandSource -> commandSource.hasPermission(2)).then(Commands.argument("to", BlockPosArgument.blockPos()).executes(context ->
		{
			BlockPos target = BlockPosArgument.getLoadedBlockPos(context, "to");
			return execute(context, new Vec3(target.getX() + .5d, target.getY(), target.getZ() + .5d));
		})).then(Commands.argument("target", EntityArgument.entity()).executes(context ->
				execute(context, EntityArgument.getEntity(context, "target").position()))));
	}

	private static int execute(CommandContext<CommandSourceStack> context, Vec3 target) throws CommandSyntaxException
	{
		ServerPlayer player = context.getSource().getPlayerOrException();


		PlayerCooldown.setPlayerCooldown(player, new SuperJump(player.getInventory().selected, target, player.position(), player.noPhysics));

		player.displayClientMessage(new TextComponent("pchoooooo"), false);
		SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(player), player);

		return 0;
	}

	@Mod.EventBusSubscriber
	public static class Subscriber
	{
		@SubscribeEvent
		public static void playerTick(LivingEvent.LivingUpdateEvent event)
		{
			if(!(event.getEntityLiving() instanceof Player player))
				return;

			if(!PlayerCooldown.hasPlayerCooldown(player))
				return;

			PlayerInfo info = PlayerInfoCapability.get(player);
			PlayerCooldown cooldown = info.getPlayerCooldown();

			if(cooldown instanceof SuperJump)
			{
				Vec3 target = ((SuperJump) cooldown).target;

				double distLeft = (player.position().multiply(1,0,1).distanceTo(target.multiply(1,0,1)));

				if(distLeft >= ((SuperJump) cooldown).distanceLeft)
					cooldown.setTime(0);

				((SuperJump) cooldown).setDistanceLeft(distLeft);

				player.stopFallFlying();
				player.getAbilities().flying = false;
				double distancePctg = ((SuperJump) cooldown).distanceLeft/((SuperJump) cooldown).distance;

				player.fallDistance = 0;

				if (distancePctg > .2f != info.isSquid()) {
					info.setIsSquid(!info.isSquid());
					if (!player.level.isClientSide()) {
						SplatcraftPacketHandler.sendToTrackers(new PlayerSetSquidS2CPacket(player.getUUID(), info.isSquid()), player);
					}
				}

				player.noPhysics = true;

				if(((SuperJump) cooldown).distanceLeft < 0.01)
					cooldown.setTime(0);
				else
				{
					Vec3 dist = target.subtract(player.position());

					dist = dist.multiply(1, 0, 1).scale(.1f).add(0, distancePctg > 0.9 ? Math.max(2, dist.y) * 1.25 : player.getDeltaMovement().y, 0);

					dist = new Vec3(Math.min(3, dist.x), dist.y, Math.min(3, dist.z));

					player.setDeltaMovement(dist);
					player.hurtMarked = true;

					//new Vec3((entitylivingbaseIn.getX() - entitylivingbaseIn.xo), (entitylivingbaseIn.getY() - entitylivingbaseIn.yo), (entitylivingbaseIn.getZ() - entitylivingbaseIn.zo)).normalize().y
				}
			}

		}
	}

	public static class SuperJump extends PlayerCooldown
	{
		final Vec3 target;
		double distance;
		double distanceLeft;
		boolean noClip = false;


		public SuperJump(int slotIndex, Vec3 target, Vec3 from, boolean canClip)
		{
			this(slotIndex, target, target.multiply(1,0,1).distanceTo(from.multiply(1,0,1)));
			this.noClip = canClip;
		}

		public SuperJump(int slotIndex, Vec3 target, double distance)
		{
			super(ItemStack.EMPTY, (int) distance, slotIndex, InteractionHand.MAIN_HAND, false, false, false, false);
			this.target = target;
			this.distance = distance;
			distanceLeft = distance;
		}

		public SuperJump(CompoundTag nbt)
		{
			this(nbt.getInt("SlotIndex"), new Vec3(nbt.getDouble("TargetX"), nbt.getDouble("TargetY"), nbt.getDouble("TargetZ")), nbt.getDouble("Distance"));
			distanceLeft = nbt.getDouble("DistanceLeft");
			noClip = nbt.getBoolean("CanClip");
		}

		public void setDistanceLeft(double distanceLeft) {
			this.distanceLeft = distanceLeft;
		}

		@Override
		public PlayerCooldown setTime(int v)
		{
			if(getTime() > 0)
				super.setTime((int) distanceLeft);

			return this;
		}

		@Override
		public CompoundTag writeNBT(CompoundTag nbt)
		{
			nbt.putInt("SlotIndex", getSlotIndex());
			nbt.putDouble("Distance", distance);
			nbt.putDouble("DistanceLeft", distanceLeft);

			nbt.putDouble("TargetX", target.x);
			nbt.putDouble("TargetY", target.y);
			nbt.putDouble("TargetZ", target.z);

			nbt.putBoolean("SuperJump", true);
			nbt.putBoolean("CanClip", noClip);

			return nbt;
		}
	}
}
