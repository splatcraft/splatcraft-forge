package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.AbstractSquidPassthroughBlock;
import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.IInkPassthrough;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftStats;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import java.util.*;

public class InkBlockUtils
{

    public static TreeMap<InkType, InkBlocks> inkTypeMap = new TreeMap<InkType, InkBlocks>()
    {{
        put(InkType.NORMAL, new InkBlocks(SplatcraftBlocks.inkedBlock).put(StairsBlock.class, SplatcraftBlocks.inkedStairs).put(SlabBlock.class, SplatcraftBlocks.inkedSlab));
        put(InkType.GLOWING, new InkBlocks(SplatcraftBlocks.glowingInkedBlock).put(StairsBlock.class, SplatcraftBlocks.glowingInkedStairs).put(SlabBlock.class, SplatcraftBlocks.glowingInkedSlab));
    }};

    public static boolean playerInkBlock(PlayerEntity player, World world, BlockPos pos, int color, float damage, InkType inkType)
    {
        boolean inked = inkBlock(world, pos, color, damage, inkType);

        if (inked)
        {
            player.addStat(SplatcraftStats.BLOCKS_INKED);
        }

        return inked;
    }

    public static boolean inkBlock(World world, BlockPos pos, int color, float damage, InkType inkType)
    {
        BlockState state = world.getBlockState(pos);
        TileEntity te = world.getTileEntity(pos);

        if (InkedBlock.isTouchingLiquid(world, pos))
            return false;

        if (state.getBlock() instanceof IColoredBlock)
            return ((IColoredBlock) state.getBlock()).inkBlock(world, pos, color, damage, inkType);

        if (!canInk(world, pos))
            return false;

        BlockState inkState = getInkState(inkType, state);
        world.setBlockState(pos, inkState, 3);

        world.setTileEntity(pos, SplatcraftBlocks.inkedBlock.createTileEntity(inkState, world));
        InkedBlockTileEntity inkte = (InkedBlockTileEntity) world.getTileEntity(pos);
        if (inkte == null)
        {
            return false;
        }
        inkte.setColor(color);
        inkte.setSavedState(state);
        return true;
    }

    public static BlockState getInkState(InkType type, BlockState baseState)
    {
        BlockState inkState = getInkBlock(type, baseState.getBlock()).getDefaultState();

        for (Property<?> property : baseState.getProperties())
        {
            if (inkState.hasProperty(property))
            {
                inkState = mergeProperty(inkState, baseState, property);
            }

        }

        return inkState;
    }

    private static <T extends Comparable<T>> BlockState mergeProperty(BlockState state, BlockState baseState, Property<T> property)
    {
        T value = baseState.get(property);
        return state.with(property, value);
    }


    public static boolean canInkFromFace(World world, BlockPos pos, Direction face)
    {
        if(!(world.getBlockState(pos).getBlock() instanceof IColoredBlock) && !canInk(world, pos))
            return false;

        return canInkPassthrough(world, pos.offset(face)) || !world.getBlockState(pos.offset(face)).isIn(SplatcraftTags.Blocks.BLOCKS_INK);
    }

    public static boolean canInk(World world, BlockPos pos)
    {

        if (InkedBlock.isTouchingLiquid(world, pos))
            return false;

        Block block = world.getBlockState(pos).getBlock();

        if (SplatcraftTags.Blocks.UNINKABLE_BLOCKS.contains(block))
            return false;

        if (block instanceof StairsBlock || block instanceof SlabBlock)
            return true;

        if (!(world.getTileEntity(pos) instanceof InkColorTileEntity) && world.getTileEntity(pos) != null)
            return false;

        if (SplatcraftTags.Blocks.INKABLE_BLOCKS.contains(block))
            return true;

        if (canInkPassthrough(world, pos))
            return false;

        if (!world.getBlockState(pos).isOpaqueCube(world, pos))
            return false;

        return !block.isTransparent(world.getBlockState(pos));
    }

    public static boolean canInkPassthrough(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof AbstractSquidPassthroughBlock || state.getBlock() instanceof IInkPassthrough)
        {
            return true;
        }

