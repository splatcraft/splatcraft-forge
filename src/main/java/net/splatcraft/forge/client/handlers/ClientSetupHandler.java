package net.splatcraft.forge.client.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
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
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.gui.InkVatScreen;
import net.splatcraft.forge.client.gui.WeaponWorkbenchScreen;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.SquidBumperItem;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        int i = 1;
        while(Minecraft.getInstance().getResourceManager().hasResource(new ResourceLocation(Splatcraft.MODID, "textures/blocks/inked_block"+i+".png")))
            event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/inked_block"+(i++)));
        i = 1;
        while(Minecraft.getInstance().getResourceManager().hasResource(new ResourceLocation(Splatcraft.MODID, "textures/blocks/glitter"+i+".png")))
            event.addSprite(new ResourceLocation(Splatcraft.MODID, "blocks/glitter"+(i++)));
    }

    public static void bindScreenContainers()
    {
        ScreenManager.register(SplatcraftTileEntities.inkVatContainer, InkVatScreen::new);
        ScreenManager.register(SplatcraftTileEntities.weaponWorkbenchContainer, WeaponWorkbenchScreen::new);
    }


    @SubscribeEvent
    public static void initItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();

        SplatcraftItems.inkColoredItems.add(SplatcraftItems.splatfestBand);
        SplatcraftItems.inkColoredItems.add(SplatcraftItems.clearBand);

        colors.register(new InkItemColor(), SplatcraftItems.inkColoredItems.toArray(new Item[0]));

    }

    @SubscribeEvent
    public static void initBlockColors(ColorHandlerEvent.Block event)
    {
        BlockColors colors = event.getBlockColors();

        colors.register(new InkBlockColor(), SplatcraftBlocks.inkColoredBlocks.toArray(new Block[0]));

    }

    protected static class InkItemColor implements IItemColor
    {
        @Override
        public int getColor(@NotNull ItemStack stack, int i)
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
        public int getColor(@NotNull BlockState blockState, @Nullable IBlockDisplayReader iBlockDisplayReader, @Nullable BlockPos blockPos, int i)
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
