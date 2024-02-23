package net.splatcraft.forge.network.s2c;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.client.gui.stagepad.AbstractStagePadScreen;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.util.ClientUtils;

public class UpdateStageListPacket extends PlayS2CPacket
{
	CompoundTag nbt;

	public UpdateStageListPacket(CompoundTag nbt)
	{
		this.nbt = nbt;
	}

	public UpdateStageListPacket(HashMap<String, Stage> stages)
	{
		CompoundTag stageNbt = new CompoundTag();

		for(Map.Entry<String, Stage> e : stages.entrySet())
			stageNbt.put(e.getKey(), e.getValue().writeData());

		nbt = stageNbt;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeNbt(nbt);
	}
	public static UpdateStageListPacket decode(FriendlyByteBuf buffer)
	{
		return new UpdateStageListPacket(buffer.readNbt());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void execute()
	{
		ClientUtils.clientStages.clear();
		for(String key : nbt.getAllKeys())
			ClientUtils.clientStages.put(key,new Stage(nbt.getCompound(key), key));

		if(Minecraft.getInstance().screen instanceof AbstractStagePadScreen stagePadScreen)
			stagePadScreen.onStagesUpdate();
	}
}
