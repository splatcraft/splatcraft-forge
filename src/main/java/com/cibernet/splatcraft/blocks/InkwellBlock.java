package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class InkwellBlock extends Block implements IColoredBlock, IWaterLoggable
{
	private static final VoxelShape SHAPE = VoxelShapes.or(
			makeCuboidShape(0, 0, 0, 16, 12, 16),
			makeCuboidShape(1, 12, 1, 14/16f, 13, 14),
			makeCuboidShape(0, 13, 0, 16, 16, 16));
	
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public InkwellBlock()
	{
		super(Properties.create(Material.GLASS).hardnessAndResistance(0.35f).harvestTool(ToolType.PICKAXE));
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(false)));
		
		SplatcraftBlocks.inkColoredBlocks.add(this);
	}
	
	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos)
	{
		return ColorUtils.hexToRGB(getColor((World) world, pos));
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return ColorUtils.setInkColor(super.getPickBlock(state, target, world, pos, player), getColor((World) world, pos));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		
		return getDefaultState().with(WATERLOGGED, Boolean.valueOf(context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
	
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}
	
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
	
	@Override
	public ItemStack getItem(IBlockReader reader, BlockPos pos, BlockState state)
	{
		ItemStack stack = super.getItem(reader, pos, state);
		
		if(reader.getTileEntity(pos) instanceof InkColorTileEntity)
			ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getTileEntity(pos)));
		
		return stack;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
	{
		if(!world.isRemote && stack.getTag() != null && world.getTileEntity(pos) instanceof InkColorTileEntity)
			ColorUtils.setInkColor(world.getTileEntity(pos), ColorUtils.getInkColor(stack));
		super.onBlockPlacedBy(world, pos, state, entity, stack);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return SplatcraftTileEntitites.inkwellTileEntity.get().create();
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
	public int getColor(World world, BlockPos pos)
	{
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
			return ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
		return -1;
	}
	
	@Override
	public void remoteColorChange(World world, BlockPos pos, int newColor)
	{
		BlockState state = world.getBlockState(pos);
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
			((InkColorTileEntity) world.getTileEntity(pos)).setColor(newColor);
		world.notifyBlockUpdate(pos, state, state, 2);
	}
	
	@Override
	public void remoteInkClear(World world, BlockPos pos)
	{
	
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return false;
	}
}
