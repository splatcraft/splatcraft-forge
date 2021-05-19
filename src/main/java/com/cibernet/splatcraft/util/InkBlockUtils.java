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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InkBlockUtils
{
    /*
    public static TreeMap<InkType, InkBlocks> inkTypeMap = new TreeMap<InkType, InkBlocks>()
    {{
        put(InkType.NORMAL, new InkBlocks(SplatcraftBlocks.inkedBlock).put(StairsBlock.class, SplatcraftBlocks.inkedStairs).put(SlabBlock.class, SplatcraftBlocks.inkedSlab));
        put(InkType.GLOWING, new InkBlocks(SplatcraftBlocks.glowingInkedBlock).put(StairsBlock.class, SplatcraftBlocks.glowingInkedStairs).put(SlabBlock.class, SplatcraftBlocks.glowingInkedSlab));
    }};
    */

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

        BlockState inkState = getInkState(inkType, world, pos);

        InkedBlockTileEntity inkte = (InkedBlockTileEntity) SplatcraftBlocks.inkedBlock.createTileEntity(inkState, world);
        if (inkte == null)
        {
            return false;
        }
        inkte.setColor(color);
        inkte.setSavedState(state);

        world.setBlockState(pos, inkState, 0);
        world.setTileEntity(pos, inkte);
        world.notifyBlockUpdate(pos, inkState, inkState, 3);

        for(Direction facing : Direction.values())
        {
            if(world.getTileEntity(pos.offset(facing)) instanceof InkedBlockTileEntity)
            {
                InkedBlockTileEntity otherTe = (InkedBlockTileEntity) world.getTileEntity(pos.offset(facing));
                otherTe.setSavedState(otherTe.getSavedState().getBlock().updatePostPlacement(otherTe.getSavedState(), facing.getOpposite(), inkState, world, pos.offset(facing), pos));
            }
        }

        return true;
    }


    public static BlockState getInkState(InkType inkType, World world, BlockPos pos)
    {
        if(world.getBlockState(pos).getBlock() instanceof InkedBlock)
            return inkType.block.getDefaultState();
        return inkType.block.getDefaultState();

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

        if (!(world.getTileEntity(pos) instanceof InkColorTileEntity) && world.getTileEntity(pos) != null)
            return false;


        if(world.getBlockState(pos).getBlock() instanceof  StairsBlock || world.getBlockState(pos).getBlock() instanceof SlabBlock)
            return true;

        if (SplatcraftTags.Blocks.INKABLE_BLOCKS.contains(block))
            return true;

        if (canInkPassthrough(world, pos))
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
        BlockPos result;
        for(double i = 0; i >= -0.5; i-=0.1)
        {
            result = new BlockPos(entity.getPosX(), entity.getPosY()+i, entity.getPosZ());

            if(!(entity.world.getBlockState(result).getMaterial().equals(Material.AIR) || entity.world.getBlockState(result).getBlock().getCollisionShape(entity.world.getBlockState(result), entity.world, result).isEmpty()))
                return result;
        }

        return new BlockPos(entity.getPosX(), entity.getPosY()-0.6, entity.getPosZ());
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
            final List<NonNullList<ItemStack>> allInventories = ImmutableList.of(inv.offHandInventory, inv.armorInventory, inv.mainInventory);

            for(List<ItemStack> list : allInventories)
            {
                for(ItemStack stack : list)
                    if (stack.getItem().isIn(SplatcraftTags.Items.INK_BANDS))
                    {
                        for(InkType t : InkType.values.values())
                            if(t.getRepItem().equals(stack.getItem()))
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
        public String getString() {
            return getName().toString();
        }
    }
}
