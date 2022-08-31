package net.splatcraft.forge.blocks;

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
        super(name, Properties.of(Material.CLOTH_DECORATION).strength(0.1F).sound(SoundType.WOOL));
    }

    protected static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public VoxelShape getShape(BlockState st, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, IWorld level, BlockPos pos, BlockPos facingPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, facingState, level, pos, facingPos);
    }

    public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return !p_196260_2_.isEmptyBlock(p_196260_3_.below());
    }
}
