package com.cibernet.splatcraft.entities.classes;

import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBlasterProjectile extends EntityInkProjectile
{
	private int lifespan = 1;
	
	public EntityBlasterProjectile(World worldIn, EntityLivingBase throwerIn, int color, float damage, int lifespan)
	{
		super(worldIn, throwerIn, color, damage);
		this.lifespan = lifespan;
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
			SplatCraftUtils.createInkExplosion(world, this, pos, getProjectileSize()*2f, getColor());
			this.setDead();
			this.world.setEntityState(this, (byte)3);
		}
		else
		{
			for(double y = posY; y >= 0 && posY-y <= 16; y--)
			{
				BlockPos inkPos = new BlockPos(posX, y, posZ);
				if(SplatCraftUtils.canInk(world, inkPos))
				{
					SplatCraftUtils.createInkExplosion(world, inkPos, getProjectileSize()/5f, getColor());
					SplatCraftUtils.createInkExplosion(world, pos, getProjectileSize()/5f, getColor());
					break;
				}
			}
		}
		
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
