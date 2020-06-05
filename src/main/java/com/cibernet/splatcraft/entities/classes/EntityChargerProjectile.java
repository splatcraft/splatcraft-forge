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
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		for(double y = posY; y >= 0 && posY-y <= 16; y--)
		{
			BlockPos inkPos = new BlockPos(posX, y, posZ);
			if(SplatCraftUtils.canInk(world, inkPos))
			{
				SplatCraftUtils.createInkExplosion(world, inkPos, getProjectileSize()*5, getColor());
				SplatCraftUtils.createInkExplosion(world, new BlockPos(posX, posY, posZ), getProjectileSize(), getColor());
				break;
			}
		}
		
		if(!world.isRemote)
			lifespan--;
		if(lifespan == 0)
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
