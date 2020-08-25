package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class InkedBlock extends Block implements IColoredBlock
{
	public static final Properties DEFAULT_PROPERTIES = Properties.create(Material.CLAY, MaterialColor.BLACK_TERRACOTTA).tickRandomly().harvestTool(ToolType.PICKAXE).setRequiresTool().sound(SoundType.SLIME);
	public static final int GLOWING_LIGHT_LEVEL = 6;
	
	
	public InkedBlock(String name)
	{
		this(name, DEFAULT_PROPERTIES);
	}
	
	public InkedBlock(String name, Properties properties)
	{
		super(properties);
		SplatcraftBlocks.inkColoredBlocks.add(this);
		setRegistryName(name);
	}
	
	public static InkedBlock glowing(String name)
	{
		return new InkedBlock(name, DEFAULT_PROPERTIES.setLightLevel(state -> GLOWING_LIGHT_LEVEL));
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
		{
			BlockState savedState = ((InkedBlockTileEntity) world.getTileEntity(pos)).getSavedState();
			return savedState.getBlock().getPickBlock(savedState, target, world, pos, player);
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		InkedBlockTileEntity te = (InkedBlockTileEntity) worldIn.getTileEntity(pos);
		
		if(te.getSavedState().getBlock() instanceof InkedBlock)
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
			
		
		return te.getSavedState().getBlock().getPlayerRelativeBlockHardness(te.getSavedState(), player, worldIn, pos);
	}
	
	/* TODO modular ink
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		//return VoxelShapes.empty();
		
		if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
			//return super.getShape(state, worldIn, pos, context);
			return VoxelShapes.empty();
		BlockState savedState = ((InkedBlockTileEntity) worldIn.getTileEntity(pos)).getSavedState();
		
		if(savedState == null || savedState.getBlock().equals(this))
			return super.getShape(state, worldIn, pos, context);
		return savedState.getBlock().getShape(savedState, worldIn, pos, context);
		
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
			return super.getCollisionShape(state, worldIn, pos, context);
		BlockState savedState = ((InkedBlockTileEntity) worldIn.getTileEntity(pos)).getSavedState();
		
		if(savedState == null || savedState.getBlock().equals(this))
			return super.getCollisionShape(state, worldIn, pos, context);
		return savedState.getBlock().getCollisionShape(savedState, worldIn, pos, context);
		
		
	}
	
	@Override
	public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity)
	{
		return true;
		/*
		if(!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
		return super.collisionExtendsVertically(state, world, pos, collidingEntity);
		BlockState savedState = ((InkedBlockTileEntity) world.getTileEntity(pos)).getSavedState();
		
		if(savedState == null || savedState.getBlock().equals(this))
			return super.collisionExtendsVertically(state, world, pos, collidingEntity);
		return savedState.getBlock().collisionExtendsVertically(savedState, world, pos, collidingEntity);
		//
	}
	
	*/
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Override
	public float getJumpFactor()
	{
		
		return super.getJumpFactor();
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(world.getGameRules().getBoolean(SplatcraftGameRules.INK_DECAY) && world.getTileEntity(pos) instanceof InkedBlockTileEntity)
		{
			clearInk(world, pos);
		}
	}
	
	public static boolean isTouchingLiquid(IBlockReader reader, BlockPos pos) {
		boolean flag = false;
		BlockPos.Mutable blockpos$mutable = pos.toMutable();
		
		BlockState currentState = reader.getBlockState(pos);
		
		if(currentState.hasProperty(BlockStateProperties.WATERLOGGED) && currentState.get(BlockStateProperties.WATERLOGGED))
			return true;
		
		for(Direction direction : Direction.values()) {
			BlockState blockstate = reader.getBlockState(blockpos$mutable);
			if (direction != Direction.DOWN || causesClear(blockstate)) {
				blockpos$mutable.func_239622_a_(pos, direction);
				blockstate = reader.getBlockState(blockpos$mutable);
				if (causesClear(blockstate) && !blockstate.isSolidSide(reader, pos, direction.getOpposite())) {
					flag = true;
					break;
				}
			}
		}
		
		return flag;
	}
	
	private static boolean causesClear(BlockState state) {
		return state.getFluidState().isTagged(FluidTags.WATER);
	}
	
	private static BlockState clearInk(IWorld world, BlockPos pos)
	{
		InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
		if(te.hasSavedState())
		{
			world.setBlockState(pos, te.getSavedState(), 3);
			
			if(te.hasSavedColor() && te.getSavedState().getBlock() instanceof IColoredBlock)
			{
				((World)world).setTileEntity(pos, te.getSavedState().getBlock().createTileEntity(te.getSavedState(), world));
				if(world.getTileEntity(pos) instanceof InkColorTileEntity)
				{
					InkColorTileEntity newte = (InkColorTileEntity) world.getTileEntity(pos);
					newte.setColor(te.getSavedColor());
				}
			}
			
			return te.getSavedState();
		}
		
		return world.getBlockState(pos);
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		if(isTouchingLiquid(worldIn, currentPos))
		{
			if(worldIn.getTileEntity(currentPos) instanceof InkedBlockTileEntity)
				return clearInk(worldIn, currentPos);
		}
		
		if(worldIn.getTileEntity(currentPos) instanceof InkedBlockTileEntity)
		{
			BlockState savedState = ((InkedBlockTileEntity) worldIn.getTileEntity(currentPos)).getSavedState();
			
			if(savedState != null && !savedState.getBlock().equals(this))
				((InkedBlockTileEntity) worldIn.getTileEntity(currentPos)).setSavedState(savedState.getBlock().updatePostPlacement(savedState, facing, facingState, worldIn, currentPos, facingPos));
		}
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return SplatcraftTileEntitites.inkedTileEntity.create();
	}
	
	@Override
	public boolean canClimb()
	{
		return true;
	}
	
	@Override
	public boolean canSwim()
	{
		return true;
	}
	
	@Override
	public boolean canDamage()
	{
		return true;
	}
	
	@Override
	public int getColor(World world, BlockPos pos)
	{
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
			return ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
		return -1;
	}
	
	@Override
	public boolean remoteColorChange(World world, BlockPos pos, int newColor)
	{
		
		return false;
	}
	
	@Override
	public boolean remoteInkClear(World world, BlockPos pos)
	{
		BlockState oldState = world.getBlockState(pos);
		if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
			return !clearInk(world, pos).equals(oldState);
		return false;
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return true;
	}
	
	@Override
	public boolean inkBlock(World world, BlockPos pos, int color,float damage,  InkBlockUtils.InkType inkType)
	{
		if(!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
			return false;
		
		InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
		BlockState oldState = world.getBlockState(pos);
		BlockState state = world.getBlockState(pos);
		
		if(te.getColor() != color)
			te.setColor(color);
		if(InkBlockUtils.getInkBlock(inkType, state.getBlock()) != state.getBlock())
		{
			state = InkBlockUtils.getInkState(inkType, state);
			world.setBlockState(pos, state, 2);
			InkedBlockTileEntity newTe = (InkedBlockTileEntity) world.getTileEntity(pos);
			newTe.setSavedState(te.getSavedState());
			newTe.setColor(te.getColor());
			
			world.setTileEntity(pos, newTe);
		}
		else world.notifyBlockUpdate(pos, oldState, state, 2);
		return !(te.getColor() == color && InkBlockUtils.getInkBlock(inkType, state.getBlock()) == state.getBlock());
	}
}
