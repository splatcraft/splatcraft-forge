package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftStats;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class InkBlockUtils
{
    public static boolean playerInkBlock(PlayerEntity player, World level, BlockPos pos, int color, float damage, InkType inkType)
    {
        boolean inked = inkBlock(level, pos, color, damage, inkType);

        if (inked)
        {
            player.awardStat(SplatcraftStats.BLOCKS_INKED);
        }

        return inked;
    }

    public static boolean inkBlock(World level, BlockPos pos, int color, float damage, InkType inkType)
    {
        BlockState state = level.getBlockState(pos);
        TileEntity te = level.getBlockEntity(pos);

        if (InkedBlock.isTouchingLiquid(level, pos))
            return false;

        if (state.getBlock() instanceof IColoredBlock)
            return ((IColoredBlock) state.getBlock()).inkBlock(level, pos, color, damage, inkType);

        if (!canInk(level, pos))
            return false;

        BlockState inkState = getInkState(inkType, level, pos);

        InkedBlockTileEntity inkte = (InkedBlockTileEntity) SplatcraftBlocks.inkedBlock.createTileEntity(inkState, level);
        if (inkte == null)
        {
            return false;
        }
        inkte.setColor(color);
        inkte.setSavedState(state);

        level.setBlock(pos, inkState, 0);
        level.setBlockEntity(pos, inkte);
        level.markAndNotifyBlock(pos, level.getChunkAt(pos), inkState, inkState, 3, 512);

        for(Direction facing : Direction.values())
        {
            if(level.getBlockEntity(pos.relative(facing)) instanceof InkedBlockTileEntity)
            {
                InkedBlockTileEntity otherTe = (InkedBlockTileEntity) level.getBlockEntity(pos.relative(facing));
                otherTe.setSavedState(otherTe.getSavedState().getBlock().updateShape(otherTe.getSavedState(), facing.getOpposite(), inkState, level, pos.relative(facing), pos));
            }
        }

        return true;
    }


    public static BlockState getInkState(InkType inkType, World level, BlockPos pos)
    {
        if(level.getBlockState(pos).getBlock() instanceof InkedBlock)
            return inkType.block.defaultBlockState();
        return inkType.block.defaultBlockState();

    }


    public static boolean canInkFromFace(World level, BlockPos pos, Direction face)
    {
        if(!(level.getBlockState(pos).getBlock() instanceof IColoredBlock) && !canInk(level, pos))
            return false;

        return canInkPassthrough(level, pos.relative(face)) || !level.getBlockState(pos.relative(face)).is(SplatcraftTags.Blocks.BLOCKS_INK);
    }

    public static boolean canInk(World level, BlockPos pos)
    {

        if (InkedBlock.isTouchingLiquid(level, pos))
            return false;

        Block block = level.getBlockState(pos).getBlock();

        if (SplatcraftTags.Blocks.UNINKABLE_BLOCKS.contains(block))
            return false;

        if (!(level.getBlockEntity(pos) instanceof InkColorTileEntity) && level.getBlockEntity(pos) != null)
            return false;

        return !canInkPassthrough(level, pos);
    }

    public static boolean canInkPassthrough(World level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);

        return state.getCollisionShape(level, pos).isEmpty() || level.getBlockState(pos).is(SplatcraftTags.Blocks.INK_PASSTHROUGH);
    }

    public static boolean canSquidHide(LivingEntity entity)
    {
        return (entity.isOnGround() || !entity.level.getBlockState(new BlockPos(entity.getX(), entity.getY() - 0.1, entity.getZ())).getBlock().equals(Blocks.AIR))
                && canSquidSwim(entity) || canSquidClimb(entity);
    }

    public static boolean canSquidSwim(LivingEntity entity)
    {
        boolean canSwim = false;

        BlockPos down = getBlockStandingOnPos(entity);

        Block standingBlock = entity.level.getBlockState(down).getBlock();
        if (standingBlock instanceof IColoredBlock)
            canSwim = ((IColoredBlock) standingBlock).canSwim();

        if (canSwim)
            return ColorUtils.colorEquals(entity, entity.level.getBlockEntity(down));
        return false;
    }

    public static BlockPos getBlockStandingOnPos(Entity entity)
    {
        BlockPos result;
        for(double i = 0; i >= -0.5; i-=0.1)
        {
            result = new BlockPos(entity.getX(), entity.getY()+i, entity.getZ());

            if(!(entity.level.getBlockState(result).getMaterial().equals(Material.AIR) || entity.level.getBlockState(result).getCollisionShape(entity.level, result, ISelectionContext.of(entity)).isEmpty()))
                return result;
        }

        return new BlockPos(entity.getX(), entity.getY()-0.6, entity.getZ());
    }

    public static boolean onEnemyInk(LivingEntity entity)
    {
        if (!entity.isOnGround())
            return false;
        boolean canDamage = false;
        BlockPos pos = getBlockStandingOnPos(entity);

        if (entity.level.getBlockState(pos).getBlock() instanceof IColoredBlock)
            canDamage = ((IColoredBlock) entity.level.getBlockState(pos).getBlock()).canDamage();

        return canDamage && ColorUtils.getInkColor(entity.level.getBlockEntity(pos)) != -1 && !canSquidSwim(entity);
    }

    public static boolean canSquidClimb(LivingEntity entity)
    {
        if (onEnemyInk(entity))
            return false;
        for (int i = 0; i < 4; i++)
        {
            float xOff = (i < 2 ? .32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .32f) * (i % 2 == 0 ? 1 : -1);
            BlockPos pos = new BlockPos(entity.getX() - xOff, entity.getY(), entity.getZ() - zOff);
            Block block = entity.level.getBlockState(pos).getBlock();
            VoxelShape shape = entity.level.getBlockState(pos).getCollisionShape(entity.level, pos, ISelectionContext.of(entity));

            if(pos.equals(getBlockStandingOnPos(entity)) || (shape != null && !shape.isEmpty() && shape.bounds().maxY <= (entity.getY()-entity.position().y())))
                continue;

            if ((!(block instanceof IColoredBlock) || ((IColoredBlock) block).canClimb()) && entity.level.getBlockEntity(pos) instanceof InkColorTileEntity && ColorUtils.colorEquals(entity, entity.level.getBlockEntity(pos)) && !entity.isPassenger())
                return true;
        }
        return false;
    }

    public static InkBlockUtils.InkType getInkType(LivingEntity entity)
    {
        return PlayerInfoCapability.hasCapability(entity) ? PlayerInfoCapability.get(entity).getInkType() : InkType.NORMAL;
    }

    public static InkType checkInkType(LivingEntity entity)
    {
        return checkInkType(checkInkTypeStack(entity));
    }

    public static InkType checkInkType(ItemStack stack)
    {
        if(!stack.isEmpty())
            for(InkType t : InkType.values.values())
                if(t.getRepItem().equals(stack.getItem()))
                    return t;

        return InkType.NORMAL;
    }

    public static ItemStack checkInkTypeStack(LivingEntity entity)
    {

        if(entity instanceof PlayerEntity)
        {
            PlayerInventory inv = ((PlayerEntity) entity).inventory;
            final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(inv.offhand, inv.armor, inv.items);

            for(List<ItemStack> list : allInventories)
            {
                for(ItemStack stack : list)
                    if (stack.getItem().is(SplatcraftTags.Items.INK_BANDS))
                    {
                        for(InkType t : InkType.values.values())
                            if(t.getRepItem().equals(stack.getItem()))
                                return stack;
                    }
            }

        }

        return ItemStack.EMPTY;
    }
    public static ItemStack checkInkTypeStack(LivingEntity entity, InkType type)
    {

        if(entity instanceof PlayerEntity)
        {
            PlayerInventory inv = ((PlayerEntity) entity).inventory;
            final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(inv.offhand, inv.armor, inv.items);

            for(List<ItemStack> list : allInventories)
            {
                for(ItemStack stack : list)
                    if (stack.getItem().is(SplatcraftTags.Items.INK_BANDS))
                    {
                        if(type.getRepItem().equals(stack.getItem()))
                            return stack;
                    }
            }

        }

        return ItemStack.EMPTY;
    }

    public static InkType getInkType(BlockState state)
    {
        for(InkType type : InkType.values.values())
        {
            if(type.block.equals(state.getBlock()))
                return type;
        }
        return InkType.NORMAL;
    }

    public static class InkType implements Comparable<InkType>, IStringSerializable
    {
        public static final HashMap<ResourceLocation, InkType> values = new HashMap<>();

        public static final InkType NORMAL = new InkType(new ResourceLocation(Splatcraft.MODID, "normal"), SplatcraftBlocks.inkedBlock);
        public static final InkType GLOWING = new InkType(new ResourceLocation(Splatcraft.MODID, "splatfest_band"), SplatcraftItems.splatfestBand, SplatcraftBlocks.glowingInkedBlock);
        public static final InkType CLEAR = new InkType(new ResourceLocation(Splatcraft.MODID, "clear_band"), SplatcraftItems.clearBand, SplatcraftBlocks.clearInkedBlock);

        private final ResourceLocation name;
        private final Item repItem;

        private final InkedBlock block;

        public InkType(ResourceLocation name, Item repItem, InkedBlock inkedBlock)
        {
            values.put(name, this);
            this.name = name;
            this.repItem = repItem;
            this.block = inkedBlock;
        }

        public InkType(ResourceLocation name, InkedBlock inkedBlock)
        {
            this(name, Items.AIR, inkedBlock);
        }

        @Override
        public int compareTo(InkType o)
        {
            return getName().compareTo(o.getName());
        }

        public ResourceLocation getName()
        {
            return name;
        }

        public Item getRepItem()
        {
            return repItem;
        }

        @Override
        public String toString() {
            return name.toString();
        }

        @Override
        public String getSerializedName() {
            return getName().toString();
        }
    }
}
