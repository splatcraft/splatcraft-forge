package net.splatcraft.forge.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.client.gui.stagepad.StageSelectionScreen;
import net.splatcraft.forge.commands.SuperJumpCommand;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;

import java.util.ArrayList;
import java.util.List;

public class SendStageWarpDataToPadPacket extends PlayS2CPacket
{
	final List<String> validStages;
	final List<String> outOfReachStages;

	public SendStageWarpDataToPadPacket(List<String> validStages, List<String> outOfReachStages)
	{
		this.validStages = validStages;
		this.outOfReachStages = outOfReachStages;
	}


	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeInt(validStages.size());
		for (String stageId : validStages) {
			buffer.writeUtf(stageId);
		}
		buffer.writeInt(outOfReachStages.size());
		for (String stageId : outOfReachStages) {
			buffer.writeUtf(stageId);
		}
	}

	public static SendStageWarpDataToPadPacket compile(Player player)
	{
		ArrayList<Stage> stages = Stage.getAllStages(player.level);
		stages.removeIf(stage -> !stage.hasSpawnPads());

		ArrayList<Stage> outOfRange = new ArrayList<>();

		for (Stage stage : stages)
		{
			ArrayList<SpawnPadTileEntity> validPads = stage.getAllSpawnPads();
			validPads.removeIf(pad -> !SuperJumpCommand.canSuperJumpTo(player, pad.getSuperJumpPos()));

			if(validPads.isEmpty())
				outOfRange.add(stage);
		}

		stages.removeIf(outOfRange::contains);

		return new SendStageWarpDataToPadPacket(stages.stream().map(stage -> stage.id).toList(), outOfRange.stream().map(stage -> stage.id).toList());
	}

	public static SendStageWarpDataToPadPacket decode(FriendlyByteBuf buffer)
	{
		int validStageCount = buffer.readInt();
		ArrayList<String> validStages = new ArrayList<>();

		for(int i = 0; i < validStageCount; i++)
			validStages.add(buffer.readUtf());

		int outOfReachStageCount = buffer.readInt();
		ArrayList<String> outOfReachStages = new ArrayList<>();

		for(int i = 0; i < outOfReachStageCount; i++)
			outOfReachStages.add(buffer.readUtf());

		return new SendStageWarpDataToPadPacket(validStages, outOfReachStages);
	}

	@Override
	public void execute()
	{
		StageSelectionScreen.updateValidSuperJumpsList(validStages, outOfReachStages);
	}
}
