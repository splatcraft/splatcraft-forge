package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.crafting.InkColor;
import com.cibernet.splatcraft.crafting.InkColorManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class InkExplosion
{
	private final Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	@Nullable
	private final Entity exploder;
	private final float size;
	private final DamageSource damageSource;
	private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
	private final Map<PlayerEntity, Vector3d> playerKnockbackMap = Maps.newHashMap();
	private final Vector3d position;
	
	private final int color;
	private final InkBlockUtils.InkType inkType;
	private final boolean damageMobs;
	private final float damage;
	private final ItemStack weapon;
	
	public static void createInkExplosion(World world, Entity source, DamageSource damageSource, BlockPos pos, float size, float damage, boolean damageMobs, int color, InkBlockUtils.InkType type, ItemStack weapon)
	{
		InkExplosion inksplosion = new InkExplosion(world, source, damageSource, pos.getX(), pos.getY(), pos.getZ(), damage, damageMobs, size, color, type, weapon);
		
		inksplosion.doExplosionA();
		inksplosion.doExplosionB(false);
	}
	
	@OnlyIn(Dist.CLIENT)
	public InkExplosion(World worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, List<BlockPos> affectedBlockPositionsIn, float damage, boolean damageMobs, int color, InkBlockUtils.InkType inkType, ItemStack weapon) {
		this(worldIn, exploderIn, xIn, yIn, zIn, damage, damageMobs, sizeIn, color, inkType, weapon);
		this.affectedBlockPositions.addAll(affectedBlockPositionsIn);
	}
	
	@OnlyIn(Dist.CLIENT)
	public InkExplosion(World worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float damage, boolean damageMobs, float sizeIn, int color, InkBlockUtils.InkType inkType, ItemStack weapon) {
		this(worldIn, exploderIn, (DamageSource)null, xIn, yIn, zIn,damage, damageMobs, sizeIn, color, inkType, weapon);
	}
	
	public InkExplosion(World world, @Nullable Entity source, @Nullable DamageSource damageSource, double x, double y, double z, float damage, boolean damageMobs, float size, int color, InkBlockUtils.InkType inkType, ItemStack weapon) {
		this.world = world;
		this.exploder = source;
		this.size = size;
		this.x = x;
		this.y = y;
		this.z = z;
		this.damageSource = damageSource;
		this.position = new Vector3d(this.x, this.y, this.z);
		
		this.color = color;
		this.inkType = inkType;
		this.damageMobs = damageMobs;
		this.damage = damage;
		this.weapon = weapon;
	}
	
	public static float getBlockDensity(Vector3d p_222259_0_, Entity p_222259_1_) {
		AxisAlignedBB axisalignedbb = p_222259_1_.getBoundingBox();
		double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
		double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
		double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
		double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
		double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
		if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
			int i = 0;
			int j = 0;
			
			for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
				for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
					for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
						double d5 = MathHelper.lerp((double)f, axisalignedbb.minX, axisalignedbb.maxX);
						double d6 = MathHelper.lerp((double)f1, axisalignedbb.minY, axisalignedbb.maxY);
						double d7 = MathHelper.lerp((double)f2, axisalignedbb.minZ, axisalignedbb.maxZ);
						Vector3d vector3d = new Vector3d(d5 + d3, d6, d7 + d4);
						if (p_222259_1_.world.rayTraceBlocks(new RayTraceContext(vector3d, p_222259_0_, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, p_222259_1_)).getType() == RayTraceResult.Type.MISS) {
							++i;
						}
						
						++j;
					}
				}
			}
			
			return (float)i / (float)j;
		} else {
			return 0.0F;
		}
	}
	
	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	public void doExplosionA() {
		Set<BlockPos> set = Sets.newHashSet();
		int i = 16;
		
		for(int j = 0; j < 16; ++j)
		{
			for(int k = 0; k < 16; ++k)
			{
				for(int l = 0; l < 16; ++l)
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
						
						for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
						{
							BlockRayTraceResult raytrace = world.rayTraceBlocks(new RayTraceContext(new Vector3d(x+0.5f, y+0.5f, z+0.5f), new Vector3d(d4+0.5f, d6+0.5f, d8+0.5f), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							f -= 0.3f * 0.3F;
							
							if(raytrace != null)
								blockpos = raytrace.getPos();
							
							if(!set.contains(blockpos))
								set.add(blockpos);
							
							
							d4 += d0 * (double)0.3F;
							d6 += d1 * (double)0.3F;
							d8 += d2 * (double)0.3F;
						}
					}
				}
			}
		}
		
		this.affectedBlockPositions.addAll(set);
		float f2 = this.size * 2.0F;
		int k1 = MathHelper.floor(this.x - (double)f2 - 1.0D);
		int l1 = MathHelper.floor(this.x + (double)f2 + 1.0D);
		int i2 = MathHelper.floor(this.y - (double)f2 - 1.0D);
		int i1 = MathHelper.floor(this.y + (double)f2 + 1.0D);
		int j2 = MathHelper.floor(this.z - (double)f2 - 1.0D);
		int j1 = MathHelper.floor(this.z + (double)f2 + 1.0D);
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
		Vector3d vector3d = new Vector3d(this.x, this.y, this.z);
		
		for(int k2 = 0; k2 < list.size(); ++k2)
		{
			Entity entity = list.get(k2);
			
			int targetColor = -2;
			if(entity instanceof LivingEntity)
				targetColor = (int) ColorUtils.getEntityColor((LivingEntity) entity);
			
			if((targetColor == -1 && damageMobs) || (color != targetColor && targetColor > -1))
				InkDamageUtils.doSplatDamage(world, (LivingEntity) entity, damage, color, exploder, weapon, damageMobs, inkType); //TODO damage
			
			DyeColor dyeColor = null;
			
			if(InkColorManager.instance.getColorByHex(color) != null)
				dyeColor = InkColorManager.instance.getColorByHex(color).getDyeColor();
			
			if(dyeColor != null && entity instanceof SheepEntity)
				((SheepEntity) entity).setFleeceColor(dyeColor);
			
			/*
			if (!entity.isImmuneToExplosions())
			{
				double d12 = (double)(MathHelper.sqrt(entity.getDistanceSq(vector3d)) / f2);
				if (d12 <= 1.0D)
				{
					double d5 = entity.getPosX() - this.x;
					double d7 = (entity instanceof TNTEntity ? entity.getPosY() : entity.getPosYEye()) - this.y;
					double d9 = entity.getPosZ() - this.z;
					double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
					if (d13 != 0.0D) {
						d5 = d5 / d13;
						d7 = d7 / d13;
						d9 = d9 / d13;
						double d14 = (double)getBlockDensity(vector3d, entity);
						double d10 = (1.0D - d12) * d14;
						entity.attackEntityFrom(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f2 + 1.0D)));
						double d11 = d10;
						if (entity instanceof LivingEntity) {
							d11 = ProtectionEnchantment.getBlastDamageReduction((LivingEntity)entity, d10);
						}
					}
				}
			}
			*/
		}
		
	}
	
	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	public void doExplosionB(boolean spawnParticles)
	{
		/*
		if (this.world.isRemote) {
			this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
		}
		boolean flag = this.mode != Explosion.Mode.NONE;
		*/
		if (spawnParticles) {
			if (!(this.size < 2.0F)) {
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			} else {
				this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			}
		}
		
		if (true) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
			Collections.shuffle(this.affectedBlockPositions, this.world.rand);
			
			for(BlockPos blockpos : this.affectedBlockPositions)
			{
				BlockState blockstate = this.world.getBlockState(blockpos);
				Block block = blockstate.getBlock();
				if (!blockstate.isAir(this.world, blockpos))
				{
					InkBlockUtils.inkBlock(world, blockpos, color, inkType);
					/*
					BlockPos blockpos1 = blockpos.toImmutable();
					this.world.getProfiler().startSection("explosion_blocks");
					if (blockstate.canDropFromExplosion(this.world, blockpos, this) && this.world instanceof ServerWorld) {
						TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
						LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.world.rand).withParameter(LootParameters.POSITION, blockpos).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity).withNullableParameter(LootParameters.THIS_ENTITY, this.exploder);
						if (this.mode == Explosion.Mode.DESTROY) {
							lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.size);
						}
						
						blockstate.getDrops(lootcontext$builder).forEach((p_229977_2_) -> {
							func_229976_a_(objectarraylist, p_229977_2_, blockpos1);
						});
					}
					
					blockstate.onBlockExploded(this.world, blockpos, this);
					this.world.getProfiler().endSection();
					*/
				}
				
			}
			
			for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
				Block.spawnAsEntity(this.world, pair.getSecond(), pair.getFirst());
			}
		}
		
	}
	
	private static void func_229976_a_(ObjectArrayList<Pair<ItemStack, BlockPos>> p_229976_0_, ItemStack p_229976_1_, BlockPos p_229976_2_) {
		int i = p_229976_0_.size();
		
		for(int j = 0; j < i; ++j) {
			Pair<ItemStack, BlockPos> pair = p_229976_0_.get(j);
			ItemStack itemstack = pair.getFirst();
			if (ItemEntity.canMergeStacks(itemstack, p_229976_1_)) {
				ItemStack itemstack1 = ItemEntity.mergeStacks(itemstack, p_229976_1_, 16);
				p_229976_0_.set(j, Pair.of(itemstack1, pair.getSecond()));
				if (p_229976_1_.isEmpty()) {
					return;
				}
			}
		}
		
		p_229976_0_.add(Pair.of(p_229976_1_, p_229976_2_));
	}
	
	public DamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public Map<PlayerEntity, Vector3d> getPlayerKnockbackMap() {
		return this.playerKnockbackMap;
	}
	
	/**
	 * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
	 */
	@Nullable
	public LivingEntity getExplosivePlacedBy() {
		if (this.exploder == null) {
			return null;
		} else if (this.exploder instanceof TNTEntity) {
			return ((TNTEntity)this.exploder).getTntPlacedBy();
		} else if (this.exploder instanceof LivingEntity) {
			return (LivingEntity)this.exploder;
		} else {
			if (this.exploder instanceof ProjectileEntity) {
				Entity entity = ((ProjectileEntity)this.exploder).func_234616_v_();
				if (entity instanceof LivingEntity) {
					return (LivingEntity)entity;
				}
			}
			
			return null;
		}
	}
	
	public void clearAffectedBlockPositions() {
		this.affectedBlockPositions.clear();
	}
	
	public List<BlockPos> getAffectedBlockPositions() {
		return this.affectedBlockPositions;
	}
	
	public Vector3d getPosition() {
		return this.position;
	}
	
	@Nullable
	public Entity getExploder() {
		return this.exploder;
	}
	
}
