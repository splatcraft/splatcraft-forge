package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.gui.InkVatScreen;
import com.cibernet.splatcraft.client.gui.WeaponWorkbenchScreen;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static com.cibernet.splatcraft.registries.SplatcraftBlocks.inkColoredBlocks;
import static com.cibernet.splatcraft.registries.SplatcraftItems.inkColoredItems;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupHandler {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE))
            return;

        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/stage_barrier_fancy"));
        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/stage_void_fancy"));
    }

    public static void bindScreenContainers() {
        ScreenManager.registerFactory(SplatcraftTileEntitites.inkVatContainer, InkVatScreen::new);
        ScreenManager.registerFactory(SplatcraftTileEntitites.weaponWorkbenchContainer, WeaponWorkbenchScreen::new);
    }


    @SubscribeEvent
    public static void initItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();

        colors.register(new InkItemColor(), inkColoredItems.toArray(new Item[0]));

    }

    @SubscribeEvent
    public static void initBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();

        colors.register(new InkBlockColor(), inkColoredBlocks.toArray(new Block[0]));

    }

    protected static class InkItemColor implements IItemColor {
        @Override
        public int getColor(ItemStack stack, int i) {
            int color = i == 0 ? ColorUtils.getInkColor(stack) : -1;
            if (SplatcraftConfig.Client.getColorLock())
                color = ColorUtils.getLockedColor(color);
            return color;
        }
    }

    protected static class InkBlockColor implements IBlockColor {

        @Override
        public int getColor(BlockState blockState, @Nullable IBlockDisplayReader iBlockDisplayReader, @Nullable BlockPos blockPos, int i) {
            if (iBlockDisplayReader == null || blockPos == null)
                return -1;

            int color = ColorUtils.getInkColor(iBlockDisplayReader.getTileEntity(blockPos));

            if (SplatcraftConfig.Client.getColorLock())
                color = ColorUtils.getLockedColor(color);

            if (color == -1)
                return 0xFFFFFF;

            return color;
        }
    }
}
