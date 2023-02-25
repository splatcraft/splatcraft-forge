package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.handlers.SplatcraftCommonHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;
import net.splatcraft.forge.util.PlayerCooldown;

public class SuperJumpCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("superjump").then(Commands.argument("to", BlockPosArgument.blockPos()).executes(context ->
		{
			BlockPos target = BlockPosArgument.getOrLoadBlockPos(context, "to");
			return excute(context, new Vector3d(target.getX()+.5d, target.getY(), target.getZ()+.5d));
		})).then(Commands.argument("target", EntityArgument.entity()).executes(context ->
				excute(context, EntityArgument.getEntity(context, "target").position()))));
	}

	private static int excute(CommandContext<CommandSource> context, Vector3d target) throws CommandSyntaxException
	{
		ServerPlayerEntity player = context.getSource().getPlayerOrException();


		PlayerCooldown.setPlayerCooldown(player, new SuperJump(player.inventory.selected, target, player.position(), player.noPhysics));

		player.displayClientMessage(new StringTextComponent("pchoooooo"), false);
		SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(player), player);

		return 0;
	}

	@Mod.EventBusSubscriber
	public static class Subscriber
	{
		@SubscribeEvent
		public static void playerTick(LivingEvent.LivingUpdateEvent event)
		{
			if(!(event.getEntityLiving() instanceof PlayerEntity))
				return;

			PlayerEntity player = (PlayerEntity) event.getEntityLiving();
			
			if(!PlayerCooldown.hasPlayerCooldown(player))
				return;

			IPlayerInfo info = PlayerInfoCapability.get(player);
			PlayerCooldown cooldown = info.getPlayerCooldown();

			if(cooldown instanceof SuperJump)
			{
				Vector3d target = ((SuperJump) cooldown).target;

				double distLeft = (player.position().multiply(1,0,1).distanceTo(target.multiply(1,0,1)));

				if(distLeft >= ((SuperJump) cooldown).distanceLeft)
					cooldown.setTime(0);

				((SuperJump) cooldown).setDistanceLeft(distLeft);

				player.stopFallFlying();
				player.abilities.flying = false;
				double distancePctg = ((SuperJump) cooldown).distanceLeft/((SuperJump) cooldown).distance;

				player.fallDistance = 0;

				if(!player.level.isClientSide() && distancePctg > .2f != info.isSquid())
				{
					info.setIsSquid(!info.isSquid());
					SplatcraftPacketHandler.sendToDim(new PlayerSetSquidClientPacket(player.getUUID(), info.isSquid()), player.level);
				}

				player.noPhysics = true;

				if(((SuperJump) cooldown).distanceLeft < 0.01)
					cooldown.setTime(0);
				else
				{
					Vector3d dist = target.subtract(player.position());

					dist = dist.multiply(1, 0, 1).scale(.1f).add(0, distancePctg > 0.9 ? Math.max(2, dist.y) * 1.25 : player.getDeltaMovement().y, 0);

					dist = new Vector3d(Math.min(3, dist.x), dist.y, Math.min(3, dist.z));

					player.setDeltaMovement(dist);
					player.hasImpulse = true;

					//new Vector3d((entitylivingbaseIn.getX() - entitylivingbaseIn.xo), (entitylivingbaseIn.getY() - entitylivingbaseIn.yo), (entitylivingbaseIn.getZ() - entitylivingbaseIn.zo)).normalize().y
				}
			}

		}
	}

	public static class SuperJump extends PlayerCooldown
	{
		final Vector3d target;
		double distance;
		double distanceLeft;
		boolean noClip = false;


		public SuperJump(int slotIndex, Vector3d target, Vector3d from, boolean canClip)
		{
			this(slotIndex, target, target.multiply(1,0,1).distanceTo(from.multiply(1,0,1)));
			this.noClip = canClip;
		}

		public SuperJump(int slotIndex, Vector3d target, double distance)
		{
			super(ItemStack.EMPTY, (int) distance, slotIndex, Hand.MAIN_HAND, false, false, false, false);
			this.target = target;
			this.distance = distance;
			distanceLeft = distance;
		}

		public SuperJump(CompoundNBT nbt)
		{
			this(nbt.getInt("SlotIndex"), new Vector3d(nbt.getDouble("TargetX"), nbt.getDouble("TargetY"), nbt.getDouble("TargetZ")), nbt.getDouble("Distance"));
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
		public CompoundNBT writeNBT(CompoundNBT nbt)
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
