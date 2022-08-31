package net.splatcraft.forge.registries;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.DyeColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.blocks.BarrierBarBlock;
import net.splatcraft.forge.blocks.CanvasBlock;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.blocks.CrateBlock;
import net.splatcraft.forge.blocks.EmptyInkwellBlock;
import net.splatcraft.forge.blocks.GrateBlock;
import net.splatcraft.forge.blocks.GrateRampBlock;
import net.splatcraft.forge.blocks.InkStainedBlock;
import net.splatcraft.forge.blocks.InkVatBlock;
import net.splatcraft.forge.blocks.InkedBlock;
import net.splatcraft.forge.blocks.InkedCarpetBlock;
import net.splatcraft.forge.blocks.InkedGlassBlock;
import net.splatcraft.forge.blocks.InkedGlassPaneBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.blocks.LightBlock;
import net.splatcraft.forge.blocks.MetalBlock;
import net.splatcraft.forge.blocks.OreBlock;
import net.splatcraft.forge.blocks.RemotePedestalBlock;
import net.splatcraft.forge.blocks.SplatSwitchBlock;
import net.splatcraft.forge.blocks.StageBarrierBlock;
import net.splatcraft.forge.blocks.TarpBlock;
import net.splatcraft.forge.blocks.WeaponWorkbenchBlock;

import java.util.ArrayList;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftBlocks
{
    public static final ArrayList<Block> inkColoredBlocks = new ArrayList<>();

    public static final InkedBlock inkedBlock = new InkedBlock("inked_block");
    public static final InkedBlock glowingInkedBlock = InkedBlock.glowing("glowing_inked_block");
    public static final InkedBlock clearInkedBlock = new InkedBlock("clear_inked_block");

    public static final Block sardiniumBlock = new MetalBlock(Material.METAL, MaterialColor.TERRACOTTA_WHITE).setRegistryName("sardinium_block");
    public static final Block sardiniumOre = new OreBlock(0).setRegistryName("sardinium_ore");
    public static final Block powerEggBlock = new LightBlock(9, AbstractBlock.Properties.of(Material.VEGETABLE, DyeColor.ORANGE).harvestTool(ToolType.SHOVEL).sound(SoundType.SLIME_BLOCK).strength(0.2f, 0)).setRegistryName("power_egg_block");
    public static final Block crate = new CrateBlock("crate", false);
    public static final Block sunkenCrate = new CrateBlock("sunken_crate", true);

    public static final Block inkVat = new InkVatBlock("ink_vat");
    public static final Block weaponWorkbench = new WeaponWorkbenchBlock("ammo_knights_workbench");
    public static final Block remotePedestal = new RemotePedestalBlock().setRegistryName("remote_pedestal");

    public static final Block emptyInkwell =new EmptyInkwellBlock(Block.Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).harvestTool(ToolType.PICKAXE)).setRegistryName("empty_inkwell");
    public static final Block inkwell = new InkwellBlock().setRegistryName("inkwell");

    public static final Block inkedWool = new InkStainedBlock("ink_stained_wool", AbstractBlock.Properties.of(Material.WOOL).strength(0.8F).sound(SoundType.WOOL));
    public static final Block inkedCarpet = new InkedCarpetBlock("ink_stained_carpet");
    public static final Block inkedGlass = new InkedGlassBlock("ink_stained_glass");
    public static final Block inkedGlassPane = new InkedGlassPaneBlock("ink_stained_glass_pane");
    public static final Block canvas = new CanvasBlock("canvas");
    public static final Block splatSwitch = new SplatSwitchBlock().setRegistryName("splat_switch");

    public static final Block grate = new GrateBlock("grate");
    public static final Block grateRamp = new GrateRampBlock("grate_ramp");
    public static final Block barrierBar = new BarrierBarBlock("barrier_bar");
    public static final Block cautionBarrierBar = new BarrierBarBlock("caution_barrier_bar");
    public static final Block platedBarrierBar = new BarrierBarBlock("plated_barrier_bar");
    public static final Block tarp = new TarpBlock().setRegistryName("tarp");
    public static final Block stageBarrier = new StageBarrierBlock("stage_barrier", false);
    public static final Block stageVoid = new StageBarrierBlock("stage_void", true);
    public static final Block allowedColorBarrier = new ColoredBarrierBlock("allowed_color_barrier", false);
    public static final Block deniedColorBarrier = new ColoredBarrierBlock("denied_color_barrier", true);

    @SubscribeEvent
    public static void blockInit(final RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(inkedBlock);
        registry.register(glowingInkedBlock);
        registry.register(clearInkedBlock);

        registry.register(sardiniumBlock);
        registry.register(sardiniumOre);
        registry.register(powerEggBlock);
        registry.register(sunkenCrate);
        registry.register(crate);

        registry.register(inkVat);
        registry.register(weaponWorkbench);
        registry.register(remotePedestal);

        registry.register(emptyInkwell);
        registry.register(inkwell);
        registry.register(inkedWool);
        registry.register(inkedCarpet);
        registry.register(inkedGlass);
        registry.register(inkedGlassPane);
        registry.register(canvas);

        registry.register(grate);
        registry.register(grateRamp);
        registry.register(barrierBar);
        registry.register(platedBarrierBar);
        registry.register(cautionBarrierBar);
        registry.register(tarp);
        registry.register(splatSwitch);

        registry.register(stageBarrier);
        registry.register(stageVoid);
        registry.register(allowedColorBarrier);
        registry.register(deniedColorBarrier);
    }


    public static void setRenderLayers()
    {
        RenderTypeLookup.setRenderLayer(glowingInkedBlock, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(inkedGlass, RenderType.translucent());
        RenderTypeLookup.setRenderLayer(inkedGlassPane, RenderType.translucent());

        RenderTypeLookup.setRenderLayer(emptyInkwell, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(inkwell, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(grate, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(grateRamp, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(crate, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(sunkenCrate, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(remotePedestal, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(splatSwitch, RenderType.cutout());
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Missmaps
    {
        private static final HashMap<String, Block> remaps = new HashMap<String, Block>()
        {{
            put("inked_wool", inkedWool);
            put("inked_carpet", inkedCarpet);
            put("inked_glass", inkedGlass);
            put("inked_glass_pane", inkedGlassPane);
            put("weapon_workbench", weaponWorkbench);

            put("inked_stairs", inkedBlock);
            put("inked_slab", inkedBlock);
            put("tall_inked_block", inkedBlock);
            put("glowing_inked_stairs", glowingInkedBlock);
            put("glowing_inked_slab", glowingInkedBlock);
            put("tall_glowing_inked_block", glowingInkedBlock);
            put("tall_clear_inked_block", clearInkedBlock);
        }};

        @SubscribeEvent
        public static void onMissingMappings(final RegistryEvent.MissingMappings<Block> event)
        {
            for(RegistryEvent.MissingMappings.Mapping<Block> block : event.getMappings(Splatcraft.MODID))
            {
                String key = block.key.getPath();
                if(remaps.containsKey(key))
                    block.remap(remaps.get(key));
            }
        }
    }
}
