package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.splatcraft.forge.crafting.SplatcraftRecipeTypes;
import net.splatcraft.forge.crafting.StackedIngredient;
import net.splatcraft.forge.crafting.WeaponWorkbenchRecipe;
import net.splatcraft.forge.crafting.WeaponWorkbenchSubtypeRecipe;
import net.splatcraft.forge.registries.SplatcraftStats;

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

    public static CraftWeaponPacket decode(FriendlyByteBuf buffer)
    {
        return new CraftWeaponPacket(buffer.readResourceLocation(), buffer.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(recipeID);
        buffer.writeInt(subtype);
    }

    @Override
    public void execute(Player player)
    {
        Optional<? extends Recipe<?>> recipeOptional = player.level.getRecipeManager().byKey(recipeID);

        if (recipeOptional.isPresent() && recipeOptional.get() instanceof WeaponWorkbenchRecipe)
        {
            WeaponWorkbenchSubtypeRecipe recipe = ((WeaponWorkbenchRecipe) recipeOptional.get()).getRecipeFromIndex(player, subtype);
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

            if(!output.isEmpty())
            {
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
                player.awardStat(SplatcraftStats.WEAPONS_CRAFTED);
            }


        }
    }
}
