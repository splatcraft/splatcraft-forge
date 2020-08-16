package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class SendColorScoresPacket extends PlayToClientPacket
{
	Integer[] colors;
	Float[] scores;
	int length;
	
	public SendColorScoresPacket(Integer[] colors, Float[] scores)
	{
		this.colors = colors;
		this.scores = scores;
		this.length = Math.min(colors.length, scores.length);
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		buffer.writeInt(length);
		for(int i = 0; i < length; i++)
		{
			buffer.writeInt(colors[i]);
			buffer.writeFloat(scores[i]);
		}
	}
	
	public static SendColorScoresPacket decode(PacketBuffer buffer)
	{
		ArrayList<Integer> colorList = new ArrayList<>();
		ArrayList<Float> scoreList = new ArrayList<>();
		int length = buffer.readInt();
		for(int i = 0; i < length; i++)
		{
			colorList.add(buffer.readInt());
			scoreList.add(buffer.readFloat());
		}
		
		return new SendColorScoresPacket(colorList.toArray(new Integer[colorList.size()]), scoreList.toArray(new Float[scoreList.size()]));
	}
	
	@Override
	public void execute()
	{
		PlayerEntity player = ClientUtils.getClientPlayer();
		int winner = -1;
		float winnerScore = -1;
		
		for(int i = 0; i < colors.length; i++)
		{
			player.sendStatusMessage(new TranslationTextComponent("status.scan_turf.score", ColorUtils.getFormatedColorName(colors[i], false), String.format("%.1f",scores[i])), false);
			if( winnerScore < scores[i])
			{
				winnerScore = scores[i];
				winner = colors[i];
			}
		}
		
		if(winner != -1)
			player.sendStatusMessage(new TranslationTextComponent("status.scan_turf.winner", ColorUtils.getFormatedColorName(winner, false)), false);
		
	}
	
}
