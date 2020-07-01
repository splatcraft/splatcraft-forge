package com.cibernet.splatcraft.proxy;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.gui.SplatCraftGuiHandler;
import com.cibernet.splatcraft.handlers.CommonEventHandler;
import com.cibernet.splatcraft.registries.*;
import com.cibernet.splatcraft.scoreboard.SplatcraftScoreboardHandler;
import com.cibernet.splatcraft.tileentities.*;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftSaveHandler;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.recipes.RecipesInkwellVat;
import com.cibernet.splatcraft.recipes.RecipesWeaponStation;
import com.cibernet.splatcraft.world.gen.OreGenHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(SplatCraftItems.class);
        MinecraftForge.EVENT_BUS.register(SplatCraftBlocks.class);
        MinecraftForge.EVENT_BUS.register(SplatCraftSounds.class);
        
        SplatCraftSounds.initSounds();
        SplatCraftEntities.registerEntities();
        SplatCraftPacketHandler.registerMessages(SplatCraft.MODID);
        SplatCraftGamerules.registerRules();
        SplatCraftStats.registerStats();
        
        GameRegistry.registerTileEntity(TileEntityInkedBlock.class, SplatCraft.MODID+ ":inked_block");
        GameRegistry.registerTileEntity(TileEntityColor.class, SplatCraft.MODID+ ":ink_color");
        GameRegistry.registerTileEntity(TileEntitySunkenCrate.class, SplatCraft.MODID+ ":sunken_crate");
        GameRegistry.registerTileEntity(TileEntityInkwellVat.class, SplatCraft.MODID+ ":inkwell_vat");
        GameRegistry.registerTileEntity(TileEntityStageBarrier.class, SplatCraft.MODID+ ":stage_barrier");

    }

    public void init()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(SplatCraft.instance, new SplatCraftGuiHandler());
        
        MinecraftForge.EVENT_BUS.register(CommonEventHandler.instance);
        MinecraftForge.EVENT_BUS.register(new SplatCraftSaveHandler());
        MinecraftForge.EVENT_BUS.register(new SplatcraftScoreboardHandler());

        registerSmelting();
        RecipesInkwellVat.registerRecipes();
        RecipesWeaponStation.registerRecipes();

        GameRegistry.registerWorldGenerator(new OreGenHandler(), 0);
    }

    public void postInit()
    {

    }

    private void registerSmelting()
    {
        GameRegistry.addSmelting(SplatCraftBlocks.oreSardinium, new ItemStack(SplatCraftItems.sardinium), 0.6f);
    }
}
