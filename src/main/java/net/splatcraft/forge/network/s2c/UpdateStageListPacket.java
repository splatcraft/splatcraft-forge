package net.splatcraft.forge.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.commands.StageCommand;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfo;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.util.ClientUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UpdateStageListPacket extends PlayToClientPacket
{
	CompoundNBT nbt;

	public UpdateStageListPacket(CompoundNBT nbt)
	{
		this.nbt = nbt;
	}

	public UpdateStageListPacket(HashMap<String, Stage> stages)
	{
		CompoundNBT stageNbt = new CompoundNBT();

		for(Map.Entry<String, Stage> e : stages.entrySet())
			stageNbt.put(e.getKey(), e.getValue().writeData());

		nbt = stageNbt;
	}

	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeNbt(nbt);
	}
	public static UpdateStageListPacket decode(PacketBuffer buffer)
	{
		return new UpdateStageListPacket(buffer.readNbt());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void execute()
	{
		ClientUtils.clientStages.clear();
		for(String key : nbt.getAllKeys())
			ClientUtils.clientStages.put(key,new Stage(nbt.getCompound(key)));
	}
}
