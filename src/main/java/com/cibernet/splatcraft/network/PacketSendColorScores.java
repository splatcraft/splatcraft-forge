package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public class PacketSendColorScores implements IMessage
{

    private boolean messageValid;
    Integer[] colors;
    Float[] scores;

    public PacketSendColorScores() {messageValid = false;}
    public PacketSendColorScores(Integer[] colors, Float[] scores)
    {
        messageValid = true;
        this.colors = colors;
        this.scores = scores;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            ArrayList<Integer> colorList = new ArrayList<>();
            ArrayList<Float> scoreList = new ArrayList<>();
            while(buf.readableBytes() > 0)
            {
                colorList.add(buf.readInt());
                scoreList.add(buf.readFloat());
            }
            
            colors = colorList.toArray(new Integer[colorList.size()]);
            scores = scoreList.toArray(new Float[scoreList.size()]);
    
        } catch (IndexOutOfBoundsException e)
        {
            SplatCraft.logger.info(e.toString());
        }
        messageValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if(!messageValid)
            return;
        for(int color : colors)
            buf.writeInt(color);
        for(float score : scores)
            buf.writeFloat(score);
    }

    public static class Handler implements IMessageHandler<PacketSendColorScores, IMessage>
    {

        @Override
        public IMessage onMessage(PacketSendColorScores message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketSendColorScores message, MessageContext ctx)
        {
            //try
            {
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                
                int winner = -1;
                int winnerScore = -1;
                
                for(int i = 0; i < message.colors.length; i++)
                {
                    player.sendMessage(new TextComponentString(I18n.format("commands.turfWar.score", SplatCraftUtils.getColorName(message.colors[i]), String.format("%.1f",message.scores[i]))));
                    if( winnerScore < message.scores[i])
                        winner = message.colors[i];
                }
    
                
                if(winner != -1)
                    player.sendMessage(new TextComponentTranslation("commands.turfWar.winner", SplatCraftUtils.getColorName(winner)));
                
            } /*catch (Exception e)
            {
                SplatCraft.logger.info(e.toString());
            }*/
        }
    }
}
