package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.loot.ChestLootModifier;
import com.cibernet.splatcraft.loot.FishingLootModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Splatcraft.MODID)
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
