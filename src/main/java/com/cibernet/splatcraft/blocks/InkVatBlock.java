package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkVatTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class InkVatBlock extends ContainerBlock implements IColoredBlock
{
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public InkVatBlock(String name)
	{
		super(Properties.create(Material.IRON));
		setRegistryName(name);
		SplatcraftBlocks.inkColoredBlocks.add(this);
		
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(ACTIVE, false).with(POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, ACTIVE, POWERED);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if(worldIn.isRemote)
			return ActionResultType.SUCCESS;
		
		if(worldIn.getTileEntity(pos) instanceof InkVatTileEntity)
		{
			NetworkHooks.openGui((ServerPlayerEntity) player, (InkVatTileEntity) worldIn.getTileEntity(pos), pos);
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.FAIL;
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
		return SplatcraftTileEntitites.inkVatTileEntity.create();
	}
	
	@Override
	public boolean canClimb()
	{
		return false;
	}
	
	@Override
	public boolean canSwim()
	{
		return false;
	}
	
	@Override
	public boolean canDamage()
	{
		return false;
	}
	
	@Override
	public boolean remoteColorChange(World world, BlockPos pos, int newColor)
	{
		return false;
	}
	
	@Override
	public boolean remoteInkClear(World world, BlockPos pos)
	{
		return false;
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return false;
	}
	
	@Override
	public int getColor(World world, BlockPos pos)
	{
		if(world.getTileEntity(pos) instanceof InkVatTileEntity)
			return ((InkVatTileEntity) world.getTileEntity(pos)).getColor();
		return -1;
	}
	
	@Override
	public boolean setColor(World world, BlockPos pos, int color)
	{
		if(!(world.getTileEntity(pos) instanceof InkVatTileEntity))
			return false;
		((InkVatTileEntity) world.getTileEntity(pos)).setColor(color);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new InkVatTileEntity();
	}
	
	
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
	
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof InkVatTileEntity) {
				((InkVatTileEntity)tileentity).setCustomName(stack.getDisplayName());
			}
		}
		
	}
	
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.isIn(newState.getBlock())) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof InkVatTileEntity) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (InkVatTileEntity)tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
			
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}
	
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}
	
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		boolean isPowered = worldIn.isBlockPowered(pos);
		if (isPowered != state.get(POWERED))
		{
			if (isPowered && worldIn.getTileEntity(pos) instanceof InkVatTileEntity)
				((InkVatTileEntity) worldIn.getTileEntity(pos)).onRedstonePulse();
			
			worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(isPowered)), 3);
		}
		
	}
}
