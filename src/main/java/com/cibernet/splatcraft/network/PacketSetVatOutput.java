package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.recipes.RecipesInkwellVat;
import com.cibernet.splatcraft.tileentities.TileEntityInkwellVat;
import com.cibernet.splatcraft.utils.InkColors;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketSetVatOutput implements IMessage
{

    private boolean messageValid;
    BlockPos pos;
    ItemStack recipe;
    int selected;

    public PacketSetVatOutput() {messageValid = false;}

    public PacketSetVatOutput(TileEntityInkwellVat te, ItemStack recipe, int selected)
    {
        messageValid = true;
        pos = te.getPos();
        this.recipe = recipe;
        this.selected = selected;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            pos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
            recipe = ByteBufUtils.readItemStack(buf);
            selected = buf.readInt();
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
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        ByteBufUtils.writeItemStack(buf, recipe);
        buf.writeInt(selected);
    }

    public static class Handler implements IMessageHandler<PacketSetVatOutput, IMessage>
    {

        @Override
        public IMessage onMessage(PacketSetVatOutput message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketSetVatOutput message, MessageContext ctx)
        {
            try
            {
                TileEntityInkwellVat te = (TileEntityInkwellVat) ctx.getServerHandler().player.world.getTileEntity(message.pos);

                //if(message.selected >= 0 && message.selected < RecipesInkwellVat.getOutput(message.recipe).size())
                te.setOutput(message.selected);


            } catch (Exception e)
            {
            }
        }
    }
}
