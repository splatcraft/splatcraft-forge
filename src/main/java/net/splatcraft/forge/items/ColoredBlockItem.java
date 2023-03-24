package net.splatcraft.forge.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.blocks.InkedBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkwellTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColoredBlockItem extends BlockItem implements IColoredItem
{

    private final Item clearItem;
    private boolean addStartersToTab = false;
    private boolean matchColor = true;

    public ColoredBlockItem(Block block, String name, Properties properties, Item clearItem)
    {
        super(block, properties);
        SplatcraftItems.inkColoredItems.add(this);
        InkwellTileEntity.inkCoatingRecipes.put(clearItem, this);
        setRegistryName(name);
        this.clearItem = clearItem;
    }

    public ColoredBlockItem(Block block, String name, int stackSize, @Nullable Item clearItem)
    {
        this(block, name, new Properties().stacksTo(stackSize).tab(SplatcraftItemGroups.GROUP_GENERAL), clearItem);
    }

    public ColoredBlockItem setMatchColor(boolean matchColor) {
        this.matchColor = matchColor;
        return this;
    }

    public boolean matchesColor() {
        return matchColor;
    }

    public ColoredBlockItem(Block block, String name)
    {
        this(block, name, 64, null);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World level, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (I18n.exists(getDescriptionId() + ".tooltip"))
            tooltip.add(new TranslationTextComponent(getDescriptionId() + ".tooltip").withStyle(TextFormatting.GRAY));

        if (ColorUtils.isColorLocked(stack))
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        else if(matchColor)
            tooltip.add(new TranslationTextComponent( "item.splatcraft.tooltip.matches_color").withStyle(TextFormatting.GRAY));
    }

    public ColoredBlockItem addStarterColors()
    {
        addStartersToTab = true;
        return this;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, World levelIn, @Nullable PlayerEntity player, @NotNull ItemStack stack, @NotNull BlockState state)
    {
        MinecraftServer server = levelIn.getServer();
        if (server == null)
        {
            return false;
        }

        int color = ColorUtils.getInkColor(stack);

        TileEntity tileEntity = levelIn.getBlockEntity(pos);
        if(color != -1)
        {
            if(getBlock() instanceof IColoredBlock)
                ((IColoredBlock) getBlock()).setColor(levelIn, pos, color);
            else if (tileEntity instanceof InkColorTileEntity)
                ((InkColorTileEntity) tileEntity).setColor(color);
        }
        return super.updateCustomBlockEntityTag(pos, levelIn, player, stack, state);
    }

    @Override
    public void fillItemCategory(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> items)
    {
        if (allowdedIn(group))
        {
            items.add(ColorUtils.setColorLocked(new ItemStack(this), false));
            if (addStartersToTab)
            {
                for (int color : ColorUtils.STARTER_COLORS)
                    items.add(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(this), color), true));
            }
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World levelIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, levelIn, entityIn, itemSlot, isSelected);

        if (matchColor && (ColorUtils.getInkColor(stack) == -1 || !ColorUtils.isColorLocked(stack)))
        {
            ColorUtils.setInkColor(stack, entityIn instanceof PlayerEntity && PlayerInfoCapability.hasCapability((LivingEntity) entityIn) ?
                    ColorUtils.getPlayerColor((PlayerEntity) entityIn) : ColorUtils.DEFAULT);
        }
    }


    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition();

        if (entity.level.getBlockState(pos.below()).getBlock() instanceof InkwellBlock)
        {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos.below());

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
            {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }
        else if (clearItem != null && InkedBlock.causesClear(entity.level.getBlockState(pos)))
            entity.setItem(new ItemStack(clearItem, stack.getCount()));

        return false;
    }

    @Override
    public @NotNull ActionResultType useOn(ItemUseContext context)
    {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof CauldronBlock && context.getPlayer() != null && context.getPlayer().isCrouching())
        {
            int i = state.getValue(CauldronBlock.LEVEL);

            if (i > 0)
            {
                ItemStack itemstack1 = new ItemStack(clearItem, 1);
                World level = context.getLevel();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getItemInHand();

                context.getPlayer().awardStat(Stats.USE_CAULDRON);

                if (!player.isCreative())
                {
                    stack.shrink(1);
                    level.setBlock(context.getClickedPos(), state.setValue(CauldronBlock.LEVEL, MathHelper.clamp(i - 1, 0, 3)), 2);
                    level.updateNeighbourForOutputSignal(context.getClickedPos(), state.getBlock());
                }

                if (stack.isEmpty())
                {
                    player.setItemInHand(context.getHand(), itemstack1);
                } else if (!player.inventory.add(itemstack1))
                {
                    player.drop(itemstack1, false);
                } else if (player instanceof ServerPlayerEntity)
                {
                    ((ServerPlayerEntity) player).refreshContainer(player.containerMenu);
                }

                return ActionResultType.SUCCESS;
            }

        }

        return super.useOn(context);
    }

    public ColoredBlockItem addStarters(boolean b)
    {
        addStartersToTab = b;
        return this;
    }
}
