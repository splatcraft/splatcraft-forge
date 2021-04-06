package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.blocks.*;
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

import java.util.ArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftBlocks
{
    public static final ArrayList<Block> inkColoredBlocks = new ArrayList<>();

    public static final Block inkedBlock = new InkedBlock("inked_block");
    public static final Block inkedStairs = new InkedStairsBlock("inked_stairs");
    public static final Block inkedSlab = new InkedSlabBlock("inked_slab");

    public static final Block glowingInkedBlock = InkedBlock.glowing("glowing_inked_block");
    public static final Block glowingInkedStairs = InkedStairsBlock.glowing("glowing_inked_stairs");
    public static final Block glowingInkedSlab = InkedSlabBlock.glowing("glowing_inked_slab");

    public static final Block sardiniumBlock = new MetalBlock(Material.IRON, MaterialColor.WHITE_TERRACOTTA).setRegistryName("sardinium_block");
    public static final Block sardiniumOre = new OreBlock(0).setRegistryName("sardinium_ore");
    public static final Block powerEggBlock = new LightBlock(9, AbstractBlock.Properties.create(Material.GOURD, DyeColor.ORANGE).harvestTool(ToolType.SHOVEL).sound(SoundType.SLIME).hardnessAndResistance(0.2f, 0)).setRegistryName("power_egg_block");
    public static final Block crate = new CrateBlock("crate", false);
    public static final Block sunkenCrate = new CrateBlock("sunken_crate", true);

    public static final Block inkVat = new InkVatBlock("ink_vat");

    public static final Block emptyInkwell =new EmptyInkwellBlock(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS).harvestTool(ToolType.PICKAXE)).setRegistryName("empty_inkwell");
    public static final Block inkwell = new InkwellBlock().setRegistryName("inkwell");
    public static final Block weaponWorkbench = new WeaponWorkbenchBlock("weapon_workbench");
    public static final Block inkedWool = new InkCoatedBlock("inked_wool", AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(0.8F).sound(SoundType.CLOTH));
    public static final Block inkedCarpet = new InkedCarpetBlock("inked_carpet");
    public static final Block inkedGlass = new InkedGlassBlock("inked_glass");
    public static final Block inkedGlassPane = new InkedGlassPaneBlock("inked_glass_pane");
    public static final Block canvas = new CanvasBlock("canvas");

    public static final Block grate = new GrateBlock("grate");
    public static final Block grateRamp = new GrateRampBlock("grate_ramp");
    public static final Block barrierBar = new BarrierBarBlock("barrier_bar");
    public static final Block cautionBarrierBar = new BarrierBarBlock("caution_barrier_bar");
    public static final Block platedBarrierBar = new BarrierBarBlock("plated_barrier_bar");
    public static final Block stageBarrier = new StageBarrierBlock("stage_barrier", false);
    public static final Block stageVoid = new StageBarrierBlock("stage_void", true);
    public static final Block allowedColorBarrier = new ColoredBarrierBlock("allowed_color_barrier", false);
    public static final Block deniedColorBarrier = new ColoredBarrierBlock("denied_color_barrier", true);

    @SubscribeEvent
    public static void blockInit(final RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(inkedBlock);
        registry.register(inkedStairs);
        registry.register(inkedSlab);
        registry.register(glowingInkedBlock);
        registry.register(glowingInkedStairs);
        registry.register(glowingInkedSlab);

        registry.register(sardiniumBlock);
        registry.register(sardiniumOre);
        registry.register(powerEggBlock);
        registry.register(sunkenCrate);
        registry.register(crate);

        registry.register(inkVat);

        registry.register(emptyInkwell);
        registry.register(inkwell);
        registry.register(weaponWorkbench);
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

        registry.register(stageBarrier);
        registry.register(stageVoid);
        registry.register(allowedColorBarrier);
        registry.register(deniedColorBarrier);

        registry.register(new IronBarsBlockOverride());
        registry.register(new ChainBlockOverride());
    }

    public static void setRenderLayers()
    {
        RenderTypeLookup.setRenderLayer(glowingInkedBlock, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(glowingInkedStairs, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(glowingInkedSlab, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(inkedGlass, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(inkedGlassPane, RenderType.getTranslucent());

        RenderTypeLookup.setRenderLayer(emptyInkwell, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(inkwell, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(grate, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(grateRamp, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(crate, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(sunkenCrate, RenderType.getCutout());
    }
}
