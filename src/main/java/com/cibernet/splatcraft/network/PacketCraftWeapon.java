package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.recipes.RecipeSubtype;
import com.cibernet.splatcraft.recipes.RecipesWeaponStation;
import com.cibernet.splatcraft.registries.SplatCraftStats;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketCraftWeapon implements IMessage
{

    private boolean messageValid;
    RecipeSubtype recipe;

    public PacketCraftWeapon() {messageValid = false;}
    public PacketCraftWeapon(RecipeSubtype recipe)
    {
        messageValid = true;
        this.recipe = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            recipe = RecipesWeaponStation.getRecipeByID(buf.readInt());
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
        buf.writeInt(RecipesWeaponStation.getRecipeID(recipe));
    }

    public static class Handler implements IMessageHandler<PacketCraftWeapon, IMessage>
    {
    
        @Override
        public IMessage onMessage(PacketCraftWeapon message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.SERVER)
                return null;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> process(message, ctx));
            return null;
        }
    
        void process(PacketCraftWeapon message, MessageContext ctx)
        {
            EntityPlayer player = ctx.getServerHandler().player;
            RecipeSubtype recipe = message.recipe;
    
    
            for(int i = 0; i < recipe.getIngredients().size(); i++)
            {
                RecipesWeaponStation.getItem(player, recipe.getIngredients().get(i), true);
            }
    
            SplatCraftUtils.giveItem(player, recipe.getOutput().copy());
            player.addStat(SplatCraftStats.WEAPONS_CRAFTED);
        }
    }
}
