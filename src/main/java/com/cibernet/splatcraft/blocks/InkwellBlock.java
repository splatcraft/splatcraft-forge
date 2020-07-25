package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.crafting.InkColor;
import com.cibernet.splatcraft.crafting.InkColorManager;
import com.cibernet.splatcraft.handlers.client.ColorHandler;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InkwellBlock extends Block implements IColoredBlock
{
	public InkwellBlock()
	{
		super(Properties.create(Material.GLASS));
		
		SplatcraftBlocks.inkColoredBlocks.add(this);
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
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
	{
		if(world.getTileEntity(pos) instanceof InkColorTileEntity)
			ColorUtils.setPlayerColor(player, ColorUtils.getInkColor(world.getTileEntity(pos)));
		return ActionResultType.SUCCESS;
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
		return SplatcraftTileEntitites.colorTileEntity.get().create();
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
}
