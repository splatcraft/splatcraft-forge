package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class InkExplosion
{
	
	/** whether or not this explosion spawns smoke particles */
	private final boolean damagesTerrain;
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	private final int color;
	private final Entity exploder;
	private final float size;
	/** A list of ChunkPositions of blocks affected by this explosion */
	private final List<BlockPos> affectedBlockPositions;
	/** Maps players to the knockback vector applied by the explosion, to send to the client */
	private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
	private final Vec3d position;
	private float damage = 0;
	
	@SideOnly(Side.CLIENT)
	public InkExplosion(World worldIn, Entity entityIn, double x, double y, double z, int size, int color, List<BlockPos> affectedPositions)
	{
		this(worldIn, entityIn, x, y, z, size, color,true, affectedPositions);
	}
	
	@SideOnly(Side.CLIENT)
	public InkExplosion(World worldIn, Entity entityIn, double x, double y, double z, int size, int color, boolean damagesTerrain, List<BlockPos> affectedPositions)
	{
		this(worldIn, entityIn, x, y, z, size, color, damagesTerrain);
		this.affectedBlockPositions.addAll(affectedPositions);
	}
	
	public InkExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, int color, boolean damagesTerrain)
	{
		this.affectedBlockPositions = Lists.<BlockPos>newArrayList();
		this.playerKnockbackMap = Maps.<EntityPlayer, Vec3d>newHashMap();
		this.world = worldIn;
		this.exploder = entityIn;
		this.size = size;
		this.x = x;
		this.y = y;
		this.z = z;
		this.damagesTerrain = damagesTerrain;
		this.position = new Vec3d(this.x, this.y, this.z);
		this.color = color;
		
		if(entityIn instanceof EntityInkProjectile)
			damage = ((EntityInkProjectile) entityIn).getDamage()/2;
	}
	
	/**
	 * Does the first part of the explosion (ink blocks)
	 */
	public void doExplosionA()
	{
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		int i = 16;
		
		for (int j = 0; j < 16; ++j)
		{
			for (int k = 0; k < 16; ++k)
			{
				for (int l = 0; l < 16; ++l)
				{
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
					{
						double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 = d0 / d3;
						d1 = d1 / d3;
						d2 = d2 / d3;
						float f = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
						double d4 = this.x;
						double d6 = this.y;
						double d8 = this.z;
						
						for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
						{
							RayTraceResult rayTrace = SplatCraftUtils.rayTraceBlocks(world, new Vec3d(x+0.5f,y+0.5f,z+0.5f), new Vec3d(d4+0.5f, d6+0.5f, d8+0.5f));
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							if(rayTrace != null)
								blockpos = rayTrace.getBlockPos();
							
							IBlockState iblockstate = this.world.getBlockState(blockpos);
							
							if (iblockstate.getMaterial() != Material.AIR)
							{
								float f2 = 0;
								f -= (f2 + 0.3F) * 0.3F;
							}
							
							if (f > 0.0F)
							{
								if(!set.contains(blockpos))
								{
									set.add(blockpos);
								}
							}
							
							d4 += d0 * 0.30000001192092896D;
							d6 += d1 * 0.30000001192092896D;
							d8 += d2 * 0.30000001192092896D;
						}
					}
				}
			}
		}
		
		this.affectedBlockPositions.addAll(set);
		float f3 = this.size * 2.0F;
		int k1 = MathHelper.floor(this.x - (double)f3 - 1.0D);
		int l1 = MathHelper.floor(this.x + (double)f3 + 1.0D);
		int i2 = MathHelper.floor(this.y - (double)f3 - 1.0D);
		int i1 = MathHelper.floor(this.y + (double)f3 + 1.0D);
		int j2 = MathHelper.floor(this.z - (double)f3 - 1.0D);
		int j1 = MathHelper.floor(this.z + (double)f3 + 1.0D);
		
		
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
		
		for (int k2 = 0; k2 < list.size(); ++k2)
		{
			Entity entity = list.get(k2);
			
			if (!entity.isImmuneToExplosions())
			{
				double d12 = entity.getDistance(this.x, this.y, this.z) / (double)f3;
				
				if (d12 <= 1.0D)
				{
					double d5 = entity.posX - this.x;
					double d7 = entity.posY + (double)entity.getEyeHeight() - this.y;
					double d9 = entity.posZ - this.z;
					double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
					
					if (d13 != 0.0D)
					{
						//TODO entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
						InkColors inkColor = InkColors.getByColor(color);
						if(entity instanceof EntitySheep && inkColor != null && inkColor.getDyeColor() != null)
							((EntitySheep) entity).setFleeceColor(inkColor.getDyeColor());
							
					}
				}
			}
		}
	}
	
	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	public void doExplosionB(boolean spawnParticles)
	{
		//this.world.playSound((EntityPlayer)null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
		
		if (this.size >= 2.0F && this.damagesTerrain)
		{
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
		}
		else
		{
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
		}
		
		if (this.damagesTerrain)
		{
			for (BlockPos blockpos : this.affectedBlockPositions)
			{
				IBlockState iblockstate = this.world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				
				if (spawnParticles)
				{
					double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
					double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
					double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
					double d3 = d0 - this.x;
					double d4 = d1 - this.y;
					double d5 = d2 - this.z;
					double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 = d3 / d6;
					d4 = d4 / d6;
					d5 = d5 / d6;
					double d7 = 0.5D / (d6 / (double)this.size + 0.1D);
					d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
					d3 = d3 * d7;
					d4 = d4 * d7;
					d5 = d5 * d7;
					this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
				}
				
				if (iblockstate.getMaterial() != Material.AIR)
					SplatCraftUtils.inkBlock(this.world, blockpos, this.color, damage);
				
			}
		}
	}
	
	public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap()
	{
		return this.playerKnockbackMap;
	}
	
	public void clearAffectedBlockPositions()
	{
		this.affectedBlockPositions.clear();
	}
	
	public List<BlockPos> getAffectedBlockPositions()
	{
		return this.affectedBlockPositions;
	}
	
	public Vec3d getPosition(){ return this.position; }
	
	/**
	 * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
	 */
	@Nullable
	public EntityLivingBase getExplosivePlacedBy()
	{
		if (this.exploder == null)
		{
			return null;
		}
		else if (this.exploder instanceof EntityTNTPrimed)
		{
			return ((EntityTNTPrimed)this.exploder).getTntPlacedBy();
		}
		else
		{
			return this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null;
		}
	}
}
