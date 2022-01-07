package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.gui.InkVatScreen;
import com.cibernet.splatcraft.client.gui.WeaponWorkbenchScreen;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.SquidBumperItem;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.container.PlayerContainer;
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
public class ClientSetupHandler
{

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (!event.getMap().location().equals(PlayerContainer.BLOCK_ATLAS))
            return;

        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/stage_barrier_fancy"));
        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/stage_void_fancy"));
        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/allowed_color_barrier_fancy"));
        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/denied_color_barrier_fancy"));
        event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/permanent_ink_overlay"));
    }

    public static void bindScreenContainers()
    {
        ScreenManager.register(SplatcraftTileEntitites.inkVatContainer, InkVatScreen::new);
        ScreenManager.register(SplatcraftTileEntitites.weaponWorkbenchContainer, WeaponWorkbenchScreen::new);
    }


    @SubscribeEvent
    public static void initItemColors(ColorHandlerEvent.Item event)
    {
        ItemColors colors = event.getItemColors();

        inkColoredItems.add(SplatcraftItems.splatfestBand);
        inkColoredItems.add(SplatcraftItems.clearBand);

        colors.register(new InkItemColor(), inkColoredItems.toArray(new Item[0]));

    }

    @SubscribeEvent
    public static void initBlockColors(ColorHandlerEvent.Block event)
    {
        BlockColors colors = event.getBlockColors();

        colors.register(new InkBlockColor(), inkColoredBlocks.toArray(new Block[0]));

    }

    protected static class InkItemColor implements IItemColor
    {
        @Override
        public int getColor(ItemStack stack, int i)
        {
            boolean isDefault = ColorUtils.getInkColor(stack) == -1 && !ColorUtils.isColorLocked(stack);
            int color = i == 0 ? (((stack.getItem().is(SplatcraftTags.Items.INK_BANDS) || !stack.getItem().is(SplatcraftTags.Items.MATCH_ITEMS))
                    && isDefault && PlayerInfoCapability.hasCapability(Minecraft.getInstance().player))
                    ? ColorUtils.getEntityColor(Minecraft.getInstance().player) : ColorUtils.getInkColor(stack)) : -1;

            if(i == 0 && stack.getItem() instanceof SquidBumperItem && isDefault)
                color = 0xFFFFFF - color;

            if (SplatcraftConfig.Client.getColorLock())
                color = ColorUtils.getLockedColor(color);
            return color;
        }
    }

    protected static class InkBlockColor implements IBlockColor
    {

        @Override
        public int getColor(BlockState blockState, @Nullable IBlockDisplayReader iBlockDisplayReader, @Nullable BlockPos blockPos, int i)
        {
            if (iBlockDisplayReader == null || blockPos == null)
                return -1;

            int color = ColorUtils.getInkColor(iBlockDisplayReader.getBlockEntity(blockPos));

            if (SplatcraftConfig.Client.getColorLock())
                color = ColorUtils.getLockedColor(color);

            if (color == -1)
                return 0xFFFFFF;

            return color;
        }
    }
}
