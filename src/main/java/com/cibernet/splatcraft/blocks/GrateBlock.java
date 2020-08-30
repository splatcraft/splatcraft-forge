package com.cibernet.splatcraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.ToolType;

public class GrateBlock extends AbstractSquidPassthroughBlock implements IWaterLoggable
{
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public static final Properties PROPERTIES = Properties.create(Material.IRON).notSolid().setRequiresTool().harvestTool(ToolType.PICKAXE).hardnessAndResistance(4.0f).sound(SoundType.METAL);
	
	public GrateBlock(String name)
	{
		super(PROPERTIES);
		this.setDefaultState(this.stateContainer.getBaseState().with(HALF, Half.BOTTOM).with(WATERLOGGED, Boolean.valueOf(false)));
		setRegistryName(name);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return state.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
	}
	
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
	{
		if(type ==  PathType.WATER)
			return state.get(WATERLOGGED);
		return true;
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = this.getDefaultState();
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		Direction direction = context.getFace();
		if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal())
			blockstate = blockstate.with(HALF, context.getHitVec().y - (double)context.getPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
		else blockstate = blockstate.with(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
		
		return blockstate.with(WATERLOGGED, Boolean.valueOf(fluidstate.getFluid() == Fluids.WATER));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HALF, WATERLOGGED);
	}
	
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 */
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
}
