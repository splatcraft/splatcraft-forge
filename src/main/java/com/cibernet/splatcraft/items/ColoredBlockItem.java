package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
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
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ColoredBlockItem extends BlockItem
{

    private final Item clearItem;
    private boolean addStartersToTab = true;


    public ColoredBlockItem(Block block, String name, Properties properties, Item clearItem, boolean addStartersToTab)
    {
        this(block, name, properties, clearItem);
        this.addStartersToTab = addStartersToTab;
    }

    public ColoredBlockItem(Block block, String name, Properties properties, Item clearItem)
    {
        super(block, properties);
        SplatcraftItems.inkColoredItems.add(this);
        setRegistryName(name);
        this.clearItem = clearItem;
    }

    public ColoredBlockItem(Block block, String name, Properties properties)
    {
        this(block, name, properties, null);
    }

    public ColoredBlockItem(Block block, String name, int stackSize, Item clearItem)
    {
        this(block, name, new Properties().maxStackSize(stackSize).group(SplatcraftItemGroups.GROUP_GENERAL), clearItem);
    }

    public ColoredBlockItem(Block block, String name)
    {
        this(block, name, 64, null);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        if (ColorUtils.getInkColor(stack) != -1)
        {
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        }

    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        MinecraftServer server = worldIn.getServer();
        if (server == null)
        {
            return false;
        }

        int color = ColorUtils.getInkColor(stack);

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(color != -1)
        {
            if(getBlock() instanceof IColoredBlock)
                ((IColoredBlock) getBlock()).setColor(worldIn, pos, color);
            else if (tileEntity instanceof InkColorTileEntity)
                ((InkColorTileEntity) tileEntity).setColor(color);
        }
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (isInGroup(group))
        {
            if (addStartersToTab)
            {
                for (int color : ColorUtils.STARTER_COLORS)
                {
                    items.add(ColorUtils.setInkColor(new ItemStack(this), color));
                }
            } else
            {
                super.fillItemGroup(group, items);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (ColorUtils.getInkColor(stack) == -1)
        {
            ColorUtils.setInkColor(stack, entityIn instanceof PlayerEntity && PlayerInfoCapability.hasCapability((LivingEntity) entityIn) ?
                    ColorUtils.getPlayerColor((PlayerEntity) entityIn) : ColorUtils.DEFAULT);
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.getPosition();

        if (clearItem != null && InkedBlock.causesClear(entity.world.getBlockState(pos)))
            entity.setItem(new ItemStack(clearItem, stack.getCount()));

        return false;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        BlockState state = context.getWorld().getBlockState(context.getPos());
        if (state.getBlock() instanceof CauldronBlock && context.getPlayer() != null && context.getPlayer().isSneaking())
        {
            int i = state.get(CauldronBlock.LEVEL);

            if (i > 0)
            {
                ItemStack itemstack1 = new ItemStack(clearItem, 1);
                World world = context.getWorld();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getItem();

                context.getPlayer().addStat(Stats.USE_CAULDRON);

                if (!player.abilities.isCreativeMode)
                {
                    stack.shrink(1);
                    world.setBlockState(context.getPos(), state.with(CauldronBlock.LEVEL, MathHelper.clamp(i - 1, 0, 3)), 2);
                    world.updateComparatorOutputLevel(context.getPos(), state.getBlock());
                }

                if (stack.isEmpty())
                {
                    player.setHeldItem(context.getHand(), itemstack1);
                } else if (!player.inventory.addItemStackToInventory(itemstack1))
                {
                    player.dropItem(itemstack1, false);
                } else if (player instanceof ServerPlayerEntity)
                {
                    ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                }

                return ActionResultType.SUCCESS;
            }

        }

        return super.onItemUse(context);
    }

    public ColoredBlockItem addStarters(boolean b)
    {
        addStartersToTab = b;
        return this;
    }
}
