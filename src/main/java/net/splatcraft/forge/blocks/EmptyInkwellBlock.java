package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.dispenser.PlaceBlockDispenseBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class EmptyInkwellBlock extends AbstractGlassBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 12, 16),
            box(1, 12, 1, 14, 13, 14),
            box(0, 13, 0, 16, 16, 16));


    public EmptyInkwellBlock(Properties properties)
    {
        super(properties.noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));

        DispenserBlock.registerBehavior(this, new PlaceBlockDispenseBehavior());
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state)
    {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter levelIn, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState state)
    {
        return PushReaction.DESTROY;
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {

        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor levelIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos)
    {
        if (stateIn.getValue(WATERLOGGED))
        {
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

}
