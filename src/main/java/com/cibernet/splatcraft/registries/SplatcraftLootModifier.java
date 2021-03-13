package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.loot.BaseLootModifier;
import com.cibernet.splatcraft.loot.ChestLootModifier;
import com.cibernet.splatcraft.loot.FishingLootModifier;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = Splatcraft.MODID)
public class SplatcraftLootModifier
{
    @SubscribeEvent
    public static void registerGLM(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event)
    {
        IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();

        registry.register(new FishingLootModifier.Serializer().setRegistryName(new ResourceLocation(Splatcraft.MODID, "fishing")));
        registry.register(new ChestLootModifier.Serializer().setRegistryName(new ResourceLocation(Splatcraft.MODID, "chest_loot")));
    }
}

