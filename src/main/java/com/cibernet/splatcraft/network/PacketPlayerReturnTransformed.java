package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketPlayerReturnTransformed implements IMessage
{

    private boolean messageValid;

    private UUID player;
    private boolean isTransformed;

    public PacketPlayerReturnTransformed() {messageValid = false;}

    public PacketPlayerReturnTransformed(UUID player, boolean isTransformed)
    {
        messageValid = true;
        this.isTransformed = isTransformed;
        this.player = player;
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
            SplatCraft.logger.info(e.toString());
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
        //ByteBufUtils.writeUTF8String(buf, transformedFieldName);
    }

    public static class Handler implements IMessageHandler<PacketPlayerReturnTransformed, IMessage>
    {

        @Override
        public IMessage onMessage(PacketPlayerReturnTransformed message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketPlayerReturnTransformed message, MessageContext ctx)
        {
            try
            {
               if(SplatCraftPlayerData.getPlayerData(message.player).isSquid < 1 == message.isTransformed)
                    SplatCraftPlayerData.getPlayerData(message.player).isSquid = message.isTransformed ? 2 : 1;

            } catch (Exception e)
            {
            }
        }
    }
}
