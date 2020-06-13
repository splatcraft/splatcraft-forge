package com.cibernet.splatcraft.entities.classes;

import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBlasterProjectile extends EntityInkProjectile
{
	private int lifespan = 1;
	private float splashDamage = 0;
	
	public EntityBlasterProjectile(World worldIn, EntityLivingBase throwerIn, int color, float damage, float splashDamage, int lifespan)
	{
		super(worldIn, throwerIn, color, damage);
		this.lifespan = lifespan;
		this.splashDamage = splashDamage;
		
		setTrail(false);
	}
	
	public EntityBlasterProjectile(World world) {super(world);}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		BlockPos pos = new BlockPos(posX, posY, posZ);
		
		if(!world.isRemote)
			lifespan--;
		
		if(lifespan <= 0)
		{
			SplatCraftUtils.createInkExplosion(world, this, pos, getProjectileSize()/2, splashDamage, getColor(), glowingInk);
			this.setDead();
			this.world.setEntityState(this, (byte)3);
		}
		else
		{
			for(double y = posY; y >= 0 && posY-y <= 8; y--)
			{
				BlockPos inkPos = new BlockPos(posX, y, posZ);
				if(!SplatCraftUtils.canInkPassthrough(world, inkPos))
				{
					SplatCraftUtils.createInkExplosion(world, inkPos.up(), getProjectileSize()/3f, getColor(), glowingInk);
					SplatCraftUtils.createInkExplosion(world, pos, getProjectileSize()/3f, getColor(), glowingInk);
					break;
				}
			}
		}
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		lifespan = compound.getInteger("lifespan");
		splashDamage = compound.getFloat("splashDamage");
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setInteger("lifespan", lifespan);
		compound.setFloat("splashDamage", splashDamage);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id)
	{
		if(id == 3)
		{
			SplatCraftParticleSpawner.spawnInksplosionParticle(this.posX, this.posY, this.posZ, 0, 0, 0, getColor(), getProjectileSize()*2);
			/*
			for (int i = 0; i < 32; ++i)
			{
				SplatCraftParticleSpawner.spawnInkParticle(this.posX, this.posY, this.posZ, 0, 0, 0, getColor(), getProjectileSize()*2);
			}
			*/
		}
	}
	
	@Override
	public boolean hasNoGravity()
	{
		return true;
	}
}
