package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.ColoredBarrierTileEntity;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ColoredBarrierBlock extends StageBarrierBlock implements IColoredBlock
{
    public final boolean blocksColor;
    public ColoredBarrierBlock(String name, boolean blocksColor)
    {
        super(name, false);
        this.blocksColor = blocksColor;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return SplatcraftTileEntitites.colorBarrierTileEntity.create();
    }

    @Override
    public boolean setColor(World world, BlockPos pos, int color)
    {
        BlockState state = world.getBlockState(pos);
        if(world.getTileEntity(pos) instanceof ColoredBarrierTileEntity)
        {
            ((ColoredBarrierTileEntity) world.getTileEntity(pos)).setColor(color);
            world.notifyBlockUpdate(pos, state, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public int getColor(World world, BlockPos pos)
    {
        if(world.getTileEntity(pos) instanceof ColoredBarrierTileEntity)
            return ((ColoredBarrierTileEntity) world.getTileEntity(pos)).getColor();
        return -1;
    }


    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, world, pos, player), getColor((World) world, pos)), true);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if(context.getEntity() instanceof LivingEntity && ColorUtils.getEntityColor((LivingEntity) context.getEntity()) > -1)
            return !canAllowThrough(pos, context.getEntity()) ? super.getCollisionShape(state, worldIn, pos, context) : VoxelShapes.empty();
        return blocksColor ? super.getCollisionShape(state, worldIn, pos, context) : VoxelShapes.empty();
    }

    public boolean canAllowThrough(BlockPos pos, Entity entity)
    {
        return blocksColor != ColorUtils.colorEquals(entity, entity.world.getTileEntity(pos));
    }

    @Override
    public boolean canClimb() {
        return false;
    }

    @Override
    public boolean canSwim() {
        return false;
    }

    @Override
    public boolean canDamage() {
        return false;
    }

    @Override
    public boolean remoteColorChange(World world, BlockPos pos, int newColor)
    {
        return setColor(world, pos, newColor);
    }


    @Override
    public boolean remoteInkClear(World world, BlockPos pos) {
        return false;
    }
}
