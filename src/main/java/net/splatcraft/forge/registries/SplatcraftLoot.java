package net.splatcraft.forge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.loot.BlueprintLootFunction;
import net.splatcraft.forge.loot.ChestLootModifier;
import net.splatcraft.forge.loot.FishingLootModifier;

import static net.splatcraft.forge.Splatcraft.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Splatcraft.MODID)
public class SplatcraftLoot
{
    protected static final DeferredRegister<LootItemFunctionType> REGISTRY = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, MODID);

    public static final RegistryObject<LootItemFunctionType> BLUEPRINT = REGISTRY.register("blueprint_pool", () -> new LootItemFunctionType(new BlueprintLootFunction.Serializer()));


    @SubscribeEvent
    public static void registerGLM(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event)
    {
        IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();

        registry.register(new FishingLootModifier.Serializer().setRegistryName(new ResourceLocation(Splatcraft.MODID, "fishing")));
        registry.register(new ChestLootModifier.Serializer().setRegistryName(new ResourceLocation(Splatcraft.MODID, "chest_loot")));
    }
}
