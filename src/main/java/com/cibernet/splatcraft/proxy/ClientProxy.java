package com.cibernet.splatcraft.proxy;


import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.renderers.RenderInkProjectile;
import com.cibernet.splatcraft.handlers.ClientEventHandler;
import com.cibernet.splatcraft.handlers.SplatCraftKeyHandler;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.registries.SplatCraftModelManager;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    protected static void registerRenderers()
    {
        
        Minecraft mc = Minecraft.getMinecraft();
        
        mc.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            //if(tintIndex == 0)
                return ItemWeaponBase.getInkColor(stack);
            
            //return 0;
        }, SplatCraftItems.splatRoller, SplatCraftItems.splattershot, SplatCraftItems.splatCharger);
        
        mc.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
                return 0;
            
            TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
            
            return te.getColor();
            
        }, SplatCraftBlocks.inkedBlock);
        
    }
    
    @Override
    public void preInit()
    {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(SplatCraftModelManager.class);
        SplatCraftKeyHandler.instance.registerKeys();

        RenderingRegistry.registerEntityRenderingHandler(EntityInkProjectile.class, manager -> new RenderInkProjectile(manager));

    }

    @Override
    public void init()
    {
        super.init();
        registerRenderers();
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.instance);
    }

    @Override
    public void postInit()
    {
        super.postInit();
    }
}
