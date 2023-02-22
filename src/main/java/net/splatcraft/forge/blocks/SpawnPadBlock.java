package net.splatcraft.forge.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpawnPadBlock extends Block implements IColoredBlock, IWaterLoggable
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 6.1, 16);

	private Aux auxBlock;

	public SpawnPadBlock()
	{
		super(Properties.of(Material.GLASS).strength(0.35f).harvestTool(ToolType.PICKAXE));
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(DIRECTION, Direction.NORTH));

		SplatcraftBlocks.inkColoredBlocks.add(this);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

	@Override
	public Optional<Vector3d> getRespawnPosition(BlockState state, EntityType<?> type, IWorldReader world, BlockPos pos, float orientation, @Nullable LivingEntity entity)
	{
		if(!ColorUtils.colorEquals(entity, world.getBlockEntity(pos)))
			return Optional.empty();

		Vector3d vec = TransportationHelper.findSafeDismountLocation(type, world, pos, false);

		return vec == null ? Optional.empty() : Optional.of(vec);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
	{
		return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, level, pos, player), getColor((World) level, pos)), true);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{

		for(Direction dir : Direction.values()) {
			if (dir.get2DDataValue() < 0)
				continue;

			for (int i = 0; i <= 1; i++)
				if(!context.getLevel().getBlockState(context.getClickedPos().relative(dir).relative(dir.getCounterClockWise(), i)).canBeReplaced(context))
					return null;
		}
		return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER).setValue(DIRECTION, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED).add(DIRECTION);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
	{
		if (stateIn.getValue(WATERLOGGED))
		{
			levelIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
		}

		return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.BLOCK;
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state)
	{
		ItemStack stack = super.getCloneItemStack(reader, pos, state);

		if (reader.getBlockEntity(pos) instanceof InkColorTileEntity)
			ColorUtils.setColorLocked(ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getBlockEntity(pos))), true);

		return stack;
	}

	@Override
	public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
		return false;
	}

	@Override
	public boolean isPossibleToRespawnInThis()
	{
		return true;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
	{
		if (!level.isClientSide && stack.getTag() != null && level.getBlockEntity(pos) instanceof InkColorTileEntity)
		{
			ColorUtils.setInkColor(level.getBlockEntity(pos), ColorUtils.getInkColor(stack));
		}

		for(Direction dir : Direction.values())
		{
			if(dir.get2DDataValue() < 0)
				continue;

			for(int i = 0; i <= 1; i++)
			{
				BlockPos auxPos = pos.relative(dir).relative(dir.getCounterClockWise(), i);
				level.setBlock(auxPos, auxBlock.defaultBlockState()
						.setValue(WATERLOGGED, level.getFluidState(auxPos).getType() == Fluids.WATER)
						.setValue(DIRECTION, dir)
						.setValue(Aux.IS_CORNER, i == 1), 3);
			}
		}
		level.blockUpdated(pos, Blocks.AIR);
		state.updateNeighbourShapes(level, pos, 3);

		super.setPlacedBy(level, pos, state, entity, stack);
	}

	@Override
	public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_)
	{
		super.playerWillDestroy(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level)
	{
		return SplatcraftTileEntitites.spawnPadTileEntity.create();
	}

	@Override
	public boolean canClimb()
	{
		return false;
	}

	@Override
	public boolean canSwim()
	{
		return true;
	}

	@Override
	public boolean canDamage()
	{
		return false;
	}

	@Override
	public int getColor(World level, BlockPos pos)
	{
		if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
		{
			InkColorTileEntity tileEntity = (InkColorTileEntity) level.getBlockEntity(pos);
			if (tileEntity != null)
			{
				return tileEntity.getColor();
			}
		}
		return -1;
	}

	@Override
	public boolean remoteColorChange(World level, BlockPos pos, int newColor)
	{
		BlockState state = level.getBlockState(pos);
		TileEntity tileEntity = level.getBlockEntity(pos);
		if (tileEntity instanceof InkColorTileEntity && ((InkColorTileEntity) tileEntity).getColor() != newColor)
		{
			((InkColorTileEntity) tileEntity).setColor(newColor);
			level.sendBlockUpdated(pos, state, state, 3);
			state.updateNeighbourShapes(level, pos, 3);
			return true;
		}
		return false;
	}

	@Override
	public boolean remoteInkClear(World level, BlockPos pos)
	{
		return false;
	}

	public static class Aux extends Block implements IColoredBlock, IWaterLoggable
	{
		public static final BooleanProperty IS_CORNER = BooleanProperty.create("corner");

		private static final VoxelShape[] SHAPES = new VoxelShape[8];

		final SpawnPadBlock parent;

		public Aux(SpawnPadBlock parent)
		{
			super(parent.properties);
			this.parent = parent;

			parent.auxBlock = this;

			this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(DIRECTION, Direction.NORTH).setValue(IS_CORNER, false));
		}


		@Override
		public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context)
		{
			int i = state.getValue(DIRECTION).get2DDataValue()*2 + (state.getValue(IS_CORNER) ? 1 : 0);

			if(i < 0)
				return VoxelShapes.empty();

			if(SHAPES[i] == null)
				SHAPES[i] = VoxelShapes.or(
						BarrierBarBlock.modifyShapeForDirection(state.getValue(DIRECTION), Block.box(state.getValue(IS_CORNER) ? 8 : 0, 0, 8, 16, 6, 16)),
						BarrierBarBlock.modifyShapeForDirection(state.getValue(DIRECTION).getOpposite(), Block.box( 0, 6, 6,state.getValue(IS_CORNER) ? 7 : 16, 7, 7)),
						BarrierBarBlock.modifyShapeForDirection(state.getValue(DIRECTION), Block.box(state.getValue(IS_CORNER) ? 10 : 0, 0, 10, 16, 6.1, 16)),
						state.getValue(IS_CORNER) ? BarrierBarBlock.modifyShapeForDirection(state.getValue(DIRECTION), Block.box(9, 6, 10,  10, 7, 16)) : VoxelShapes.empty());

			return SHAPES[i];
		}


		public BlockPos getParentPos(BlockState state, BlockPos pos)
		{
			return pos.relative(state.getValue(DIRECTION).getOpposite()).relative(state.getValue(DIRECTION).getClockWise(), state.getValue(IS_CORNER) ? 1 : 0);
		}

		@Nullable
		@Override
		public BlockState getStateForPlacement(BlockItemUseContext context)
		{
			return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
		}

		@Override
		protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
		{
			builder.add(WATERLOGGED).add(DIRECTION).add(IS_CORNER);
		}

		@Override
		public FluidState getFluidState(BlockState state)
		{
			return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
		}

		@Override
		public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
		{
			if(levelIn.getBlockState(getParentPos(stateIn, currentPos)).getBlock() != parent)
				return Blocks.AIR.defaultBlockState();

			if (stateIn.getValue(WATERLOGGED))
			{
				levelIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
			}

			return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
		}

		@Override
		public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean p_196243_5_)
		{
			BlockPos parentPos = getParentPos(state, pos);
			if(level.getBlockState(parentPos).getBlock() == parent)
				level.destroyBlock(parentPos, p_196243_5_);

			super.onRemove(state, level, pos, newState, p_196243_5_);
		}

		@Override
		public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
		{
			BlockPos parentPos = getParentPos(state, pos);
			if(player.level.getBlockState(parentPos).getBlock() == parent)
				player.level.destroyBlock(parentPos, !player.isCreative(), player);

			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
		}

		@Override
		public PushReaction getPistonPushReaction(BlockState state)
		{
			return PushReaction.BLOCK;
		}

		@Override
		public boolean canClimb() {
			return false;
		}

		@Override
		public boolean canSwim() {
			return true;
		}

		@Override
		public boolean canDamage() {
			return false;
		}

		@Override
		public boolean remoteColorChange(World level, BlockPos pos, int newColor)
		{
			return parent.remoteColorChange(level, getParentPos(level.getBlockState(pos), pos), newColor);
		}

		@Override
		public boolean remoteInkClear(World level, BlockPos pos)
		{
			return false;
		}

		@Override
		public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
			return false;
		}

		@Override
		public Optional<Vector3d> getRespawnPosition(BlockState state, EntityType<?> type, IWorldReader world, BlockPos pos, float orientation, @Nullable LivingEntity entity)
		{
			BlockPos parentPos = getParentPos(state, pos);
			return parent.getRespawnPosition(world.getBlockState(parentPos), type, world, parentPos, orientation, entity);
		}

		@Override
		public ItemStack getCloneItemStack(IBlockReader level, BlockPos pos, BlockState state)
		{
			BlockPos parentPos = getParentPos(state, pos);
			return parent.getCloneItemStack(level, parentPos, level.getBlockState(parentPos));
		}

		@Override
		public BlockRenderType getRenderShape(BlockState state) {
			return BlockRenderType.INVISIBLE;
		}
	}
}
