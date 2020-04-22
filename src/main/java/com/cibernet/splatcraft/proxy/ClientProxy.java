package com.cibernet.splatcraft.proxy;


import com.cibernet.splatcraft.SplatCraftConfig;
import com.cibernet.splatcraft.blocks.BlockInkColor;
import com.cibernet.splatcraft.blocks.BlockInkwell;
import com.cibernet.splatcraft.entities.classes.EntityBlasterProjectile;
import com.cibernet.splatcraft.entities.classes.EntityChargerProjectile;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelArmorOverride;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.entities.renderers.RenderInkProjectile;
import com.cibernet.splatcraft.gui.GuiOverlay;
import com.cibernet.splatcraft.handlers.ClientEventHandler;
import com.cibernet.splatcraft.handlers.SplatCraftKeyHandler;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftModelManager;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkwellVat;
import com.cibernet.splatcraft.tileentities.TileEntitySunkenCrate;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class ClientProxy extends CommonProxy
{
    protected static void registerRenderers()
    {
        
        Minecraft mc = Minecraft.getMinecraft();
    
        Item[] inkColorItems = ColorItemUtils.inkColorItems.toArray(new Item[ColorItemUtils.inkColorItems.size()]);
        
        Block[] inkColorBlocks = new Block[BlockInkColor.blocks.size()];
        for(int i = 0; i < BlockInkColor.blocks.size(); i++)
            inkColorBlocks[i] = BlockInkColor.blocks.get(i);
        
        mc.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            if(tintIndex == 0)
                return ColorItemUtils.getInkColor(stack);
            
            return -1;
        }, inkColorItems);

        mc.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            if(tintIndex == 0)
                return BlockInkwell.getInkColor(stack);

            return -1;
        }, Item.getItemFromBlock(SplatCraftBlocks.inkwell));

        mc.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if(tintIndex != 0 || !(worldIn.getTileEntity(pos) instanceof TileEntityColor))
                return -1;
            
            if((worldIn.getTileEntity(pos) instanceof TileEntitySunkenCrate))
            {
                TileEntitySunkenCrate te = (TileEntitySunkenCrate) worldIn.getTileEntity(pos);
                if(te.getState() == 0)
                    return -1;
            }
            
            TileEntityColor te = (TileEntityColor) worldIn.getTileEntity(pos);
            
            return te.getColor();
            
        }, inkColorBlocks);

        mc.getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if((worldIn.getTileEntity(pos) instanceof TileEntityInkwellVat))
            {
                TileEntityInkwellVat te = (TileEntityInkwellVat) worldIn.getTileEntity(pos);
                    return te.getColor();
            }
            return -1;
        }, SplatCraftBlocks.inkwellVat);
        
    }
    
    
    public static EntityPlayer getClientPlayer() {
        return FMLClientHandler.instance().getClientPlayerEntity();
    }
    public static void addScheduledTask(Runnable runnable) {
    Minecraft.getMinecraft().addScheduledTask(runnable);
}
    
    
    
    @Override
    public void preInit()
    {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(SplatCraftModelManager.class);
        MinecraftForge.EVENT_BUS.register(GuiOverlay.instance);
        SplatCraftKeyHandler.instance.registerKeys();

        RenderingRegistry.registerEntityRenderingHandler(EntityInkProjectile.class, manager -> new RenderInkProjectile(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityChargerProjectile.class, manager -> new RenderInkProjectile(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityBlasterProjectile.class, manager -> new RenderInkProjectile(manager));

    }

    @Override
    public void init()
    {
        super.init();
        registerRenderers();
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.instance);
        MinecraftForge.EVENT_BUS.register(new SplatCraftConfig());
    }

    @Override
    public void postInit()
    {
        super.postInit();
        overridePlayerModels();
    }


    private void overridePlayerModels()
    {
        Map<String, RenderPlayer> skinMap = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, Minecraft.getMinecraft().getRenderManager(), "field_178636_l");
        if(skinMap == null) return;

        RenderPlayer defaultRender = skinMap.get("default");
        overridePlayerModels(defaultRender, false);

        RenderPlayer slimRender = skinMap.get("slim");
        overridePlayerModels(slimRender, true);
    }

    private void overridePlayerModels(RenderPlayer renderPlayer, boolean slim)
    {
        ModelBase oldModel = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, renderPlayer, "field_77045_g");
        ObfuscationReflectionHelper.setPrivateValue(RenderLivingBase.class, renderPlayer, new ModelPlayerOverride(oldModel, 0.0F, slim), "field_77045_g");

        List<LayerRenderer<EntityLivingBase>> layers = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, renderPlayer, "field_177097_h");
        if(layers != null)
        {
            LayerRenderer<EntityLivingBase> armorLayer = layers.stream().filter(layer -> layer instanceof LayerBipedArmor).findFirst().orElse(null);
            if(armorLayer != null)
            {
                Field field = ReflectionHelper.<EntityLivingBase>findField(LayerArmorBase.class, ObfuscationReflectionHelper.remapFieldNames(LayerArmorBase.class.getName(), "field_177186_d"));
                field.setAccessible(true);
                try
                {
                    field.set(armorLayer, new ModelArmorOverride());
                } catch(IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
