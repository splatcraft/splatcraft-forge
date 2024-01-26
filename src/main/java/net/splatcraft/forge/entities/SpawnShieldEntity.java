package net.splatcraft.forge.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.registries.SplatcraftEntities;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.util.ColorUtils;

public class SpawnShieldEntity extends Entity implements IColoredEntity
{
	public final int MAX_ACTIVE_TIME = 20;

	private static final EntityDataAccessor<Integer> ACTIVE_TIME = SynchedEntityData.defineId(SpawnShieldEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SpawnShieldEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(SpawnShieldEntity.class, EntityDataSerializers.FLOAT);

	private BlockPos spawnPadPos;

	public SpawnShieldEntity(EntityType<SpawnShieldEntity> type, Level level)
	{
		super(type, level);
		refreshDimensions();
		reapplyPosition();
	}

	public SpawnShieldEntity(Level level, BlockPos pos, int color)
	{
		this(SplatcraftEntities.SPAWN_SHIELD.get(), level);
		setColor(color);
		setPos(pos.getX()+.5, pos.getY() - 1, pos.getZ()+.5);
		setSpawnPadPos(pos);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
		if (dataParameter.equals(SIZE)) {
			refreshDimensions();
		}

		super.onSyncedDataUpdated(dataParameter);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(level.isClientSide())
			return;

		if(!(getSpawnPadPos() != null && level.getBlockEntity(getSpawnPadPos()) instanceof SpawnPadTileEntity spawnPad &&
				spawnPad.isSpawnShield(this)))
		{
			discard();
			return;
		}

		if(spawnPad.getColor() != getColor())
			setColor(spawnPad.getColor());

		if (getActiveTime() > 0)
			setActiveTime(getActiveTime()-1);

		for (Entity entity : level.getEntitiesOfClass(Entity.class, getBoundingBox()))
		{
			if(!(entity.getType().is(SplatcraftTags.EntityTypes.BYPASSES_SPAWN_SHIELD) || ColorUtils.colorEquals(level, blockPosition(), ColorUtils.getEntityColor(entity), getColor())))
			{
				setActiveTime(MAX_ACTIVE_TIME);

				if(entity instanceof AbstractSubWeaponEntity || entity instanceof InkProjectileEntity)
				{
					level.broadcastEntityEvent(entity, (byte) -1);
					entity.discard();
				}
				else
				{
					entity.setDeltaMovement(entity.position().subtract(position().x, position().y, position().z).normalize().scale(.5));
					entity.hurtMarked = true;
				}
			}
		}
	}


	//prevents shield from being affected by /kill
	@Override
	public void kill()
	{
		if(getSpawnPadPos() == null)
			super.kill();
	}

	@Override
	protected void defineSynchedData()
	{
		entityData.define(ACTIVE_TIME, 0);
		entityData.define(COLOR, ColorUtils.DEFAULT);
		entityData.define(SIZE, 4f);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt)
	{
		if(nbt.contains("Size"))
			setSize(nbt.getFloat("Size"));
		if(nbt.contains("Color"))
			setColor(ColorUtils.getColorFromNbt(nbt));
		if(nbt.contains("SpawnPadPos"))
			setSpawnPadPos(NbtUtils.readBlockPos(nbt.getCompound("SpawnPadPos")));

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt)
	{
		nbt.putFloat("Size", getSize());
		nbt.putInt("Color", getColor());
		if(getSpawnPadPos() != null)
			nbt.put("SpawnPadPos", NbtUtils.writeBlockPos(getSpawnPadPos()));
	}



	@Override
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public int getColor()
	{
		return entityData.get(COLOR);
	}

	@Override
	public void setColor(int color)
	{
		entityData.set(COLOR, color);
	}

	public int getActiveTime() {
		return entityData.get(ACTIVE_TIME);
	}

	public void setActiveTime(int activeTime)
	{
		entityData.set(ACTIVE_TIME, activeTime);
	}

	public float getSize() {
		return entityData.get(SIZE);
	}

	public void setSize(float size)
	{
		entityData.set(SIZE, size);
		reapplyPosition();
		refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_213305_1_) {
		return super.getDimensions(p_213305_1_).scale(getSize());
	}

	public BlockPos getSpawnPadPos() {
		return spawnPadPos;
	}

	public void setSpawnPadPos(BlockPos spawnPadPos) {
		this.spawnPadPos = spawnPadPos;
	}
}
