package com.cibernet.splatcraft.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityStageBarrier extends TileEntity implements ITickable
{
	private int activeTime = 0;
	private int maxActiveTime = 20;
	
	
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		if(compound.hasKey("activeTime"))
			activeTime = compound.getInteger("activeTime");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("activeTime", activeTime);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void update()
	{
		if(activeTime > 0)
			activeTime--;
	}
	
	
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tagCompound = this.getUpdateTag();
		return new SPacketUpdateTileEntity(this.pos, 2, tagCompound);
	}
	
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
	
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.handleUpdateTag(pkt.getNbtCompound());
	}
	
	public int getActiveTime() {return activeTime;}
	public float getMaxActiveTime() {return maxActiveTime;}
	public void resetActiveTime() {activeTime = maxActiveTime;}
}
