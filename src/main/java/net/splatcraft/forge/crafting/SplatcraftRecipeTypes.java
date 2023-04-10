package net.splatcraft.forge.crafting;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.splatcraft.forge.Splatcraft;

public class SplatcraftRecipeTypes
{
    public static final RecipeSerializer<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING = new InkVatColorRecipe.InkVatColorSerializer("ink_vat_color");
    public static final RecipeSerializer<WeaponWorkbenchTab> WEAPON_STATION_TAB = new WeaponWorkbenchTab.WeaponWorkbenchTabSerializer("weapon_workbench_tab");
    public static final RecipeSerializer<WeaponWorkbenchRecipe> WEAPON_STATION = new WeaponWorkbenchRecipe.Serializer("weapon_workbench");
    public static final RecipeSerializer<SingleUseSubRecipe> SINGLE_USE_SUB = new SimpleRecipeSerializer<>(SingleUseSubRecipe::new);
    public static final RecipeSerializer<ShapedRecipe> COLORED_SHAPED_CRAFTING = new ColoredShapedRecipe.Serializer("colored_crafting_shaped");
    public static RecipeType<AbstractWeaponWorkbenchRecipe> WEAPON_STATION_TYPE;
    public static RecipeType<WeaponWorkbenchTab> WEAPON_STATION_TAB_TYPE;
    public static RecipeType<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING_TYPE;

    public static boolean getItem(Player player, Ingredient ingredient, int count, boolean takeItems)
    {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!takeItems)
            {
                invStack = invStack.copy();
            }

            if (ingredient.test(invStack))
            {
                if (count > invStack.getCount())
                {
                    count -= invStack.getCount();
                    invStack.setCount(0);
                } else
                {
                    invStack.setCount(invStack.getCount() - count);
                    return true;
                }
            }
        }
        return false;
    }

    @Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Subscriber
    {
        @SubscribeEvent
        public static void registerSerializers(final RegistryEvent.Register<RecipeSerializer<?>> event)
        {
            IForgeRegistry<RecipeSerializer<?>> registry = event.getRegistry();

            INK_VAT_COLOR_CRAFTING_TYPE = RecipeType.register(Splatcraft.MODID + ":ink_vat_color");
            WEAPON_STATION_TAB_TYPE = RecipeType.register(Splatcraft.MODID + ":weapon_workbench_tab");
            WEAPON_STATION_TYPE = RecipeType.register(Splatcraft.MODID + ":weapon_workbench");

            registry.register(INK_VAT_COLOR_CRAFTING);
            registry.register(WEAPON_STATION_TAB);
            registry.register(WEAPON_STATION);
            registry.register(COLORED_SHAPED_CRAFTING);
            registry.register(SINGLE_USE_SUB.setRegistryName(new ResourceLocation(Splatcraft.MODID, "single_use_sub")));
        }
    }


}
