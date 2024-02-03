package net.splatcraft.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidS2CPacket;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;
import net.splatcraft.forge.registries.SplatcraftAttributes;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.PlayerCooldown;

import java.util.ArrayList;

public class SuperJumpCommand
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("superjump").requires(commandSource -> commandSource.hasPermission(2)).then(Commands.argument("location", Vec3Argument.vec3()).executes(context ->
		{
			BlockPos target = BlockPosArgument.getLoadedBlockPos(context, "location");
			return executeLocation(context, new Vec3(target.getX() + .5d, target.getY(), target.getZ() + .5d));
		})).then(Commands.argument("target", EntityArgument.entity()).executes(context ->
				executeLocation(context, EntityArgument.getEntity(context, "target").position())))
				.executes(SuperJumpCommand::executeSpawn));
	}

	private static int executeLocation(CommandContext<CommandSourceStack> context, Vec3 target) throws CommandSyntaxException
	{
		ServerPlayer player = context.getSource().getPlayerOrException();
		superJump(player, target);

		return 0;
	}

	private static int executeSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ServerPlayer player = context.getSource().getPlayerOrException();
		superJumpToSpawn(player);

		return 0;
	}

	public static boolean superJumpToSpawn(ServerPlayer player)
	{
		if(player.getRespawnDimension().equals(player.level.dimension()))
		{
			BlockPos targetPos = player.getRespawnPosition();
			if(player.level.getBlockEntity(targetPos) instanceof SpawnPadTileEntity spawnpad && !ColorUtils.colorEquals(player, spawnpad))
				targetPos = null;
			if(targetPos == null)
				targetPos = new BlockPos(player.level.getLevelData().getXSpawn(), player.level.getLevelData().getYSpawn(), player.level.getLevelData().getZSpawn());
			superJump(player, new Vec3(targetPos.getX(), targetPos.getY() + blockHeight(targetPos, player.level), targetPos.getZ()));

			return true;
		}

		return false;
	}

	public static boolean superJump(ServerPlayer player, Vec3 target)
	{
		return superJump(player, target,
				(int) player.getAttribute(SplatcraftAttributes.superJumpTravelTime.get()).getValue(),
				(int) player.getAttribute(SplatcraftAttributes.superJumpWindupTime.get()).getValue(),
				player.getAttribute(SplatcraftAttributes.superJumpHeight.get()).getValue(),
				SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.GLOBAL_SUPERJUMPING));
	}
	public static boolean superJump(ServerPlayer player, Vec3 target, int windupTime, int travelTime, double jumpHeight, boolean global)
	{
		if(!global && SaveInfoCapability.get(player.server).getStages().values().stream().filter(stage -> stage.getBounds().contains(player.position()) && stage.getBounds().contains(target)).toList().isEmpty())
			return false;

		PlayerCooldown.setPlayerCooldown(player, new SuperJump(player.position(), target, windupTime, travelTime, jumpHeight, player.noPhysics));

		PlayerInfo info = PlayerInfoCapability.get(player);
		if(!info.isSquid())
		{
			info.setIsSquid(true);
			SplatcraftPacketHandler.sendToTrackers(new PlayerSetSquidS2CPacket(player.getUUID(), info.isSquid()), player);
		}


		player.displayClientMessage(new TextComponent("pchoooooo"), false);
		SplatcraftPacketHandler.sendToPlayer(new UpdatePlayerInfoPacket(player), player);

		return true;
	}

	public static double blockHeight(BlockPos block, Level level){
		VoxelShape shape = level.getBlockState(block).getShape(level, block);
		if (shape.isEmpty()){
			return 0;
		}else {
			return shape.bounds().getYsize();
		}
	}

	@Mod.EventBusSubscriber
	public static class Subscriber
	{
		@SubscribeEvent
		public static void playerTick(LivingEvent.LivingUpdateEvent event)
		{

			if(PlayerInfoCapability.hasCapability(event.getEntityLiving()) && PlayerInfoCapability.get(event.getEntityLiving()).getPlayerCooldown() instanceof SuperJump superJump)
			{
				event.getEntityLiving().noPhysics = superJump.isSquid();
				event.getEntityLiving().fallDistance = 0;
			}

			if(!PlayerCooldown.hasPlayerCooldown(event.getEntityLiving()))
				return;

			if(!(event.getEntityLiving().level.isClientSide && event.getEntityLiving() instanceof LocalPlayer player))
				return;

			PlayerInfo info = PlayerInfoCapability.get(player);

			if(info.getPlayerCooldown() instanceof SuperJump cooldown && player.level.isClientSide)
			{
				player.setDeltaMovement(0,0,0);

				float progress = cooldown.getSuperJumpProgress();

				if(!cooldown.isSquid() && info.isSquid())
					ClientUtils.setSquid(info, false);

				player.setPos(Mth.lerp(progress, cooldown.source.x, cooldown.target.x), getSuperJumpYPos(progress, cooldown.source.y, cooldown.target.y, cooldown.getHeight()), Mth.lerp(progress, cooldown.source.z, cooldown.target.z));
			}

		}
	}

	public static double getSuperJumpYPos(double progress, double startY, double endY, double arcHeight)
	{
		float distance = 1;
		return arcHeight * Math.sin(progress/ distance * Math.PI) + ((endY-startY)/(distance) * (progress) + startY);
	}

	public static class SuperJump extends PlayerCooldown
	{
		final int travelTime;
		final int windupTime;
		final double height;
		final Vec3 target;
		final Vec3 source;
		boolean hadPhysics;



		public SuperJump(Vec3 target, Vec3 source, int travelTime, int windupTime, double height, boolean hadPhysics)
		{

			super(ItemStack.EMPTY, travelTime + windupTime + 1, -1, InteractionHand.MAIN_HAND, false, false, false, false);
			this.target = target;
			this.source = source;
			this.hadPhysics = hadPhysics;
			this.travelTime = travelTime;
			this.windupTime = windupTime;
			this.height = height;
		}

		public SuperJump(CompoundTag nbt)
		{
			this(new Vec3(nbt.getDouble("TargetX"), nbt.getDouble("TargetY"), nbt.getDouble("TargetZ")),
					new Vec3(nbt.getDouble("SourceX"), nbt.getDouble("SourceY"), nbt.getDouble("SourceZ")), nbt.getInt("TravelTime"), nbt.getInt("WindupTime"),
					nbt.getDouble("Height"), nbt.getBoolean("CanClip"));
			setTime(nbt.getInt("TimeLeft"));
		}

		@Override
		public boolean preventWeaponUse()
		{
			return isSquid();
		}

		@Override
		public CompoundTag writeNBT(CompoundTag nbt)
		{
			nbt.putDouble("TargetX", target.x);
			nbt.putDouble("TargetY", target.y);
			nbt.putDouble("TargetZ", target.z);

			nbt.putDouble("SourceX", source.x);
			nbt.putDouble("SourceY", source.y);
			nbt.putDouble("SourceZ", source.z);

			nbt.putDouble("Height", height);
			nbt.putBoolean("SuperJump", true);
			nbt.putBoolean("CanClip", hadPhysics);

			nbt.putInt("TimeLeft", getTime());
			nbt.putInt("WindupTime", getWindupTime());
			nbt.putInt("TravelTime", getTravelTime());

			return nbt;
		}

		public int getTravelTime() {
			return travelTime;
		}

		public int getWindupTime() {
			return windupTime;
		}

		public float getSuperJumpProgress()
		{
			return Mth.clamp((getTime()-1) / (float) getTravelTime(), 0, 1);
		}

		public boolean isSquid()
		{
			return getSuperJumpProgress() > 0.2f ;
		}

		public double getHeight() {
			return height;
		}
	}
}
