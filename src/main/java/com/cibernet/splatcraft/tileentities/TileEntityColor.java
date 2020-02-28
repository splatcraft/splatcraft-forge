package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.SplatCraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class TileEntityColor extends TileEntity
{
	
	private int color = SplatCraft.DEFAULT_INK;

	public TileEntityColor() {}
	public TileEntityColor(int color) {this.color = color;}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		if(compound.hasKey("color"))
			color = compound.getInteger("color");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("color", color);
		return super.writeToNBT(compound);
	}
	
	
	public TileEntityColor setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	public int getColor() {return color;}
	
	
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 2, this.getUpdateTag());
	}
	
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.handleUpdateTag(pkt.getNbtCompound());
		if (this.world != null) {
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, state, state, 2);
		}
		
	}
	
	
}
