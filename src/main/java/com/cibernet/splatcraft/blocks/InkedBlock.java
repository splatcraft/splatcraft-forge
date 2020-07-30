package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.crafting.InkColor;
import com.cibernet.splatcraft.crafting.InkColorManager;
import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class InkedBlock extends Block implements IColoredBlock
{
	public InkedBlock()
	{
		super(Properties.create(Material.CLAY, MaterialColor.BLACK_TERRACOTTA).tickRandomly().harvestTool(ToolType.PICKAXE).setRequiresTool());
		SplatcraftBlocks.inkColoredBlocks.add(this);
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
			InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
			if(te.hasSavedState())
				world.setBlockState(pos, te.getSavedState(), 3);
		}
	}
	
	private static boolean isTouchingLiquid(IBlockReader reader, BlockPos pos) {
		boolean flag = false;
		BlockPos.Mutable blockpos$mutable = pos.toMutable();
		
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
	
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		if(isTouchingLiquid(worldIn, currentPos))
		{
			if(worldIn.getTileEntity(currentPos) instanceof InkedBlockTileEntity)
				return ((InkedBlockTileEntity) worldIn.getTileEntity(currentPos)).getSavedState();
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return SplatcraftTileEntitites.inkedTileEntity.get().create();
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
	public void remoteColorChange(World world, BlockPos pos, int newColor)
	{
	
	}
	
	@Override
	public void remoteInkClear(World world, BlockPos pos)
	{
		if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
			world.setBlockState(pos, ((InkedBlockTileEntity) world.getTileEntity(pos)).getSavedState());
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return true;
	}
}
