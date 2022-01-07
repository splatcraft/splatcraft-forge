package com.cibernet.splatcraft.level.gen;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class SplatcraftOreGen
{
    private static final ArrayList<ConfiguredFeature<?, ?>> overlevelGen = new ArrayList<>();
    private static final ArrayList<ConfiguredFeature<?, ?>> beachGen = new ArrayList<>();
    private static final ArrayList<ConfiguredFeature<?, ?>> oceanGen = new ArrayList<>();

    public static void registerOres()
    {
        beachGen.add(register("sardinium", Feature.ORE.configured(
                new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, SplatcraftBlocks.sardiniumOre.defaultBlockState(), 6))
                .range(40).countRandom(8)));

        oceanGen.add(register("sardinium_ocean", Feature.ORE.configured(
                new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, SplatcraftBlocks.sardiniumOre.defaultBlockState(), 12))
                .range(60).countRandom(8)));
    }

    protected static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String name, ConfiguredFeature<FC, ?> feature)
    {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Splatcraft.MODID, name), feature);
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event)
    {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();

        if (!event.getCategory().equals(Biome.Category.NETHER) && !event.getCategory().equals(Biome.Category.THEEND))
        {

            for (ConfiguredFeature<?, ?> gen : event.getCategory().equals(Biome.Category.OCEAN) ? oceanGen :
                    event.getCategory().equals(Biome.Category.BEACH) ? beachGen : overlevelGen)
            {
                if (gen != null)
                {
                    generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, gen);
                }
            }
        }
    }
}
