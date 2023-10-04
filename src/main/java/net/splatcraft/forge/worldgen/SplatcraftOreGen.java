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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class SplatcraftOreGen
{
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, Splatcraft.MODID);

    public static final RegistryObject<Feature<CountConfiguration>> crate_feature = REGISTRY.register("crate", () -> new CrateFeature(CountConfiguration.CODEC));

    private static final ArrayList<Holder<PlacedFeature>> overworldGen = new ArrayList<>();
    private static final ArrayList<Holder<PlacedFeature>> beachGen = new ArrayList<>();
    private static final ArrayList<Holder<PlacedFeature>> oceanGen = new ArrayList<>();

    public static void registerOres()
    {
        OreConfiguration.TargetBlockState sardiniumTarget = OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, SplatcraftBlocks.sardiniumOre.get().defaultBlockState());

        Holder<ConfiguredFeature<OreConfiguration, ?>> sardinium_small = FeatureUtils.register("ore_sardinium_small", Feature.ORE, new OreConfiguration(Arrays.asList(sardiniumTarget), 6));
        Holder<ConfiguredFeature<OreConfiguration, ?>> sardinium = FeatureUtils.register("ore_sardinium", Feature.ORE, new OreConfiguration(Arrays.asList(sardiniumTarget), 12));

        Holder<ConfiguredFeature<CountConfiguration, ?>> crate_small = FeatureUtils.register("crate_small", crate_feature.get(), new CountConfiguration(8));
        Holder<ConfiguredFeature<CountConfiguration, ?>> crate_large = FeatureUtils.register("crate_large", crate_feature.get(), new CountConfiguration(12));

        oceanGen.add(PlacementUtils.register("crate_small", crate_small, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
        oceanGen.add(PlacementUtils.register("crate_large", crate_large, RarityFilter.onAverageOnceEvery(28), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        beachGen.add(PlacementUtils.register("ore_sardinium_beach", sardinium_small, commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(40)))));
        oceanGen.add(PlacementUtils.register("ore_sardinium_ocean", sardinium, commonOrePlacement(24, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60)))));
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
