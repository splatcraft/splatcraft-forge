package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.items.ICharge;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketReturnChargeRelease implements IMessage
{

    private boolean messageValid;
    private float charge;
    private ItemStack stack;
    
    public PacketReturnChargeRelease()
    {
        messageValid = false;
    }

    public PacketReturnChargeRelease(float charge, ItemStack stack)
    {
        messageValid = true;
        this.charge = charge;
        this.stack = stack;
    }
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            charge = buf.readFloat();
            stack = ByteBufUtils.readItemStack(buf);
            
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
        buf.writeFloat(charge);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static class Handler implements IMessageHandler<PacketReturnChargeRelease, IMessage>
    {

        @Override
        public IMessage onMessage(PacketReturnChargeRelease message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketReturnChargeRelease message, MessageContext ctx)
        {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack stack = message.stack;
            SplatCraftPlayerData.setWeaponCharge(player, stack, message.charge);
            
            System.out.println("return " + stack);
            
            if(stack.getItem() instanceof ICharge)
                ((ICharge) stack.getItem()).onRelease(player.world, player, stack);
            
        }
    }
}
