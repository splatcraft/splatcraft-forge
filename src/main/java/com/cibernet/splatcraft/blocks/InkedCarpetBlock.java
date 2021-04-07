package com.cibernet.splatcraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class InkedCarpetBlock extends InkStainedBlock
{
    public InkedCarpetBlock(String name)
    {
        super(name, Properties.create(Material.CARPET).hardnessAndResistance(0.1F).sound(SoundType.CLOTH));
    }

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public VoxelShape getShape(BlockState st, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        return !state.isValidPosition(world, pos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, direction, facingState, world, pos, facingPos);
    }

    public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return !p_196260_2_.isAirBlock(p_196260_3_.down());
    }
}