        return state.getCollisionShape(world, pos).isEmpty();
    }

    public static boolean canSquidHide(LivingEntity entity)
    {
        return (entity.isOnGround() || !entity.world.getBlockState(new BlockPos(entity.getPosX(), entity.getPosY() - 0.1, entity.getPosZ())).getBlock().equals(Blocks.AIR))
                && canSquidSwim(entity) || canSquidClimb(entity);
    }

    public static boolean canSquidSwim(LivingEntity entity)
    {
        boolean canSwim = false;

        BlockPos down = getBlockStandingOnPos(entity);

        Block standingBlock = entity.world.getBlockState(down).getBlock();
        if (standingBlock instanceof IColoredBlock)
            canSwim = ((IColoredBlock) standingBlock).canSwim();

        if (canSwim)
            return ColorUtils.colorEquals(entity, entity.world.getTileEntity(down));
        return false;
    }

    public static BlockPos getBlockStandingOnPos(Entity entity)
    {
        BlockPos result = new BlockPos(entity.getPosX(), entity.getPosY()-0.4, entity.getPosZ());
        if(entity.world.getBlockState(result).getMaterial().equals(Material.AIR) || entity.world.getBlockState(result).allowsMovement(entity.world, result, PathType.LAND))
            result = new BlockPos(entity.getPosX(), entity.getPosY()-0.5001, entity.getPosZ());
        return result;
    }

    public static boolean onEnemyInk(LivingEntity entity)
    {
        if (!entity.isOnGround())
            return false;
        boolean canDamage = false;
        BlockPos pos = getBlockStandingOnPos(entity);

        if (entity.world.getBlockState(pos).getBlock() instanceof IColoredBlock)
            canDamage = ((IColoredBlock) entity.world.getBlockState(pos).getBlock()).canDamage();

        return canDamage && ColorUtils.getInkColor(entity.world.getTileEntity(pos)) != -1 && !canSquidSwim(entity);
    }

    public static boolean canSquidClimb(LivingEntity entity)
    {
        if (onEnemyInk(entity))
            return false;
        for (int i = 0; i < 4; i++)
        {
            float xOff = (i < 2 ? .32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .32f) * (i % 2 == 0 ? 1 : -1);
            BlockPos pos = new BlockPos(entity.getPosX() - xOff, entity.getPosY(), entity.getPosZ() - zOff);
            Block block = entity.world.getBlockState(pos).getBlock();
            VoxelShape shape = block.getCollisionShape(entity.world.getBlockState(pos), entity.world, pos);

            if(pos.equals(getBlockStandingOnPos(entity)) || (shape != null && !shape.isEmpty() && shape.getBoundingBox().maxY <= (entity.getPosY()-entity.getPosition().getY())))
                continue;

            if ((!(block instanceof IColoredBlock) || ((IColoredBlock) block).canClimb()) && entity.world.getTileEntity(pos) instanceof InkColorTileEntity && ColorUtils.colorEquals(entity, entity.world.getTileEntity(pos)) && !entity.isPassenger())
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
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).inventory.hasItemStack(new ItemStack(SplatcraftItems.splatfestBand)))
            return InkType.GLOWING;
        return InkType.NORMAL;
    }

    public static Block getInkBlock(InkType inkType, Block baseBlock)
    {
        return inkTypeMap.get(inkType).get(baseBlock);
    }

    public static class InkType implements Comparable<InkType>
    {
        public static final ArrayList<InkType> values = new ArrayList<>();

        public static final InkType NORMAL = new InkType(new ResourceLocation(Splatcraft.MODID, "normal"));
        public static final InkType GLOWING = new InkType(new ResourceLocation(Splatcraft.MODID, "splatfest_band"), SplatcraftItems.splatfestBand);

        private final ResourceLocation name;
        private final Item repItem;

        public InkType(ResourceLocation name, Item repItem)
        {
            values.add(this);
            this.name = name;
            this.repItem = repItem;
        }

        public InkType(ResourceLocation name)
        {
            this(name, Items.AIR);
        }

        @Override
        public int compareTo(InkType o)
        {
            return values.indexOf(this) - values.indexOf(o);
        }

        public int getIndex()
        {
            return values.indexOf(this);
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
    }

    public static class InkBlocks
    {
        final List<Map.Entry<Class<? extends Block>, Block>> blockMap = new ArrayList<>();
        Block defaultBlock;

        public InkBlocks(Block defaultBlock)
        {
            this.defaultBlock = defaultBlock;
        }

        public Block get(Block block)
        {
            if (blockMap.isEmpty())
            {
                return defaultBlock;
            }

            for (Map.Entry<Class<? extends Block>, Block> entry : blockMap)
            {
                if (entry.getKey().isInstance(block))
                {
                    return entry.getValue();
                }
            }

            return defaultBlock;
        }

        public InkBlocks put(Class<? extends Block> blockClass, Block block)
        {
            blockMap.add(new AbstractMap.SimpleEntry<>(blockClass, block));
            return this;
        }
    }
}
