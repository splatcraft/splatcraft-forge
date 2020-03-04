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

public class PacketPlayerSetTransformed implements IMessage
{

    private boolean messageValid;

    private UUID player;
    private boolean isTransformed;

    public PacketPlayerSetTransformed()
    {
        messageValid = false;
    }

    public PacketPlayerSetTransformed(UUID player, boolean isTransformed)
    {
        messageValid = true;
        this.player = player;
        this.isTransformed = isTransformed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            isTransformed = buf.readBoolean();
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
        buf.writeBoolean(isTransformed);
        ByteBufUtils.writeUTF8String(buf, player.toString());
    }

    public static class Handler implements IMessageHandler<PacketPlayerSetTransformed, IMessage>
    {

        @Override
        public IMessage onMessage(PacketPlayerSetTransformed message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketPlayerSetTransformed message, MessageContext ctx)
        {
            SplatCraftPlayerData.getPlayerData(message.player).isSquid = message.isTransformed;
            SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnTransformed(message.player, message.isTransformed), ctx.getServerHandler().player.dimension);

        }
    }
}
