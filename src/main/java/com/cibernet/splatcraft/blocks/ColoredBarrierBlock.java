package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.ColoredBarrierTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
        if(world.getTileEntity(pos) instanceof ColoredBarrierTileEntity)
        {
            ((ColoredBarrierTileEntity) world.getTileEntity(pos)).setColor(color);
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
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if(context.getEntity() instanceof LivingEntity && ColorUtils.getEntityColor((LivingEntity) context.getEntity()) > -1)
            return blocksColor == ColorUtils.colorEquals((LivingEntity) context.getEntity(), worldIn.getTileEntity(pos)) ? super.getCollisionShape(state, worldIn, pos, context) : VoxelShapes.empty();
        return blocksColor ? super.getCollisionShape(state, worldIn, pos, context) : VoxelShapes.empty();
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
    public boolean remoteColorChange(World world, BlockPos pos, int newColor) {
        return false;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos) {
        return false;
    }
}
