package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.crafting.SplatcraftRecipeTypes;
import com.cibernet.splatcraft.crafting.StackedIngredient;
import com.cibernet.splatcraft.crafting.WeaponWorkbenchRecipe;
import com.cibernet.splatcraft.crafting.WeaponWorkbenchSubtypeRecipe;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class CraftWeaponPacket extends PlayToServerPacket
{
    ResourceLocation recipeID;
    int subtype;

    public CraftWeaponPacket(ResourceLocation recipeID, int subtype)
    {
        this.recipeID = recipeID;
        this.subtype = subtype;
    }

    public static CraftWeaponPacket decode(PacketBuffer buffer)
    {
        return new CraftWeaponPacket(buffer.readResourceLocation(), buffer.readInt());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeResourceLocation(recipeID);
        buffer.writeInt(subtype);
    }

    @Override
    public void execute(PlayerEntity player)
    {
        Optional<? extends IRecipe<?>> recipeOptional = player.level.getRecipeManager().byKey(recipeID);

        if (recipeOptional.isPresent() && recipeOptional.get() instanceof WeaponWorkbenchRecipe)
        {
            WeaponWorkbenchSubtypeRecipe recipe = ((WeaponWorkbenchRecipe) recipeOptional.get()).getRecipeFromIndex(subtype);
            for (StackedIngredient ing : recipe.getInput())
            {
                if (!SplatcraftRecipeTypes.getItem(player, ing.getIngredient(), ing.getCount(), false))
                {
                    return;
                }
            }

            for (StackedIngredient ing : recipe.getInput())
            {
                SplatcraftRecipeTypes.getItem(player, ing.getIngredient(), ing.getCount(), true);
            }
            ItemStack output = recipe.getOutput().copy();
            if (!player.addItem(output))
            {
                ItemEntity item = player.drop(output, false);
                if (item != null)
                {
                    item.setNoPickUpDelay();
                }
            } else
            {
                player.containerMenu.broadcastChanges();
            }
        }
    }
}
