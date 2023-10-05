package net.splatcraft.forge.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SeaPickleFeature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.worldgen.features.CrateFeature;
import net.splatcraft.forge.worldgen.features.SardiniumDepositFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class SplatcraftOreGen
{
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, Splatcraft.MODID);

    public static final RegistryObject<Feature<CountConfiguration>> crate_feature = REGISTRY.register("crate", () -> new CrateFeature(CountConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> sardinium_deposit_feature = REGISTRY.register("sardinium_deposit", () -> new SardiniumDepositFeature(NoneFeatureConfiguration.CODEC));

    private static final ArrayList<Holder<PlacedFeature>> overworldGen = new ArrayList<>();
    private static final ArrayList<Holder<PlacedFeature>> beachGen = new ArrayList<>();
    private static final ArrayList<Holder<PlacedFeature>> oceanGen = new ArrayList<>();

    public static void registerOres()
    {
        Holder<ConfiguredFeature<CountConfiguration, ?>> crate_small = FeatureUtils.register(Splatcraft.MODID + ":crate_small", crate_feature.get(), new CountConfiguration(8));
        Holder<ConfiguredFeature<CountConfiguration, ?>> crate_large = FeatureUtils.register(Splatcraft.MODID + ":crate_large", crate_feature.get(), new CountConfiguration(12));

        Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> sardinium_deposit = FeatureUtils.register(Splatcraft.MODID + ":sardinium_deposit", sardinium_deposit_feature.get(), new NoneFeatureConfiguration());

        oceanGen.add(PlacementUtils.register(Splatcraft.MODID + ":crate_small", crate_small, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
        oceanGen.add(PlacementUtils.register(Splatcraft.MODID + ":crate_large", crate_large, RarityFilter.onAverageOnceEvery(28), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
        oceanGen.add(PlacementUtils.register(Splatcraft.MODID + ":sardinium_deposit", sardinium_deposit, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event)
    {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();

        switch(event.getCategory())
        {
            case OCEAN -> generation.getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).addAll(oceanGen);
            case BEACH -> generation.getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).addAll(beachGen);
        }
    }


    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return Arrays.asList(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int veinsPerChunk, PlacementModifier modifier) {
        return orePlacement(CountPlacement.of(veinsPerChunk), modifier);
    }

    private static List<PlacementModifier> rareOrePlacement(int chunksPerVein, PlacementModifier modifier) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chunksPerVein), modifier);
    }
}
