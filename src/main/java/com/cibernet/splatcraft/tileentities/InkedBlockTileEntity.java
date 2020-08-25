package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;

public class InkedBlockTileEntity extends InkColorTileEntity
{
	private BlockState savedState = Blocks.AIR.getDefaultState();
	private int savedColor = -1;
	
	public InkedBlockTileEntity()
	{
		super(SplatcraftTileEntitites.inkedTileEntity);
	}
	
	//Read NBT
	@Override
	public void read(BlockState state, CompoundNBT nbt)
	{
		super.read(state, nbt);
		savedState = NBTUtil.readBlockState(nbt.getCompound("SavedState"));
		savedColor = nbt.getInt("SavedColor");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		nbt.put("SavedState", NBTUtil.writeBlockState(savedState));
		nbt.putInt("SavedColor", savedColor);
		return super.write(nbt);
	}
	
	public BlockState getSavedState() {return savedState;}
	public void setSavedState(BlockState savedState) {this.savedState = savedState;}
	public boolean hasSavedState() {return savedState.getBlock() != Blocks.AIR;}
	
	public int getSavedColor() {return savedColor;}
	public void setSavedColor(int color) {this.savedColor = color;}
	public boolean hasSavedColor() {return savedColor != -1;}
}
