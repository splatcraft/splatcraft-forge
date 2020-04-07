package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketChargeRelease implements IMessage
{

    private boolean messageValid;
    private ItemStack stack;
    private UUID player;

    public PacketChargeRelease() {messageValid = false;}
    public PacketChargeRelease(UUID player, ItemStack stack)
    {
        messageValid = true;
        this.player = player;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            stack = ByteBufUtils.readItemStack(buf);
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
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static class Handler implements IMessageHandler<PacketChargeRelease, IMessage>
    {

        @Override
        public IMessage onMessage(PacketChargeRelease message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketChargeRelease message, MessageContext ctx)
        {
            try
            {
                float charge = SplatCraftPlayerData.getWeaponCharge(message.player, message.stack);
                if(charge > 0.05f)
                {
                    SplatCraftPacketHandler.instance.sendToServer(new PacketReturnChargeRelease(charge, message.stack));
                    SplatCraftPlayerData.setWeaponCharge(message.player, message.stack, 0);
                }
                SplatCraftPlayerData.setCanDischarge(message.player, true);
            } catch (Exception e)
            {
                SplatCraft.logger.info(e.toString());
            }
        }
    }
}
