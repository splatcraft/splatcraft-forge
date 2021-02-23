package com.cibernet.splatcraft;

import com.cibernet.splatcraft.handlers.client.SplatcraftKeyHandler;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber
public class SplatcraftConfig
{
    private static final ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec clientConfig;

    static
    {
        Client.init(clientBuilder);
        clientConfig = clientBuilder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path)
    {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();

        file.load();
        config.setConfig(file);
    }

    public static class Client
    {
        public static ForgeConfigSpec.EnumValue<SplatcraftKeyHandler.KeyMode> squidKeyMode;
        public static ForgeConfigSpec.BooleanValue dynamicInkDurability;
        public static ForgeConfigSpec.BooleanValue holdBarrierToRender;
        //public static ForgeConfigSpec.BooleanValue colorLock; TODO

        public static void init(ForgeConfigSpec.Builder client)
        {
            client.comment("Accessibility Settings");
            squidKeyMode = client.comment("Squid Key Mode").defineEnum("splatcraft.squidKeyMode", SplatcraftKeyHandler.KeyMode.TOGGLE);
            dynamicInkDurability = client.comment("Determines whether the durability bar on Splatcraft weapons that determines how much ink you have left matches its ink color or not")
                    .define("splatcraft.dynamicInkDurabilityColor", true);
            holdBarrierToRender = client.comment("Prevents Stage Barriers from rendering in creative mode unless the player is holding one in their hand.")
                    .define("splatcraft.holdBarrierToRender", false);
            //colorLock = client.comment("Color Lock Mode").define("splatcraft.colorLock", false);
        }

        public static boolean getColorLock() {return false;}
    }

}
