package net.splatcraft.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.resource.PathResourcePack;
import net.splatcraft.forge.client.handlers.ClientSetupHandler;
import net.splatcraft.forge.client.handlers.SplatcraftKeyHandler;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.registries.*;
import net.splatcraft.forge.world.gen.SplatcraftOreGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Splatcraft.MODID)
public class Splatcraft {
    public static final String MODID = "splatcraft";
    public static final String MODNAME = "Splatcraft";
    public static String version;
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public Splatcraft() {
        for (IModInfo m : ModList.get().getMods()) { // Forge is stupid
            if (Objects.equals(m.getModId(), MODID) && m.getVersion() != null)
            {
                version = m.getVersion().toString();
                break;
            }
        }

        SplatcraftRegisties.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SplatcraftConfig.clientConfig);
        SplatcraftConfig.loadConfig(SplatcraftConfig.clientConfig, FMLPaths.CONFIGDIR.get().resolve(Splatcraft.MODID + "-client.toml").toString());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupBuiltInPacks);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(FMLJavaModLoadingContext.get().getModEventBus());

        //addBuiltinPack("classic_weapons", new TextComponent("Splatcraft - Classic Weapons"));

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        SplatcraftPacketHandler.registerMessages();

        SplatcraftGameRules.registerGamerules();
        SplatcraftTags.register();
        SplatcraftStats.register();
        ScoreboardHandler.register();
        SplatcraftCommands.registerArguments();

        SplatcraftOreGen.registerOres();
        SplatcraftItems.registerDispenserBehavior();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        SplatcraftEntities.bindRenderers();
        SplatcraftKeyHandler.registerKeys();
        SplatcraftBlocks.setRenderLayers();
        SplatcraftTileEntities.bindTESR();

        event.enqueueWork(() ->
        {
            SplatcraftItems.registerModelProperties();
            ClientSetupHandler.bindScreenContainers();
        });
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        SplatcraftGameRules.booleanRules.replaceAll((k, v) -> event.getServer().getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(k)));
        SplatcraftGameRules.intRules.replaceAll((k, v) -> event.getServer().getGameRules().getInt(SplatcraftGameRules.getRuleFromIndex(k)));
    }

    private static final ArrayList<ResourcePack> BUILTIN_PACKS = new ArrayList<>();
    public void setupBuiltInPacks(AddPackFindersEvent event)
    {
        if(event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            for(ResourcePack pack : BUILTIN_PACKS)
                try {
                Path path = ModList.get().getModFileById(MODID).getFile().findResource("resourcepacks/" + pack.folder);
                PathResourcePack packPath = new PathResourcePack(ModList.get().getModFileById(MODID).getFile().getFileName() + ":" + path, path);
                PackMetadataSection section = packPath.getMetadataSection(PackMetadataSection.SERIALIZER);

                if(section != null)
                {
                    event.addRepositorySource((packConsumer, packConstructor) -> packConsumer.accept(packConstructor.create(
                            "builtin/" + Splatcraft.MODID, pack.displayName, false,
                            () -> packPath, section, Pack.Position.BOTTOM, PackSource.BUILT_IN, false
                    )));
                }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    record ResourcePack(String folder, Component displayName){}

    public void addBuiltinPack(String folder, Component displayName)
    {
        BUILTIN_PACKS.add(new ResourcePack(folder, displayName));
    }
}
