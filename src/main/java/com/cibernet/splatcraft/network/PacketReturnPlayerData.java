package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketReturnPlayerData implements IMessage
{

    private boolean messageValid;

    private UUID player;
    private NBTTagCompound playerData;

    public PacketReturnPlayerData() {messageValid = false;}

    public PacketReturnPlayerData(UUID player, NBTTagCompound playerData)
    {
        messageValid = true;
        this.player = player;
        this.playerData = playerData;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            playerData = ByteBufUtils.readTag(buf);
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
        ByteBufUtils.writeUTF8String(buf, player.toString());
        ByteBufUtils.writeTag(buf, playerData);
    }

    public static class Handler implements IMessageHandler<PacketReturnPlayerData, IMessage>
    {

        @Override
        public IMessage onMessage(PacketReturnPlayerData message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketReturnPlayerData message, MessageContext ctx)
        {
            try
            {
                SplatCraftPlayerData.readFromNBT(message.playerData);

            } catch (Exception e)
            {
                SplatCraft.logger.info(e.toString());
            }
        }
    }
}
