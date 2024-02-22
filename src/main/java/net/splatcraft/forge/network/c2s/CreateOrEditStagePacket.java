package net.splatcraft.forge.network.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.NotifyStageCreatePacket;

import java.util.Objects;

public class CreateOrEditStagePacket extends PlayC2SPacket
{
	final String stageId;
	final Component stageName;
	final BlockPos corner1;
	final BlockPos corner2;
	final ResourceLocation dimension;

	public CreateOrEditStagePacket(String stageId, Component stageName, BlockPos corner1, BlockPos corner2, Level dimension)
	{
		this(stageId, stageName, corner1, corner2, dimension.dimension().location());
	}
	public CreateOrEditStagePacket(String stageId, Component stageName, BlockPos corner1, BlockPos corner2, ResourceLocation dimension)
	{
		this.stageId = stageId;
		this.stageName = stageName;
		this.corner1 = corner1;
		this.corner2 = corner2;
		this.dimension = dimension;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeUtf(stageId);
		buffer.writeComponent(stageName);
		buffer.writeBlockPos(corner1);
		buffer.writeBlockPos(corner2);
		buffer.writeResourceLocation(dimension);
	}

	public static CreateOrEditStagePacket decode(FriendlyByteBuf buf)
	{
		return new CreateOrEditStagePacket(buf.readUtf(), buf.readComponent(), buf.readBlockPos(), buf.readBlockPos(), buf.readResourceLocation());
	}

	@Override
	public void execute(Player player)
	{
		SaveInfoCapability.get(Objects.requireNonNull(player.getServer())).createOrEditStage(Objects.requireNonNull(player.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension))), stageId, corner1, corner2, stageName);
		SplatcraftPacketHandler.sendToPlayer(new NotifyStageCreatePacket(stageId), (ServerPlayer) player);
	}
}
