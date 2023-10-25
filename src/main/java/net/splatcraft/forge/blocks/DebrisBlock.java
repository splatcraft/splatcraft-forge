package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DebrisBlock extends Block
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

	private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>()
	{{
		put(Direction.WEST, box(2.4, 0, 0, 15.2, 8, 16));
		put(Direction.NORTH, box(0, 0, 2.4, 16, 8, 15.2));
		put(Direction.EAST, box(0.8, 0, 0, 13.6, 8, 16));
		put(Direction.SOUTH, box(0, 0, 0.8, 16, 8, 13.6));
	}};


	public DebrisBlock(Material material, MaterialColor color)
	{
		super(BlockBehaviour.Properties.of(material, color).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).lightLevel(
				(state) -> 1
		));
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(DIRECTION, Direction.NORTH));
	}

	@Override
	public boolean useShapeForLightOcclusion(@NotNull BlockState state)
	{
		return true;
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter levelIn, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return SHAPES.get(state.getValue(DIRECTION));
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
		return defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection())
				.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED, DIRECTION);
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
