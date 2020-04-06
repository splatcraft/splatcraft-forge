package com.cibernet.splatcraft.entities.classes;

import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.EntityLivingBase;
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
		
		for(double y = posY; y >= 0 && posY-y <= 32; y--)
		{
			BlockPos inkPos = new BlockPos(posX, y, posZ);
			if(SplatCraftUtils.canInk(world, inkPos))
			{
				SplatCraftUtils.createInkExplosion(world, inkPos, 5, getColor());
				break;
			}
		}
		lifespan--;
		if(lifespan == 0)
			setDead();
	}
	
	@Override
	public boolean hasNoGravity()
	{
		return true;
	}
}
