package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ItemDualieBase;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.recipes.RecipeSubtype;
import com.cibernet.splatcraft.recipes.RecipesWeaponStation;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDodgeRoll implements IMessage
{

    private boolean messageValid;
    boolean useOffhandValues;

    public PacketDodgeRoll() {messageValid = false;}
    public PacketDodgeRoll(boolean useOffhandValues)
    {
        messageValid = true;
        this.useOffhandValues = useOffhandValues;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            useOffhandValues = buf.readBoolean();
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
        buf.writeBoolean(useOffhandValues);
    }

    public static class Handler implements IMessageHandler<PacketDodgeRoll, IMessage>
    {
    
        @Override
        public IMessage onMessage(PacketDodgeRoll message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }
    
        void process(PacketDodgeRoll message, MessageContext ctx)
        {
            EntityPlayer player = ctx.getServerHandler().player;
            CooldownTracker cooldownTracker = player.getCooldownTracker();
    
            
            ItemStack weapon = player.getActiveItemStack();
            
            if(weapon.getItem() instanceof ItemDualieBase)
            {
                ItemStack offhandStack = player.getHeldItem(player.getHeldItemMainhand().equals(weapon.getItem()) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                
                if(message.useOffhandValues)
                    weapon = offhandStack;
                
                ItemWeaponBase.reduceInk(player, ((ItemDualieBase) weapon.getItem()).rollConsumption);
                SplatCraftUtils.createInkExplosion(player.world, new BlockPos(player.posX, player.posY, player.posZ), 0.5f, ColorItemUtils.getInkColor(weapon));
            }
        }
    }
}
