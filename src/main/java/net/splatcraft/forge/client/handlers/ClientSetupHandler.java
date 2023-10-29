package net.splatcraft.forge.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        if (!event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
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
        MenuScreens.register(SplatcraftTileEntities.inkVatContainer.get(), InkVatScreen::new);
        MenuScreens.register(SplatcraftTileEntities.weaponWorkbenchContainer.get(), WeaponWorkbenchScreen::new);
    }


    @SubscribeEvent
    public static void initItemColors(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();

        SplatcraftItems.inkColoredItems.add(SplatcraftItems.splatfestBand.get());
        SplatcraftItems.inkColoredItems.add(SplatcraftItems.clearBand.get());

        colors.register(new InkItemColor(), SplatcraftItems.inkColoredItems.toArray(new Item[0]));

    }

    @SubscribeEvent
    public static void initBlockColors(ColorHandlerEvent.Block event)
    {
        BlockColors colors = event.getBlockColors();

        colors.register(new InkBlockColor(), SplatcraftBlocks.inkColoredBlocks.toArray(new Block[0]));

    }

    protected static class InkItemColor implements ItemColor
    {
        @Override
        public int getColor(@NotNull ItemStack stack, int i)
        {
            if(i != 0)
                return -1;

            boolean isDefault = ColorUtils.getInkColor(stack) == -1 && !ColorUtils.isColorLocked(stack);
            int color = (stack.is(SplatcraftTags.Items.INK_BANDS) || !stack.is(SplatcraftTags.Items.MATCH_ITEMS)) && isDefault && PlayerInfoCapability.hasCapability(Minecraft.getInstance().player)
                    ? ColorUtils.getEntityColor(Minecraft.getInstance().player) : ColorUtils.getInkColor(stack);

            if (SplatcraftConfig.Client.getColorLock())
                color = ColorUtils.getLockedColor(color);
            else if(ColorUtils.isInverted(stack))
                color = 0xFFFFFF - color;

            return color;
        }
    }

    protected static class InkBlockColor implements BlockColor
    {

        @Override
        public int getColor(@NotNull BlockState blockState, @Nullable BlockAndTintGetter iBlockDisplayReader, @Nullable BlockPos blockPos, int i)
        {
            if (i != 0 || iBlockDisplayReader == null || blockPos == null)
                return -1;


            BlockEntity te = iBlockDisplayReader.getBlockEntity(blockPos);

            if(te == null)
                return -1;

            int color = ColorUtils.getInkColor(te);
            if (SplatcraftConfig.Client.getColorLock())
                color = ColorUtils.getLockedColor(color);
            else if(ColorUtils.isInverted(te.getLevel(), blockPos))
                color = 0xFFFFFF - color;

            if (color == -1)
                return 0xFFFFFF;

            return color;
        }
    }
}
