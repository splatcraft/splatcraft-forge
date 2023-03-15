package net.splatcraft.forge.entities.subs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.items.weapons.RollerItem;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;
import org.jetbrains.annotations.Nullable;

/* TODO
 * Bomb Cooking
 * Figure out how to animate the LEDs on top
 * Lower position when thrown
 * Use player's yRot when thrown straight up/down
 * If thrown straight up/down from a dispenser, either pick a random direction or bias towards north
 */
public class CurlingBombEntity extends AbstractSubWeaponEntity
{
	public static final float DIRECT_DAMAGE = 36;
	public static final float CONTACT_DAMAGE = 4;
	public static final float EXPLOSION_SIZE = 2.5f;
	public static final int FUSE_START = 10;
	public static final int MAX_FUSE_TIME = 80;

	public int fuseTime = MAX_FUSE_TIME;
	public int prevFuseTime = fuseTime;

	public float bladeRot = 0;
	public float prevBladeRot = 0;


	public CurlingBombEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level) {
		super(type, level);
	}

	@Override
	protected Item getDefaultItem() {
		return SplatcraftItems.curlingBomb;
	}

	@Override
	public void tick()
	{
		super.tick();

		for(int i = 0; i <= 2; i++)
			InkBlockUtils.inkBlock(level, blockPosition().below(i), getColor(), CONTACT_DAMAGE, inkType);

		if (!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)1.0E-5F)
		{
			float f1 = 0.98F;
			if (this.onGround)
				f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getSlipperiness(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this);

			f1 = (float) Math.min(0.98, f1*3f) * Math.min(1, 2*fuseTime/(float)MAX_FUSE_TIME);

			this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));

		}


		double spd = getDeltaMovement().multiply(1, 0, 1).length();
		prevBladeRot = bladeRot;
		bladeRot += spd;

		prevFuseTime = fuseTime;
			fuseTime--;

		if(fuseTime <= 0)
		{
			InkExplosion.createInkExplosion(level, getOwner(), blockPosition(), EXPLOSION_SIZE, DIRECT_DAMAGE, DIRECT_DAMAGE, DIRECT_DAMAGE, damageMobs, getColor(), inkType, sourceWeapon);
			level.broadcastEntityEvent(this, (byte) 1);
			level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.subDetonate, SoundCategory.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
			if(!level.isClientSide())
				remove();
			return;
		}
		else if(spd > 0.01 && fuseTime % (int)Math.max(1, (1-spd)*10) == 0)
			level.broadcastEntityEvent(this, (byte) 2);

		this.move(MoverType.SELF, this.getDeltaMovement().multiply(0,1,0));
		setPos(getX()+getDeltaMovement().x(), getY(), getZ()+getDeltaMovement().z);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == 1) {
			level.addAlwaysVisibleParticle(new InkExplosionParticleData(getColor(), EXPLOSION_SIZE * 2), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
		}
		if (id == 2) {
			level.addParticle(new InkSplashParticleData(getColor(), EXPLOSION_SIZE*1.15f), this.getX(), this.getY()+0.4, this.getZ(), 0, 0, 0);
		}

	}

	//Ripped and modified from Minestuck's BouncingProjectileEntity class (with permission)
	@Override
	protected void onHitEntity(EntityRayTraceResult result)
	{
		super.onHitEntity(result);

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

		if(result.getEntity() instanceof LivingEntity)
			InkDamageUtils.doRollDamage(level, (LivingEntity) result.getEntity(), CONTACT_DAMAGE, getColor(), getOwner(), this, sourceWeapon, false);
	}

	@Override
	protected void onBlockHit(BlockRayTraceResult result)
	{
		double velocityX = this.getDeltaMovement().x;
		double velocityY = this.getDeltaMovement().y;
		double velocityZ = this.getDeltaMovement().z;

		Direction blockFace = result.getDirection();

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
}
