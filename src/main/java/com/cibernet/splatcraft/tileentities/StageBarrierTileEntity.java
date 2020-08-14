package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.blocks.StageBarrierBlock;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.InkDamageUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;

public class StageBarrierTileEntity extends TileEntity implements ITickableTileEntity
{
	private int activeTime = 0;
	private int maxActiveTime = 20;
	
	
	public StageBarrierTileEntity()
	{
		super(SplatcraftTileEntitites.stageBarrierTileEntity.get());
	}
	
	
	@Override
	public void tick()
	{
		if(activeTime > 0)
			activeTime--;
		
		for(Entity entity : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(0.05)))
		{
			resetActiveTime();
			if(getBlockState().getBlock() instanceof StageBarrierBlock && ((StageBarrierBlock) getBlockState().getBlock()).damagesPlayer &&
					entity instanceof PlayerEntity)
				entity.attackEntityFrom(InkDamageUtils.VOID_DAMAGE, Float.MAX_VALUE);
			
		}
		
		if(world.isRemote && ClientUtils.getClientPlayer().isCreative())
			resetActiveTime();
			
	}
	
	private void resetActiveTime()
	{
		activeTime = maxActiveTime;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt)
	{
		super.read(state, nbt);
		
		if(nbt.contains("ActiveTime"))
			activeTime = nbt.getInt("ActiveTime");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		compound.putInt("ActiveTime", activeTime);
		return super.write(compound);
	}
	
	
	@Override
	public CompoundNBT getUpdateTag()
	{
		return this.write(new CompoundNBT());
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag)
	{
		this.read(state, tag);
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
	
	
	public float getMaxActiveTime()
	{
		return maxActiveTime;
	}
	
	public float getActiveTime()
	{
		return activeTime;
	}
}
