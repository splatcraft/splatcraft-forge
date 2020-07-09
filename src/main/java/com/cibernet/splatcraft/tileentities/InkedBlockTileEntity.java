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
		super(SplatcraftTileEntitites.inkedTileEntity.get());
	}
	
	//Read NBT
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt)
	{
		super.func_230337_a_(state, nbt);
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
	public void setSavedColor(int color) {this.savedColor = savedColor;}
	public boolean hasSavedColor() {return savedColor != -1;}
}
