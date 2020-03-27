package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemWeaponBase;
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

public class PacketWeaponLeftClick implements IMessage
{

    private boolean messageValid;

    private UUID player;

    public PacketWeaponLeftClick() {messageValid = false;}

    public PacketWeaponLeftClick(UUID player)
    {
        messageValid = true;
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
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
        ByteBufUtils.writeUTF8String(buf, player.toString());
        //ByteBufUtils.writeUTF8String(buf, transformedFieldName);
    }

    public static class Handler implements IMessageHandler<PacketWeaponLeftClick, IMessage>
    {

        @Override
        public IMessage onMessage(PacketWeaponLeftClick message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketWeaponLeftClick message, MessageContext ctx)
        {
            try
            {
                EntityPlayer player = ctx.getServerHandler().player;
                ItemStack stack = player.getHeldItemMainhand();

                if(stack.getItem() instanceof ItemWeaponBase)
                    ((ItemWeaponBase)stack.getItem()).onItemLeftClick(player.world, player, stack);

            } catch (Exception e)
            {
            }
        }
    }
}
