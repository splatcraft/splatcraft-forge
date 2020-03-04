package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketPlayerReturnColor implements IMessage
{

    private boolean messageValid;

    private UUID player;
    private int color;

    public PacketPlayerReturnColor() {messageValid = false;}

    public PacketPlayerReturnColor(UUID player, int color)
    {
        messageValid = true;
        this.color = color;
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            color = buf.readInt();
            player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        } catch (IndexOutOfBoundsException e)
        {
            //Logger goes here
            System.out.println("something went wrong! " + e);
        }
        messageValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if(!messageValid)
            return;
        buf.writeInt(color);
        ByteBufUtils.writeUTF8String(buf, player.toString());
        //ByteBufUtils.writeUTF8String(buf, transformedFieldName);
    }

    public static class Handler implements IMessageHandler<PacketPlayerReturnColor, IMessage>
    {

        @Override
        public IMessage onMessage(PacketPlayerReturnColor message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketPlayerReturnColor message, MessageContext ctx)
        {
            try
            {
                SplatCraftPlayerData.getPlayerData(message.player).inkColor = message.color;

            } catch (Exception e)
            {
                //logger goes here
            }
        }
    }
}
