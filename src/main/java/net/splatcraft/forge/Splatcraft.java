package net.splatcraft.forge;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.handlers.client.ClientSetupHandler;
import net.splatcraft.forge.handlers.client.SplatcraftKeyHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftCapabilities;
import net.splatcraft.forge.registries.SplatcraftCommands;
import net.splatcraft.forge.registries.SplatcraftEntities;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftStats;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.world.gen.SplatcraftOreGen;

import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Splatcraft.MODID)
public class Splatcraft {
    public static final String MODID = "splatcraft";
    public static final String MODNAME = "Splatcraft";
    public static final String SHORT = "SC";
    public static String version;

    public Splatcraft() {
        for (ModInfo m : ModList.get().getMods()) { // Forge is stupid
            if (Objects.equals(m.getModId(), MODID) && m.getVersion() != null) {
                version = m.getVersion().toString();
                break;
            }
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SplatcraftConfig.clientConfig);
        SplatcraftConfig.loadConfig(SplatcraftConfig.clientConfig, FMLPaths.CONFIGDIR.get().resolve(Splatcraft.MODID + "-client.toml").toString());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        SplatcraftCapabilities.registerCapabilities();
        SplatcraftPacketHandler.registerMessages();

        event.enqueueWork(() ->
        {
            SplatcraftEntities.setEntityAttributes();
            SplatcraftGameRules.registerGamerules();
        });

        SplatcraftTags.register();
        SplatcraftStats.register();
        ScoreboardHandler.register();
        SplatcraftCommands.registerArguments();

        SplatcraftOreGen.registerOres();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        SplatcraftEntities.bindRenderers();
        SplatcraftKeyHandler.registerKeys();
        SplatcraftBlocks.setRenderLayers();
        SplatcraftTileEntitites.bindTESR();


        event.enqueueWork(() ->
        {
            SplatcraftItems.registerModelProperties();
            SplatcraftItems.registerArmorModels();
            ClientSetupHandler.bindScreenContainers();
        });
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event)
    {
        SplatcraftGameRules.booleanRules.replaceAll((k, v) -> event.getServer().getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(k)));
        SplatcraftGameRules.intRules.replaceAll((k, v) -> event.getServer().getGameRules().getInt(SplatcraftGameRules.getRuleFromIndex(k)));
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
        }
    }
}
