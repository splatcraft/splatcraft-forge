package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public class InkColorTileEntity extends TileEntity
{
	
	private int color = 0xDF641A;
	
	public InkColorTileEntity(TileEntityType type)
	{
		super(type);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		nbt.putInt("Color", color);
		return super.write(nbt);
	}
	
	
	//Nbt Read
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt)
	{
		super.func_230337_a_(state, nbt);
		color = nbt.getInt("Color");
	}
	
	@Override
	public CompoundNBT getUpdateTag()
	{
		return this.write(new CompoundNBT());
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag)
	{
		this.func_230337_a_(state, tag);
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(getPos(), 2, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		if(world != null)
		{
			BlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 2);
			handleUpdateTag(state, pkt.getNbtCompound());
		}
	}
	
	public int getColor() {return color;}
	public void setColor(int color) {this.color = color;}
	
}
