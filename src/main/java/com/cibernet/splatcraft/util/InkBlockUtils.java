package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.blocks.AbstractSquidPassthroughBlock;
import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.data.tags.SplatcraftTags;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class InkBlockUtils
{
	
	public static TreeMap<InkType, InkBlocks> inkTypeMap = new TreeMap<InkType, InkBlocks>()
	{{
		put(InkType.NORMAL, new InkBlocks(SplatcraftBlocks.inkedBlock).put(StairsBlock.class, SplatcraftBlocks.inkedStairs).put(SlabBlock.class, SplatcraftBlocks.inkedSlab));
	}};
	
	
	
	public static boolean inkBlock(World world, BlockPos pos, int color, float damage, InkType inkType)
	{
		BlockState state = world.getBlockState(pos);
		TileEntity te = world.getTileEntity(pos);
		
		if(InkedBlock.isTouchingLiquid(world, pos))
			return false;
		
		if(state.getBlock() instanceof IColoredBlock)
			return ((IColoredBlock) state.getBlock()).inkBlock(world, pos, color, damage, inkType);
			
		if(!canInk(world, pos))
			return false;
		
		world.setBlockState(pos, getInkState(inkType, state), 3);
		
		world.setTileEntity(pos, SplatcraftBlocks.inkedBlock.createTileEntity(SplatcraftBlocks.inkedBlock.getDefaultState(), world));
		InkedBlockTileEntity inkte = (InkedBlockTileEntity) world.getTileEntity(pos);
		if(inkte == null)
			return false;
		inkte.setColor(color);
		inkte.setSavedState(state);
		return true;
	}
	
	public static BlockState getInkState(InkType type, BlockState baseState)
	{
		BlockState inkState = getInkBlock(type, baseState.getBlock()).getDefaultState();
		Iterator<Property<?>> properties = baseState.getProperties().iterator();
		
		while(properties.hasNext())
		{
			Property<?> property = properties.next();
			
			if(inkState.hasProperty(property))
				inkState = mergeProperty(inkState, baseState, property);
			
		}
		
		return inkState;
	}
	
	private static <T extends Comparable<T>> BlockState mergeProperty(BlockState state, BlockState baseState, Property<T> property)
	{
		T value = baseState.get(property);
		return state.with(property, value);
	}
	
	
	public static boolean canInk(World world, BlockPos pos)
	{
		
		if(InkedBlock.isTouchingLiquid(world, pos))
			return false;
		
		Block block = world.getBlockState(pos).getBlock();
		
		if(SplatcraftTags.Blocks.UNINKABLE_BLOCKS.contains(block))
			return false;
		
		if(block instanceof StairsBlock || block instanceof SlabBlock || block instanceof BarrierBlock)
			return true;
		
		if(world.getTileEntity(pos) != null)
			return false;
		
		
		if(SplatcraftTags.Blocks.INKABLE_BLOCKS.contains(block))
			return true;
		
		if(canInkPassthrough(world, pos))
			return false;
		
		if(!world.getBlockState(pos).isOpaqueCube(world, pos))
			return false;
		
		if(block.isTransparent(world.getBlockState(pos)))
			return false;
			
		return true;
	}
	
	public static boolean canInkPassthrough(World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		
		if(state.getBlock() instanceof AbstractSquidPassthroughBlock)
			return true;
		
		if(state.getCollisionShape(world, pos).isEmpty())
			return true;
		
		return false;
	}
	
	public static boolean canSquidHide(LivingEntity entity)
	{
		return canSquidSwim(entity) || canSquidClimb(entity);
	}
	
	public static boolean canSquidSwim(LivingEntity entity)
	{
		boolean canSwim = false;
		
		if(entity.world.getBlockState(entity.getPosition().down()).getBlock() instanceof IColoredBlock)
			canSwim = ((IColoredBlock) entity.world.getBlockState(entity.getPosition().down()).getBlock()).canSwim();
		
		if(canSwim)
			return ColorUtils.colorEquals(entity, entity.world.getTileEntity(entity.getPosition().down()));
		return false;
	}
	
	public static boolean onEnemyInk(LivingEntity entity)
	{
		boolean canDamage = false;
		
		if(entity.world.getBlockState(entity.getPosition().down()).getBlock() instanceof IColoredBlock)
			canDamage = ((IColoredBlock) entity.world.getBlockState(entity.getPosition().down()).getBlock()).canDamage();
		
		return canDamage && ColorUtils.getInkColor(entity.world.getTileEntity(entity.getPosition().down())) != -1 && !canSquidSwim(entity);
	}
	
	public static boolean canSquidClimb(LivingEntity entity)
	{
		if(onEnemyInk(entity))
			return false;
		for(int i = 0; i < 4; i++)
		{
			float xOff = (i < 2 ? .32f : 0) * (i % 2 == 0 ? 1 : -1), zOff = (i < 2 ? 0 : .32f) * (i % 2 == 0 ? 1 : -1);
			BlockPos pos = new BlockPos(entity.getPosX() - xOff, entity.getPosY(), entity.getPosZ() - zOff);
			Block block = entity.world.getBlockState(pos).getBlock();
			
			if((!(block instanceof IColoredBlock) || (block instanceof IColoredBlock && ((IColoredBlock) block).canClimb())) && entity.world.getTileEntity(pos) instanceof InkColorTileEntity &&
					(((InkColorTileEntity) entity.world.getTileEntity(pos)).getColor() == ColorUtils.getEntityColor(entity)) && !entity.isPassenger())
				return true;
		}
		return false;
	}
	
	public static InkBlockUtils.InkType getInkType(LivingEntity entity) //TODO
	{
		return InkBlockUtils.InkType.NORMAL;
	}
	
	public static Block getInkBlock(InkType inkType, Block baseBlock)
	{
		return inkTypeMap.get(inkType).get(baseBlock);
	}
	
	public static class InkType implements Comparable<InkType>
	{
		
		public static final ArrayList<InkType> values = new ArrayList<>();
		
		public static final InkType NORMAL = new InkType();
		public static final InkType GLOWING = new InkType();
		
		public InkType()
		{
			values.add(this);
		}
		
		@Override
		public int compareTo(InkType o)
		{
			return values.indexOf(this) - values.indexOf(o);
		}
		
		public int getIndex() {return values.indexOf(this);}
	}
	
	public static class InkBlocks
	{
		Block defaultBlock;
		final List<Map.Entry<Class<? extends Block>, Block>> blockMap = new ArrayList<>();
		
		public InkBlocks(Block defaultBlock)
		{
			this.defaultBlock = defaultBlock;
		}
		
		public Block get(Block block)
		{
			if(blockMap.isEmpty())
				return defaultBlock;
			
			for(Map.Entry<Class<? extends Block>, Block> entry : blockMap)
				if(entry.getKey().isInstance(block))
					return entry.getValue();
			
			return defaultBlock ;
		}
		
		public InkBlocks put(Class<? extends Block> blockClass, Block block)
		{
			blockMap.add(new AbstractMap.SimpleEntry<>(blockClass, block));
			return this;
		}
	}
}
