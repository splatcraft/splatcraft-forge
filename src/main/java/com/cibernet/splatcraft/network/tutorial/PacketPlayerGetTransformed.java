package com.cibernet.splatcraft.network.tutorial;

import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketPlayerGetTransformed implements IMessage
{

    private boolean messageValid;

    private UUID player;
    private boolean isTransformed;

    public PacketPlayerGetTransformed()
    {
        messageValid = false;
    }

    public PacketPlayerGetTransformed(UUID player, boolean isTransformed)
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

    public static class Handler implements IMessageHandler<PacketPlayerGetTransformed, IMessage>
    {

        @Override
        public IMessage onMessage(PacketPlayerGetTransformed message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketPlayerGetTransformed message, MessageContext ctx)
        {
            SplatCraftPacketHandler.instance.sendToDimension(new PacketPlayerReturnTransformed(message.player, message.isTransformed), ctx.getServerHandler().player.dimension);

        }
    }
}
