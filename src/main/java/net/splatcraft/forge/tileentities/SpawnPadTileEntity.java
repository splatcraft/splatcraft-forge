package net.splatcraft.forge.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class SpawnPadTileEntity extends InkColorTileEntity implements ITickableTileEntity
{
	public final int maxActiveTime = 20;
	protected int activeTime = maxActiveTime;
	public float radius = 2f;

	public SpawnPadTileEntity()
	{
		super(SplatcraftTileEntitites.spawnPadTileEntity);
	}

	public float getActiveTime() {
		return activeTime;
	}

	public float getMaxActiveTime() {
		return maxActiveTime;
	}

	@Override
	public void tick()
	{
		if (activeTime > 0)
			activeTime--;

		for (Entity entity : level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(getBlockPos().getX()+.5, getBlockPos().getY()+.5, getBlockPos().getZ()+.5,
				getBlockPos().getX()+.5, getBlockPos().getY()+.5, getBlockPos().getZ()+.5).inflate(radius)))
		{
			if(!ColorUtils.colorEquals(entity, this))
			{
				activeTime = maxActiveTime;

				if(entity instanceof InkProjectileEntity || entity instanceof AbstractSubWeaponEntity)
				{
					level.broadcastEntityEvent(entity, (byte) -1);
					entity.remove();
				}
				else
				{
					entity.setDeltaMovement(entity.position().subtract(getBlockPos().getX() + .5f, getBlockPos().getY(), getBlockPos().getZ() + .5f).normalize().scale(.5));
					entity.hurtMarked = true;
				}
			}
		}

	}

	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);

		if (nbt.contains("ActiveTime"))
			activeTime = nbt.getInt("ActiveTime");

		if(nbt.contains("ProtectionRadius"))
			radius = nbt.getFloat("ProtectionRadius");
	}

	@Override
	public CompoundNBT save(CompoundNBT compound)
	{
		compound.putInt("ActiveTime", activeTime);
		compound.putFloat("ProtectionRadius", radius);

		return super.save(compound);
	}


	@Override
	public CompoundNBT getUpdateTag()
	{
		return this.save(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag)
	{
		this.load(state, tag);
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(getBlockPos(), 2, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		if (level != null)
		{
			BlockState state = level.getBlockState(getBlockPos());
			level.sendBlockUpdated(getBlockPos(), state, state, 2);
			handleUpdateTag(state, pkt.getTag());
		}
	}
}
