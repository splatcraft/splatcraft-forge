package com.cibernet.splatcraft.proxy;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.gui.SplatCraftGuiHandler;
import com.cibernet.splatcraft.handlers.CommonEventHandler;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftSaveHandler;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.recipes.RecipesInkwellVat;
import com.cibernet.splatcraft.recipes.RecipesWeaponStation;
import com.cibernet.splatcraft.registries.SplatCraftEntities;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.tileentities.TileEntityInkwellVat;
import com.cibernet.splatcraft.tileentities.TileEntitySunkenCrate;
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

        SplatCraftEntities.registerEntities();
        SplatCraftPacketHandler.registerMessages(SplatCraft.MODID);
        SplatCraftGamerules.registerRules();
        
        GameRegistry.registerTileEntity(TileEntityInkedBlock.class, SplatCraft.MODID+ ":inked_block");
        GameRegistry.registerTileEntity(TileEntityColor.class, SplatCraft.MODID+ ":ink_color");
        GameRegistry.registerTileEntity(TileEntitySunkenCrate.class, SplatCraft.MODID+ ":sunken_crate");
        GameRegistry.registerTileEntity(TileEntityInkwellVat.class, SplatCraft.MODID+ ":inkwell_vat");

    }

    public void init()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(SplatCraft.instance, new SplatCraftGuiHandler());
        
        MinecraftForge.EVENT_BUS.register(CommonEventHandler.instance);
        MinecraftForge.EVENT_BUS.register(new SplatCraftSaveHandler());

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
