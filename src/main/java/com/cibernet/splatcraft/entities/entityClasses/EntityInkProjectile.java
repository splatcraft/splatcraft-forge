package com.cibernet.splatcraft.entities.entityClasses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityInkProjectile extends Entity implements IProjectile
{
	
	public EntityInkProjectile(World worldIn)
	{
		super(worldIn);
	}
	
	public EntityInkProjectile(World worldIn, EntityPlayer playerIn, int inkColor)
	{
		super(worldIn);
	}
	
	@Override
	protected void entityInit()
	{
	
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
	
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
	
	}
	
	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy)
	{
	
	}
	
	public void shoot(EntityPlayer player, float a, float b, float c, float d, float e)
	{}
}
