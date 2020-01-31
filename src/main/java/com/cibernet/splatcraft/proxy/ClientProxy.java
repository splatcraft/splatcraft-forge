package com.cibernet.splatcraft.proxy;

import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.registries.SplatCraftModelManager;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    protected static void registerRenderers()
    {
        Minecraft mc = Minecraft.getMinecraft();
        
        mc.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            //if(tintIndex == 0)
                return ItemWeaponBase.getInkColor(stack);
            
            //return 0;
        }, SplatCraftItems.splatRoller, SplatCraftItems.splattershot);
        
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

    }

    @Override
    public void init()
    {
        super.init();
        registerRenderers();
    }

    @Override
    public void postInit()
    {
        super.postInit();
    }
}
