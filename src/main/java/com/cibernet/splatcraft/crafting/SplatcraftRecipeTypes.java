package com.cibernet.splatcraft.crafting;


import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

public class SplatcraftRecipeTypes
{
    public static final IRecipeSerializer<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING = new InkVatColorRecipe.InkVatColorSerializer("ink_vat_color");
    public static final IRecipeSerializer<WeaponWorkbenchTab> WEAPON_STATION_TAB = new WeaponWorkbenchTab.WeaponWorkbenchTabSerializer("weapon_workbench_tab");
    public static final IRecipeSerializer<WeaponWorkbenchRecipe> WEAPON_STATION = new WeaponWorkbenchRecipe.Serializer("weapon_workbench");
    public static final SpecialRecipeSerializer<SingleUseSubRecipe> SINGLE_USE_SUB = new SpecialRecipeSerializer<>(SingleUseSubRecipe::new);
    public static IRecipeType<AbstractWeaponWorkbenchRecipe> WEAPON_STATION_TYPE;
    public static IRecipeType<WeaponWorkbenchTab> WEAPON_STATION_TAB_TYPE;
    public static IRecipeType<InkVatColorRecipe> INK_VAT_COLOR_CRAFTING_TYPE;

    public static boolean getItem(PlayerEntity player, Ingredient ingredient, int count, boolean takeItems)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack invStack = player.inventory.getStackInSlot(i);
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
        public static void registerSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event)
        {
            IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

            INK_VAT_COLOR_CRAFTING_TYPE = IRecipeType.register(Splatcraft.MODID + ":ink_vat_color");
            WEAPON_STATION_TAB_TYPE = IRecipeType.register(Splatcraft.MODID + ":weapon_workbench_tab");
            WEAPON_STATION_TYPE = IRecipeType.register(Splatcraft.MODID + ":weapon_workbench");

            registry.register(INK_VAT_COLOR_CRAFTING);
            registry.register(WEAPON_STATION_TAB);
            registry.register(WEAPON_STATION);
            registry.register(SINGLE_USE_SUB.setRegistryName(Splatcraft.MODID, "single_use_sub"));
        }
    }


}
