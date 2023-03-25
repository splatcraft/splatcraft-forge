package net.splatcraft.forge.entities.subs;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;

import java.util.stream.Stream;

public class CurlingBombEntity extends AbstractSubWeaponEntity
{
	public static final float DIRECT_DAMAGE = 36;
	public static final float CONTACT_DAMAGE = 4;
	public static final float EXPLOSION_SIZE = 2.5f;
	public static final int FUSE_START = 10;
	public static final int MAX_FUSE_TIME = 80;
	public static final int MAX_COOK_TIME = 30;

	private static final DataParameter<Integer> INIT_FUSE_TIME = EntityDataManager.defineId(CurlingBombEntity.class, DataSerializers.INT);
	private static final DataParameter<Float> COOK_SCALE = EntityDataManager.defineId(CurlingBombEntity.class, DataSerializers.FLOAT);

	public int fuseTime = MAX_FUSE_TIME;
	public int prevFuseTime = MAX_FUSE_TIME;

	public float bladeRot = 0;
	public float prevBladeRot = 0;
	private boolean playedActivationSound = false;


	public CurlingBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level) {
		super(type, level);
		maxUpStep = .7f;
	}

	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		entityData.define(INIT_FUSE_TIME, MAX_FUSE_TIME);
		entityData.define(COOK_SCALE, 0f);
	}

	@Override
	protected Item getDefaultItem() {
		return SplatcraftItems.curlingBomb;
	}

	public static void onItemUseTick(World level, LivingEntity entity, ItemStack stack, int useTime)
	{
		CompoundNBT data = stack.getTag().getCompound("EntityData");
		data.putInt("CookTime", stack.getItem().getUseDuration(stack) - useTime);

		stack.getTag().put("EntityData", data);
	}

	@Override
	public void readItemData(CompoundNBT nbt)
	{
		if(nbt.contains("CookTime"))
		{
			setCookScale((nbt.getInt("CookTime") / (float)MAX_COOK_TIME) * 1.5f);
			setInitialFuseTime(getInitialFuseTime() - nbt.getInt("CookTime"));
			prevFuseTime = getInitialFuseTime();
		}
	}

	@Override
	public void tick()
	{
		super.tick();

		double spd = getDeltaMovement().multiply(1, 0, 1).length();
		prevBladeRot = bladeRot;
		bladeRot += spd;

		prevFuseTime = fuseTime;
		fuseTime--;

		if (fuseTime <= 20 && !playedActivationSound)
		{
			level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonating, SoundCategory.PLAYERS, 0.8F, 1f);
			playedActivationSound = true;
		}

		if(!level.isClientSide)
			for(int i = 0; i <= 2; i++)
				if(!InkBlockUtils.isUninkable(level, blockPosition().below(i)))
				{
					InkBlockUtils.inkBlock(level, blockPosition().below(i), getColor(), CONTACT_DAMAGE, inkType);
					break;
				}


		if (!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)1.0E-5F)
		{
			float f1 = 0.98F;
			if (this.onGround)
				f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getSlipperiness(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this);

			f1 = (float) Math.min(0.98, f1*3f) * Math.min(1, 2* fuseTime/(float)MAX_FUSE_TIME);

			this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));

		}

		if(fuseTime <= 0)
		{
			InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE + getCookScale(), DIRECT_DAMAGE, DIRECT_DAMAGE, DIRECT_DAMAGE, bypassMobDamageMultiplier, getColor(), inkType, sourceWeapon);
			level.broadcastEntityEvent(this, (byte) 1);
			level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
			if(!level.isClientSide())
				remove();
			return;
		}
		else if(spd > 0.01 && fuseTime % (int)Math.max(1, (1-spd)*10) == 0)
			level.broadcastEntityEvent(this, (byte) 2);

		this.move(MoverType.SELF, this.getDeltaMovement().multiply(0,1,0));

		Vector3d vec = getDeltaMovement().multiply(1, 0, 1);
		vec = position().add(collide(vec));

		setPos(vec.x, vec.y, vec.z);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == 1) {
			level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), (EXPLOSION_SIZE+getCookScale()) * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		}
		if (id == 2) {
			level.addParticle(new InkSplashParticleData(getColor(), EXPLOSION_SIZE*1.15f), this.getX(), this.getY()+0.4, this.getZ(), 0, 0, 0);
		}

	}

	//Ripped and modified from Minestuck's BouncingProjectileEntity class (with permission)
	@Override
	protected void onHitEntity(EntityRayTraceResult result)
	{
		if(result.getEntity() instanceof LivingEntity)
			InkDamageUtils.doRollDamage(level, (LivingEntity) result.getEntity(), CONTACT_DAMAGE, getColor(), getOwner(), this, sourceWeapon, false);

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
	protected void onBlockHit(BlockRayTraceResult result)
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
		return 1f-Math.min(FUSE_START, MathHelper.lerp(partialTicks, prevFuseTime, fuseTime)*0.5f)/(float)FUSE_START;
	}

	private boolean canStepUp(Vector3d p_213306_1_) {
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		ISelectionContext iselectioncontext = ISelectionContext.of(this);
		VoxelShape voxelshape = this.level.getWorldBorder().getCollisionShape();
		Stream<VoxelShape> stream = VoxelShapes.joinIsNotEmpty(voxelshape, VoxelShapes.create(axisalignedbb.deflate(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
		Stream<VoxelShape> stream1 = this.level.getEntityCollisions(this, axisalignedbb.expandTowards(p_213306_1_), (p_233561_0_) -> true);
		ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(stream1, stream));
		Vector3d vector3d = p_213306_1_.lengthSqr() == 0.0D ? p_213306_1_ : collideBoundingBoxHeuristically(this, p_213306_1_, axisalignedbb, this.level, iselectioncontext, reuseablestream);
		boolean flag = p_213306_1_.x != vector3d.x;
		boolean flag1 = p_213306_1_.y != vector3d.y;
		boolean flag2 = p_213306_1_.z != vector3d.z;
		boolean flag3 = this.onGround || flag1 && p_213306_1_.y < 0.0D;
		if (this.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
			Vector3d vector3d1 = collideBoundingBoxHeuristically(this, new Vector3d(p_213306_1_.x, (double)this.maxUpStep, p_213306_1_.z), axisalignedbb, this.level, iselectioncontext, reuseablestream);
			Vector3d vector3d2 = collideBoundingBoxHeuristically(this, new Vector3d(0.0D, (double)this.maxUpStep, 0.0D), axisalignedbb.expandTowards(p_213306_1_.x, 0.0D, p_213306_1_.z), this.level, iselectioncontext, reuseablestream);
			if (vector3d2.y < (double)this.maxUpStep) {
				Vector3d vector3d3 = collideBoundingBoxHeuristically(this, new Vector3d(p_213306_1_.x, 0.0D, p_213306_1_.z), axisalignedbb.move(vector3d2), this.level, iselectioncontext, reuseablestream).add(vector3d2);
				if (getHorizontalDistanceSqr(vector3d3) > getHorizontalDistanceSqr(vector3d1)) {
					vector3d1 = vector3d3;
				}
			}

			if (getHorizontalDistanceSqr(vector3d1) > getHorizontalDistanceSqr(vector3d)) {
				return true;
			}
		}

		return false;
	}

	private Vector3d collide(Vector3d p_213306_1_) {
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		ISelectionContext iselectioncontext = ISelectionContext.of(this);
		VoxelShape voxelshape = this.level.getWorldBorder().getCollisionShape();
		Stream<VoxelShape> stream = VoxelShapes.joinIsNotEmpty(voxelshape, VoxelShapes.create(axisalignedbb.deflate(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
		Stream<VoxelShape> stream1 = this.level.getEntityCollisions(this, axisalignedbb.expandTowards(p_213306_1_), (p_233561_0_) -> {
			return true;
		});
		ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(stream1, stream));
		Vector3d vector3d = p_213306_1_.lengthSqr() == 0.0D ? p_213306_1_ : collideBoundingBoxHeuristically(this, p_213306_1_, axisalignedbb, this.level, iselectioncontext, reuseablestream);
		boolean flag = p_213306_1_.x != vector3d.x;
		boolean flag1 = p_213306_1_.y != vector3d.y;
		boolean flag2 = p_213306_1_.z != vector3d.z;
		boolean flag3 = this.onGround || flag1 && p_213306_1_.y < 0.0D;
		if (this.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
			Vector3d vector3d1 = collideBoundingBoxHeuristically(this, new Vector3d(p_213306_1_.x, (double)this.maxUpStep, p_213306_1_.z), axisalignedbb, this.level, iselectioncontext, reuseablestream);
			Vector3d vector3d2 = collideBoundingBoxHeuristically(this, new Vector3d(0.0D, (double)this.maxUpStep, 0.0D), axisalignedbb.expandTowards(p_213306_1_.x, 0.0D, p_213306_1_.z), this.level, iselectioncontext, reuseablestream);
			if (vector3d2.y < (double)this.maxUpStep) {
				Vector3d vector3d3 = collideBoundingBoxHeuristically(this, new Vector3d(p_213306_1_.x, 0.0D, p_213306_1_.z), axisalignedbb.move(vector3d2), this.level, iselectioncontext, reuseablestream).add(vector3d2);
				if (getHorizontalDistanceSqr(vector3d3) > getHorizontalDistanceSqr(vector3d1)) {
					vector3d1 = vector3d3;
				}
			}

			if (getHorizontalDistanceSqr(vector3d1) > getHorizontalDistanceSqr(vector3d)) {
				return vector3d1.add(collideBoundingBoxHeuristically(this, new Vector3d(0.0D, -vector3d1.y + p_213306_1_.y, 0.0D), axisalignedbb.move(vector3d1), this.level, iselectioncontext, reuseablestream));
			}
		}

		return vector3d;
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		setInitialFuseTime(nbt.getInt("FuseTime"));
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
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
	public void onSyncedDataUpdated(DataParameter<?> dataParameter)
	{
		super.onSyncedDataUpdated(dataParameter);

		if(INIT_FUSE_TIME.equals(dataParameter))
		{
			fuseTime = getInitialFuseTime();
		}

	}
}
