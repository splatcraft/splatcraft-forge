package com.cibernet.splatcraft.entities;

import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.registries.SplatcraftEntities;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InkProjectileEntity extends ProjectileItemEntity implements IColoredEntity
{
	
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(SlimeEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Float> PROJ_SIZE = EntityDataManager.createKey(SlimeEntity.class, DataSerializers.FLOAT);
	
	public float gravityVelocity = 0.03f;
	
	public InkProjectileEntity(EntityType<? extends ProjectileItemEntity> type, World world)
	{
		super(type, world);
	}
	
	public InkProjectileEntity(World world, LivingEntity thrower, int color, float size)
	{
		super(SplatcraftEntities.INK_PROJECTILE, thrower, world);
		setColor(color);
		setProjectileSize(size);
	}
	
	@Override
	protected void registerData()
	{
		dataManager.register(COLOR, ColorUtils.DEFAULT);
		dataManager.register(PROJ_SIZE, 1.0f);
	}
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> dataParameter)
	{
		if(dataParameter.equals(PROJ_SIZE))
			recalculateSize();
		
		super.notifyDataManagerChange(dataParameter);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return SplatcraftItems.splattershot;
	}
	
	@Override
	protected void onEntityHit(EntityRayTraceResult p_213868_1_)
	{
		super.onEntityHit(p_213868_1_);
	}
	
	protected void onBlockHit(BlockRayTraceResult result)
	{
		this.func_230299_a_(result);
		InkBlockUtils.inkBlock(world, result.getPos(), getColor(), InkBlockUtils.InkType.NORMAL);
		
		this.remove();
	}
	
	public void shoot(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy)
	{
		func_234612_a_(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);
		
		Vector3d posDiff = new Vector3d(0,0,0);;
		
		if(thrower instanceof PlayerEntity) try
		{ posDiff = thrower.getPositionVec().subtract(WeaponHandler.getPlayerPrevPos((PlayerEntity) thrower)); }
		catch(NullPointerException e) {}
		
		setPosition(getPosX() + posDiff.getX(), getPosY() + posDiff.getY(), getPosZ() + posDiff.getZ());
		setMotion(getMotion().add(posDiff.mul(0.8, 0.8, 0.8)));
	}
	
	protected void onImpact(RayTraceResult result) {
		RayTraceResult.Type rayType = result.getType();
		if (rayType == RayTraceResult.Type.ENTITY)
			this.onEntityHit((EntityRayTraceResult)result);
		else if (rayType == RayTraceResult.Type.BLOCK)
			onBlockHit((BlockRayTraceResult) result);
		
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt)
	{
		setProjectileSize(nbt.getFloat("Size"));
		setColor(nbt.getInt("Color"));
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt)
	{
		nbt.putFloat("Size", getProjectileSize());
		nbt.putInt("Color", getColor());
	}
	
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	public EntitySize getSize(Pose pose)
	{
		return super.getSize(pose).scale(getProjectileSize()/2f);
	}
	
	@Override
	public float getGravityVelocity()
	{
		return gravityVelocity;
	}
	
	public float getProjectileSize() { return dataManager.get(PROJ_SIZE);}
	public void setProjectileSize(float size)
	{
		dataManager.set(PROJ_SIZE, size);
		this.recenterBoundingBox();
		this.recalculateSize();
	}
	
	public int getColor() { return dataManager.get(COLOR);}
	public void setColor(int color) { dataManager.set(COLOR, color);}
}
