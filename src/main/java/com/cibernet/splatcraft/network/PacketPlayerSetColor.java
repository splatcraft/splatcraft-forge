package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketPlayerSetColor implements IMessage
{

    private boolean messageValid;

    private UUID player;
    private int color;

    public PacketPlayerSetColor()
    {
        messageValid = false;
    }

    public PacketPlayerSetColor(UUID player, int color)
    {
        messageValid = true;
        this.player = player;
        this.color = color;
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
    }

    public static class Handler implements IMessageHandler<PacketPlayerSetColor, IMessage>
    {

        @Override
        public IMessage onMessage(PacketPlayerSetColor message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketPlayerSetColor message, MessageContext ctx)
        {
            SplatCraftPlayerData.getPlayerData(message.player).inkColor = message.color;
            SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnColor(message.player, message.color), ctx.getServerHandler().player.dimension);

        }
    }
}
