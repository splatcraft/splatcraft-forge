package net.splatcraft.forge.entities.subs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.InkExplosion;
import org.jetbrains.annotations.Nullable;

public class CurlingBombEntity extends AbstractSubWeaponEntity
{
	public static final float DIRECT_DAMAGE = 36;
	public static final float CONTACT_DAMAGE = 4;
	public static final float EXPLOSION_SIZE = 2.5f;


	@Nullable
	private BlockState onBlockState;
	private boolean onGround;

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

		//if(!level.isClientSide)
		//InkExplosion.createInkExplosion(level, getOwner(), blockPosition().relative(Direction.DOWN), 1, 4, 0, damageMobs, getColor(), inkType, sourceWeapon);

		BlockState state = this.level.getBlockState(blockPosition());

		if(onGround)
			if(onBlockState != state && this.level.noCollision((new AxisAlignedBB(this.position(), this.position())).inflate(0.06D)))
			{
				this.onGround = false;
				//Vector3d vector3d = this.getDeltaMovement();
				//this.setDeltaMovement(vector3d.multiply(this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F, this.random.nextFloat() * 0.2F));
			}

	}

	@Override
	protected void onHitEntity(EntityRayTraceResult result)
	{
		super.onHitEntity(result);

		if(result.getEntity() instanceof LivingEntity)
			InkDamageUtils.doRollDamage(level, (LivingEntity) result.getEntity(), CONTACT_DAMAGE, getColor(), getOwner(), getItem(), damageMobs);


		double velocityX = this.getDeltaMovement().x;
		double velocityY = this.getDeltaMovement().y;
		double velocityZ = this.getDeltaMovement().z;
		double absVelocityX = Math.abs(velocityX);
		double absVelocityZ = Math.abs(velocityZ);

		if(absVelocityX >= absVelocityZ)
			this.setDeltaMovement(-velocityX, velocityY, velocityZ);
		if(absVelocityZ >= absVelocityX)
			this.setDeltaMovement(velocityX, velocityY, -velocityZ);
	}

	@Override
	public float getGravity() {
		return super.getGravity() * (onGround ? 0 : 1);
	}

	@Override
	protected void onBlockHit(BlockRayTraceResult result)
	{
		super.onBlockHit(result);

		double velocityX = this.getDeltaMovement().x;
		double velocityY = this.getDeltaMovement().y;
		double velocityZ = this.getDeltaMovement().z;

		Direction blockFace = result.getDirection();

		if(blockFace == Direction.UP)
		{
			this.setDeltaMovement(velocityX, 0, velocityZ);

			onGround = true;
			onBlockState = level.getBlockState(result.getBlockPos());

			Vector3d vector3d = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
			//this.setDeltaMovement(vector3d);
			Vector3d vector3d1 = vector3d.normalize().scale(0.05F);
			this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);

		}
		if(blockFace == Direction.DOWN)
			this.setDeltaMovement(velocityX, 0, velocityZ);
		if(blockFace == Direction.EAST || blockFace == Direction.WEST)
			this.setDeltaMovement(-velocityX, velocityY, velocityZ);
		if(blockFace == Direction.NORTH || blockFace == Direction.SOUTH) this.setDeltaMovement(velocityX, velocityY, -velocityZ);
	}
}
