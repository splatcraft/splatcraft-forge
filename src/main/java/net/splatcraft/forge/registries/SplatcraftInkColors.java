package net.splatcraft.forge.registries;

import net.minecraft.world.item.DyeColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkColor;

import static net.splatcraft.forge.Splatcraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftInkColors
{
    //Starter Colors
    public static final InkColor orange = new InkColor("orange", 0xDF641A);
    public static final InkColor blue = new InkColor("blue", 0x26229F);
    public static final InkColor pink = new InkColor("pink", 0xC83D79);
    public static final InkColor green = new InkColor("green", 0x409D3B);

    //Basic Colors
    public static final InkColor lightBlue = new InkColor("light_blue", 0x228cff);
    public static final InkColor turquoise = new InkColor("turquoise", 0x048188);
    public static final InkColor yellow = new InkColor("yellow", 0xe1a307);
    public static final InkColor lilac = new InkColor("lilac", 0x4d24a3);
    public static final InkColor lemon = new InkColor("lemon", 0x91b00b);
    public static final InkColor plum = new InkColor("plum", 0x830b9c);

    //Pastel Colors
    public static final InkColor cyan = new InkColor("cyan", 0x4ACBCB);
    public static final InkColor peach = new InkColor("peach", 0xEA8546);
    public static final InkColor mint = new InkColor("mint", 0x08B672);
    public static final InkColor cherry = new InkColor("cherry", 0xE24F65);

    //Neon Colors
    public static final InkColor neonPink = new InkColor("neon_pink", 0xcf0466);
    public static final InkColor neonGreen = new InkColor("neon_green", 0x17a80d);
    public static final InkColor neonOrange = new InkColor("neon_orange", 0xe85407);
    public static final InkColor neonBlue = new InkColor("neon_blue", 0x2e0cb5);

    //Hero Colors
    public static final InkColor squid = new InkColor("hero_yellow", 0xD3F526);
    public static final InkColor octo = new InkColor("octo_pink", 0xE51B5E);

    //Special Colors
    public static final InkColor mojang = new InkColor("mojang", 0xDF242F);
    public static final InkColor cobalt = new InkColor("cobalt", 0x005682);
    public static final InkColor ice = new InkColor("ice", 0x88ffc1);
    public static final InkColor floral = new InkColor("floral", 0xFF9BEE);
    public static final InkColor omniGreen = new InkColor("omni_green", 0x93E720);
    public static final InkColor mana = new InkColor("mana", 0xF33EF1);

    //Colorblind mode
    public static final InkColor colorLockA = new InkColor("color_lock_friendly", 0xDEA801);
    public static final InkColor colorLockB = new InkColor("color_lock_hostile", 0x4717A9);

    //Organic Colors
    public static final InkColor dyeWhite = new InkColor("dye_white", 0xFAFAFA, DyeColor.WHITE);
    public static final InkColor dyeOrange = new InkColor("dye_orange", 16351261, DyeColor.ORANGE);
    public static final InkColor dyeMagenta = new InkColor("dye_magenta", 13061821, DyeColor.MAGENTA);
    public static final InkColor dyeLightBlue = new InkColor("dye_light_blue", 3847130, DyeColor.LIGHT_BLUE);
    public static final InkColor dyeYellow = new InkColor("dye_yellow", 16701501, DyeColor.YELLOW);
    public static final InkColor dyeLime = new InkColor("dye_lime", 8439583, DyeColor.LIME);
    public static final InkColor dyePink = new InkColor("dye_pink", 15961002, DyeColor.PINK);
    public static final InkColor dyeGray = new InkColor("dye_gray", 4673362, DyeColor.GRAY);
    public static final InkColor dyeLightGray = new InkColor("dye_light_gray", 10329495, DyeColor.LIGHT_GRAY);
    public static final InkColor dyeCyan = new InkColor("dye_cyan", 1481884, DyeColor.CYAN);
    public static final InkColor dyePurple = new InkColor("dye_purple", 8991416, DyeColor.PURPLE);
    public static final InkColor dyeBlue = new InkColor("dye_blue", 3949738, DyeColor.BLUE);
    public static final InkColor dyeBrown = new InkColor("dye_brown", 8606770, DyeColor.BROWN);
    public static final InkColor dyeGreen = new InkColor("dye_green", 6192150, DyeColor.GREEN);
    public static final InkColor dyeRed = new InkColor("dye_red", 11546150, DyeColor.RED);
    public static final InkColor dyeBlack = new InkColor("dye_black", 1908001, DyeColor.BLACK);

    //3.0
    public static final InkColor royalBlue = new InkColor("royal_blue", 0x525CF5);
    public static final InkColor mothGreen = new InkColor("moth_green", 0x425113);
    public static final InkColor lightGreen = new InkColor("light_green", 0x85E378);
    public static final InkColor purple = new InkColor("purple", 0x6C0676);
    public static final InkColor mustard = new InkColor("mustard", 0xCE8003);
    public static final InkColor lumigreen = new InkColor("lumigreen", 0x60AB43);
    public static final InkColor darkBlue = new InkColor("dark_blue", 0x0D195E);
    public static final InkColor soda = new InkColor("soda", 0x65B799);
    public static final InkColor deepBlue = new InkColor("deep_blue", 0x0D37C3);
    public static final InkColor fuchsia = new InkColor("fuchsia", 0xE532D6);
    public static final InkColor winterGreen = new InkColor("winter_green", 0x4DE29D);
    public static final InkColor pumpkin = new InkColor("pumpkin", 0xDD6900);
    public static final InkColor redwood = new InkColor("redwood", 0x5B342E);

    public static final InkColor undyed = new InkColor("default", ColorUtils.DEFAULT);

    /*
    @SubscribeEvent
    public static void registerInkColors(final RegistryEvent.Register<InkColor> event)
    {
        IForgeRegistry<InkColor> registry = event.getRegistry();

        registry.register(orange);
        registry.register(blue);
        registry.register(pink);
        registry.register(green);

        registry.register(lightBlue);
        registry.register(turquoise);
        registry.register(yellow);
        registry.register(lilac);
        registry.register(lemon);
        registry.register(plum);

        registry.register(cyan);
        registry.register(peach);
        registry.register(mint);
        registry.register(cherry);

        registry.register(neonPink);
        registry.register(neonGreen);
        registry.register(neonOrange);
        registry.register(neonBlue);

        registry.register(squid);
        registry.register(octo);

        registry.register(mojang);
        registry.register(cobalt);
        registry.register(ice);
        registry.register(floral);
        registry.register(omniGreen);
        registry.register(mana);

        registry.register(dyeWhite);
        registry.register(dyeOrange);
        registry.register(dyeMagenta);
        registry.register(dyeLightBlue);
        registry.register(dyeYellow);
        registry.register(dyeLime);
        registry.register(dyePink);
        registry.register(dyeGray);
        registry.register(dyeLightGray);
        registry.register(dyeCyan);
        registry.register(dyePurple);
        registry.register(dyeBlue);
        registry.register(dyeBrown);
        registry.register(dyeGreen);
        registry.register(dyeRed);
        registry.register(dyeBlack);

        registry.register(lightGreen);
        registry.register(purple);
        registry.register(royalBlue);
        registry.register(mothGreen);
        registry.register(mustard);
        registry.register(lumigreen);
        registry.register(darkBlue);
        registry.register(soda);
        registry.register(deepBlue);
        registry.register(fuchsia);
        registry.register(winterGreen);
        registry.register(pumpkin);
        registry.register(redwood);

        registry.register(undyed);

    }
    */

    /* TODO Data-Driven Ink
    @SubscribeEvent
    public static void registerRegistry(final RegistryEvent.NewRegistry event)
    {
        REGISTRY = new RegistryBuilder<InkColor>()
                .setName(new ResourceLocation(Splatcraft.MODID, "ink_colors"))
                .setType(InkColor.class)
                .set(DummyFactory.INSTANCE)
                .tagFolder("ink_colors")
                .create();
    }

    private static class DummyFactory implements IForgeRegistry.DummyFactory<InkColor>
    {
        private static final DummyFactory INSTANCE = new DummyFactory();

        @Override
        public InkColor createDummy(ResourceLocation key)
        {
            return new InkColor.DummyType().setRegistryName(key);
        }
    }
    */
}
