package net.splatcraft.forge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.client.handlers.SplatcraftKeyHandler;

import java.io.File;

@Mod.EventBusSubscriber
public class SplatcraftConfig
{
    public static final ForgeConfigSpec clientConfig;
    private static final ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

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

    public enum InkIndicator {
        CROSSHAIR,
        DURABILITY,
        BOTH,
        NONE
    }

    public enum PreventBobView {
        SUBMERGED,
        ALWAYS,
        OFF
    }

    public static class Client {
        public static ForgeConfigSpec.EnumValue<SplatcraftKeyHandler.KeyMode> squidKeyMode;
        public static ForgeConfigSpec.EnumValue<InkIndicator> inkIndicator;
        public static ForgeConfigSpec.BooleanValue vanillaInkDurability;
        public static ForgeConfigSpec.BooleanValue holdBarrierToRender;
        public static ForgeConfigSpec.IntValue barrierRenderDistance;
        //public static ForgeConfigSpec.BooleanValue colorLock; TODO
        public static ForgeConfigSpec.EnumValue<PreventBobView> preventBobView;
        public static ForgeConfigSpec.BooleanValue lowInkWarning;

        public static void init(ForgeConfigSpec.Builder client)
        {
            client.comment("Accessibility Settings");
            squidKeyMode = client.comment("Squid Key Mode").defineEnum("splatcraft.squidKeyMode", SplatcraftKeyHandler.KeyMode.TOGGLE);
            inkIndicator = client.comment("Determines how the amount of ink left in your tank is visualized.").defineEnum("splatcraft.inkIndicator", InkIndicator.BOTH);
            vanillaInkDurability = client.comment("Determines whether the any indicator that determines how much ink you have left matches vanilla durability colors instead of your ink color.")
                    .define("splatcraft.vanillaInkDurabilityColor", false);
            holdBarrierToRender = client.comment("Prevents Stage Barriers from rendering in creative mode unless the player is holding one in their hand.")
                    .define("splatcraft.holdBarrierToRender", false);
            barrierRenderDistance = client.comment("How far away stage barriers or voids will render away from you.")
                    .defineInRange("splatcraft.barrierRenderDistance", 40, 4, 80);
            //colorLock = client.comment("Color Lock Mode").define("splatcraft.colorLock", false);
            preventBobView = client.comment("Prevents changing FOV when in Squid Mode").defineEnum("splatcraft.preventBobView", PreventBobView.OFF);
            lowInkWarning = client.comment("Determines whether the ink indicator near your crosshair warns you if your ink is low.")
                    .define("splatcraft.lowInkWarning", true);
        }

        public static boolean getColorLock()
        {
            return false;
        }
    }

}
