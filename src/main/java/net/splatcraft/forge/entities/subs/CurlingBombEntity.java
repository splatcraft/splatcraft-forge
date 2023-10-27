package net.splatcraft.forge.entities.subs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.items.weapons.settings.SubWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;

import java.util.List;

public class CurlingBombEntity extends AbstractSubWeaponEntity
{
	public static final int FLASH_DURATION = 20;

	private static final EntityDataAccessor<Integer> INIT_FUSE_TIME = SynchedEntityData.defineId(CurlingBombEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> COOK_SCALE = SynchedEntityData.defineId(CurlingBombEntity.class, EntityDataSerializers.FLOAT);

	public int fuseTime = 0;
	public int prevFuseTime = 0;

	public float bladeRot = 0;
	public float prevBladeRot = 0;
	private boolean playedActivationSound = false;
	private boolean playAlertAnim = false;


	public CurlingBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, Level level) {
		super(type, level);
		maxUpStep = .7f;
	}

	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		entityData.define(INIT_FUSE_TIME, 0);
		entityData.define(COOK_SCALE, 0f);
	}

	@Override
	protected Item getDefaultItem() {
		return SplatcraftItems.curlingBomb.get();
	}

	public static void onItemUseTick(Level level, LivingEntity entity, ItemStack stack, int useTime)
	{
		CompoundTag data = stack.getTag().getCompound("EntityData");
		data.putInt("CookTime", stack.getItem().getUseDuration(stack) - useTime);

		stack.getTag().put("EntityData", data);
	}

	@Override
	public void readItemData(CompoundTag nbt)
	{
		if(nbt.contains("CookTime"))
		{
			if(getSettings().cookTime > 0)
				setCookScale(Math.min(4, (nbt.getInt("CookTime") / (float)getSettings().cookTime)));
			setInitialFuseTime(nbt.getInt("CookTime"));
			prevFuseTime = getInitialFuseTime();
		}
	}

	@Override
	public void tick()
	{
		super.tick();

		SubWeaponSettings settings = getSettings();

		double spd = getDeltaMovement().multiply(1, 0, 1).length();
		prevBladeRot = bladeRot;
		bladeRot += spd;

		prevFuseTime = fuseTime;
		fuseTime++;

		if(fuseTime == 30)
			playAlertAnim = true;

		if (fuseTime >= settings.fuseTime - FLASH_DURATION && !playedActivationSound)
		{
			level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonating, SoundSource.PLAYERS, 0.8F, 1f);
			playedActivationSound = true;
		}

		if(!level.isClientSide)
			for(int i = 0; i <= 2; i++)
				if(!InkBlockUtils.isUninkable(level, blockPosition().below(i)))
				{
					InkBlockUtils.inkBlock(level, blockPosition().below(i), getColor(), settings.contactDamage, inkType);
					break;
				}


		if (!this.onGround || distanceToSqr(this.getDeltaMovement()) > (double)1.0E-5F)
		{
			float f1 = 0.98F;
			if (this.onGround)
				f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getFriction(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this);

			f1 = (float) Math.min(0.98, f1*3f) * Math.min(1, 2 * (1 - fuseTime/(float)settings.fuseTime));

			this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));

		}

		if(fuseTime >= settings.fuseTime)
		{
			InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), settings.explosionSize + getCookScale(), settings.propDamage, settings.indirectDamage, settings.directDamage, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
			level.broadcastEntityEvent(this, (byte) 1);
			level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
			if(!level.isClientSide())
				discard();
			return;
		}
		else if(spd > 0.01 && fuseTime % (int)Math.max(1, (1-spd)*10) == 0)
			level.broadcastEntityEvent(this, (byte) 2);

		this.move(MoverType.SELF, this.getDeltaMovement().multiply(0,1,0));

		Vec3 vec = getDeltaMovement().multiply(1, 0, 1);
		vec = position().add(collide(vec));

		setPos(vec.x, vec.y, vec.z);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == 1) {
			level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), (getSettings().explosionSize+getCookScale()) * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		}
		if (id == 2) {
			level.addParticle(new InkSplashParticleData(getColor(), getSettings().explosionSize*1.15f), this.getX(), this.getY()+0.4, this.getZ(), 0, 0, 0);
		}

	}

	//Ripped and modified from Minestuck's BouncingProjectileEntity class (with permission)
	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		if(result.getEntity() instanceof LivingEntity)
			InkDamageUtils.doRollDamage(level, (LivingEntity) result.getEntity(), getSettings().contactDamage, getColor(), getOwner(), this, sourceWeapon, false);

		double velocityX = this.getDeltaMovement().x;
		double velocityY = this.getDeltaMovement().y;
		double velocityZ = this.getDeltaMovement().z;
		double absVelocityX = Math.abs(velocityX);
		double absVelocityY = Math.abs(velocityY);
		double absVelocityZ = Math.abs(velocityZ);

		if(absVelocityX >= absVelocityY && absVelocityX >= absVelocityZ)
			this.setDeltaMovement(-velocityX, velocityY, velocityZ);
		if(absVelocityY >= .05 && absVelocityY >= absVelocityX && absVelocityY >= absVelocityZ)
			this.setDeltaMovement(velocityX, -velocityY * .5, velocityZ);
		if(absVelocityZ >= absVelocityY && absVelocityZ >= absVelocityX)
			this.setDeltaMovement(velocityX, velocityY, -velocityZ);
	}

	@Override
	protected void onBlockHit(BlockHitResult result)
	{
		if(canStepUp(getDeltaMovement()))
			return;

		double velocityX = this.getDeltaMovement().x;
		double velocityY = this.getDeltaMovement().y;
		double velocityZ = this.getDeltaMovement().z;

		Direction blockFace = result.getDirection();

		if(level.getBlockState(result.getBlockPos()).getCollisionShape(level, result.getBlockPos()).bounds().maxY - (position().y() - blockPosition().getY()) < maxUpStep)
			return;

		if(blockFace == Direction.EAST || blockFace == Direction.WEST)
			this.setDeltaMovement(-velocityX, velocityY, velocityZ);
		if(Math.abs(velocityY) >= 0.05 && (blockFace == Direction.DOWN))
			this.setDeltaMovement(velocityX, -velocityY * .5, velocityZ);
		if(blockFace == Direction.NORTH || blockFace == Direction.SOUTH) this.setDeltaMovement(velocityX, velocityY, -velocityZ);

	}

	@Override
	protected boolean handleMovement() {
		return false;
	}

	public float getFlashIntensity(float partialTicks)
	{
		SubWeaponSettings settings = getSettings();
		return Math.max(0, Mth.lerp(partialTicks, prevFuseTime, fuseTime) - (settings.fuseTime - FLASH_DURATION)) * 0.85f / FLASH_DURATION;
	}

	private boolean canStepUp(Vec3 p_20273_) {
		AABB aabb = this.getBoundingBox();
		List<VoxelShape> list = this.level.getEntityCollisions(this, aabb.expandTowards(p_20273_));
		Vec3 vec3 = p_20273_.lengthSqr() == 0.0D ? p_20273_ : collideBoundingBox(this, p_20273_, aabb, this.level, list);
		boolean flag = p_20273_.x != vec3.x;
		boolean flag1 = p_20273_.y != vec3.y;
		boolean flag2 = p_20273_.z != vec3.z;
		boolean flag3 = this.onGround || flag1 && p_20273_.y < 0.0D;
		float stepHeight = getStepHeight();
		if (stepHeight > 0.0F && flag3 && (flag || flag2)) {
			Vec3 vec31 = collideBoundingBox(this, new Vec3(p_20273_.x, (double)stepHeight, p_20273_.z), aabb, this.level, list);
			Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, (double)stepHeight, 0.0D), aabb.expandTowards(p_20273_.x, 0.0D, p_20273_.z), this.level, list);
			if (vec32.y < (double)stepHeight) {
				Vec3 vec33 = collideBoundingBox(this, new Vec3(p_20273_.x, 0.0D, p_20273_.z), aabb.move(vec32), this.level, list).add(vec32);
				if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
					vec31 = vec33;
				}
			}

			if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
				return true;
			}
		}

		return false;
	}

	private Vec3 collide(Vec3 p_20273_) {
		AABB aabb = this.getBoundingBox();
		List<VoxelShape> list = this.level.getEntityCollisions(this, aabb.expandTowards(p_20273_));
		Vec3 vec3 = p_20273_.lengthSqr() == 0.0D ? p_20273_ : collideBoundingBox(this, p_20273_, aabb, this.level, list);
		boolean flag = p_20273_.x != vec3.x;
		boolean flag1 = p_20273_.y != vec3.y;
		boolean flag2 = p_20273_.z != vec3.z;
		boolean flag3 = this.onGround || flag1 && p_20273_.y < 0.0D;
		float stepHeight = getStepHeight();
		if (stepHeight > 0.0F && flag3 && (flag || flag2)) {
			Vec3 vec31 = collideBoundingBox(this, new Vec3(p_20273_.x, (double)stepHeight, p_20273_.z), aabb, this.level, list);
			Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, (double)stepHeight, 0.0D), aabb.expandTowards(p_20273_.x, 0.0D, p_20273_.z), this.level, list);
			if (vec32.y < (double)stepHeight) {
				Vec3 vec33 = collideBoundingBox(this, new Vec3(p_20273_.x, 0.0D, p_20273_.z), aabb.move(vec32), this.level, list).add(vec32);
				if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
					vec31 = vec33;
				}
			}

			if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
				return vec31.add(collideBoundingBox(this, new Vec3(0.0D, -vec31.y + p_20273_.y, 0.0D), aabb.move(vec31), this.level, list));
			}
		}

		return vec3;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		setInitialFuseTime(nbt.getInt("FuseTime"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putInt("FuseTime", fuseTime);
	}

	public int getInitialFuseTime()
	{
		return entityData.get(INIT_FUSE_TIME);
	}

	public void setInitialFuseTime(int v)
	{
		entityData.set(INIT_FUSE_TIME, v);
	}

	public float getCookScale() {
		return entityData.get(COOK_SCALE);
	}

	public void setCookScale(float v)
	{
		entityData.set(COOK_SCALE, v);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter)
	{
		super.onSyncedDataUpdated(dataParameter);

		if(INIT_FUSE_TIME.equals(dataParameter))
		{
			fuseTime = getInitialFuseTime();
		}

	}
}
