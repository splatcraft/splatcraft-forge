package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateStageListPacket;

import java.util.HashMap;

public class RequestSetStageRulePacket extends PlayC2SPacket
{
	final String stageId;
	final String ruleId;
	final Boolean value;

	public RequestSetStageRulePacket(String stageId, String ruleId, Boolean value)
	{
		this.stageId = stageId;
		this.ruleId = ruleId;
		this.value = value;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeInt(value == null ? 0 : value ? 1 : 2);
		buffer.writeUtf(stageId);
		buffer.writeUtf(ruleId);
	}

	public static RequestSetStageRulePacket decode(FriendlyByteBuf buffer)
	{
		int valueIndex = buffer.readInt();
		return new RequestSetStageRulePacket(buffer.readUtf(), buffer.readUtf(), valueIndex == 0 ? null : valueIndex == 1);
	}

	@Override
	public void execute(Player player)
	{

		HashMap<String, Stage> stages = SaveInfoCapability.get(player.getServer()).getStages();

		Stage stage = stages.get(stageId);
		stage.applySetting(ruleId.replace(Splatcraft.MODID + ".", ""), value);

		SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));
	}
}
