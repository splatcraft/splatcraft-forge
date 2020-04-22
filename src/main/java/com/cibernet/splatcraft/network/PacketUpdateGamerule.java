package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketUpdateGamerule implements IMessage
{

    private boolean messageValid;
    NBTTagCompound ruleNBT;

    public PacketUpdateGamerule() {messageValid = false;}
    public PacketUpdateGamerule(NBTTagCompound ruleNBT)
    {
        messageValid = true;
        this.ruleNBT = ruleNBT;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            ruleNBT = ByteBufUtils.readTag(buf);
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
        ByteBufUtils.writeTag(buf, ruleNBT);
    }

    public static class Handler implements IMessageHandler<PacketUpdateGamerule, IMessage>
    {

        @Override
        public IMessage onMessage(PacketUpdateGamerule message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketUpdateGamerule message, MessageContext ctx)
        {
            try
            {
                SplatCraftGamerules.readFromNBT(message.ruleNBT);
            } catch (Exception e)
            {
                SplatCraft.logger.info(e.toString());
            }
        }
    }
}
