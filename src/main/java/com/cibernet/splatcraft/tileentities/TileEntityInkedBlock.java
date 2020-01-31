package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.InkColors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFrostedIce;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class TileEntityInkedBlock extends TileEntity
{
	private IBlockState savedState = Blocks.STONE.getDefaultState();
	private int color = 0x00FF00;
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		
		super.readFromNBT(compound);

		if(compound.hasKey("color"))
			color = compound.getInteger("color");
		
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("savedBlock")));
		int meta = compound.getInteger("savedMeta");
		
		savedState = block.getStateFromMeta(meta);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("color", color);
		
		compound.setString("savedBlock", savedState.getBlock().getRegistryName().toString());
		compound.setInteger("savedMeta", savedState.getBlock().getMetaFromState(savedState));

		return super.writeToNBT(compound);
	}

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

	public TileEntityInkedBlock setSavedState(IBlockState state)
	{
		this.savedState = state;
		return this;
	}

	public TileEntityInkedBlock setColor(int color)
	{
		this.color = color;
		return this;
	}

	public IBlockState getSavedState() {return savedState;}
	public int getColor() {return color;}

}
