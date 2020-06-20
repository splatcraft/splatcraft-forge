package com.cibernet.splatcraft.entities.classes;

import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityChargerProjectile extends EntityInkProjectile
{
	private int lifespan = 1;
	
	public EntityChargerProjectile(World worldIn)
	{
		super(worldIn);
	}
	
	public EntityChargerProjectile(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
	}
	
	public EntityChargerProjectile(World worldIn, EntityLivingBase throwerIn, int color, float damage, int lifespan)
	{
		super(worldIn, throwerIn, color, damage);
		this.lifespan = lifespan;
		
		setTrail(false);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		for(double y = posY; y >= 0 && posY-y <= 16; y--)
		{
			BlockPos inkPos = new BlockPos(posX, y, posZ);
			if(!SplatCraftUtils.canInkPassthrough(world, inkPos))
			{
				SplatCraftUtils.createInkExplosion(world, inkPos.up(), getProjectileSize()/2f, getColor(), glowingInk);
				SplatCraftUtils.createInkExplosion(world, new BlockPos(posX, posY, posZ), getProjectileSize()/2f, getColor(), glowingInk);
				break;
			}
		}
		
		if(!world.isRemote)
			lifespan--;
		if(lifespan <= 0)
			setDead();
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		lifespan = compound.getInteger("lifespan");
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setInteger("lifespan", lifespan);
	}
	
	@Override
	public boolean hasNoGravity()
	{
		return true;
	}
}
