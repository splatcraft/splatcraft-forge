package com.cibernet.splatcraft.proxy;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.handlers.CommonEventHandler;
import com.cibernet.splatcraft.handlers.SplatCraftSaveHandler;
import com.cibernet.splatcraft.network.SplatCraftChannelHandler;
import com.cibernet.splatcraft.registries.SplatCraftEntities;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(SplatCraftItems.class);
        MinecraftForge.EVENT_BUS.register(SplatCraftBlocks.class);

        SplatCraftEntities.registerEntities();

        GameRegistry.registerTileEntity(TileEntityInkedBlock.class, SplatCraft.MODID+ ":inked_block");
        GameRegistry.registerTileEntity(TileEntityColor.class, SplatCraft.MODID+ ":ink_color");
    }

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(CommonEventHandler.instance);
        MinecraftForge.EVENT_BUS.register(new SplatCraftSaveHandler());
        SplatCraftChannelHandler.setupChannel();
    }

    public void postInit()
    {

    }

}
