package com.cibernet.splatcraft.entities;

import com.cibernet.splatcraft.client.particles.InkExplosionParticleData;
import com.cibernet.splatcraft.client.particles.InkSplashParticleData;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdateInkOverlayPacket;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkDamageUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Collections;

public class SquidBumperEntity extends LivingEntity implements IColoredEntity
{
	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(SquidBumperEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RESPAWN_TIME = EntityDataManager.createKey(SquidBumperEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Float> SPLAT_HEALTH = EntityDataManager.createKey(SquidBumperEntity.class, DataSerializers.FLOAT);
	
	public static final float maxInkHealth = 20.0F;
	public static final int maxRespawnTime = 60;
	public boolean inkproof = false;
	/** After punching the stand, the cooldown before you can punch it again without breaking it. */
	public long punchCooldown;
	public long hurtCooldown;
	
	public SquidBumperEntity(EntityType<? extends LivingEntity> type, World worldIn)
	{
		super(type, worldIn);
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes()
	{
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 20).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0D);
	}
	
	@Override
	protected void registerData()
	{
		super.registerData();
		dataManager.register(COLOR, ColorUtils.DEFAULT);
		dataManager.register(SPLAT_HEALTH, maxInkHealth);
		dataManager.register(RESPAWN_TIME, maxRespawnTime);
	}
	
	@Override
	public void livingTick()
	{
		super.livingTick();
		
		hurtResistantTime = Math.max(hurtResistantTime-1, 0);
		
		if(getRespawnTime() > 1)
			setRespawnTime(getRespawnTime()-1);
		else if(getRespawnTime() == 1)
			respawn();
		
		
		BlockPos pos = getPositionUnderneath();
		
		if(world.getBlockState(pos).getBlock() == SplatcraftBlocks.inkwell && world.getTileEntity(pos) instanceof InkColorTileEntity)
		{
			InkColorTileEntity te = (InkColorTileEntity) world.getTileEntity(pos);
			if(te.getColor() != getColor())
				setColor(te.getColor());
		}
	}
	
	@Override
	public boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage, int color)
	{
		
		if(getInkHealth() > 0 && !inkproof && (getColor() != color || SplatcraftGameRules.getBooleanRuleValue(world, SplatcraftGameRules.INK_FRIENDLY_FIRE)))
		{
			ink(damage);
			if(getInkHealth() <= 0)
			{
				this.world.setEntityState(this, (byte) 34);
				InkOverlayCapability.get(this).setAmount(0);
			}
		}
		return false;
	}
	
	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(!this.world.isRemote && this.isAlive())
		{
			if(DamageSource.OUT_OF_WORLD.equals(source))
			{
				this.remove();
				return false;
			} else if(!this.isInvulnerableTo(source))
			{
				if(source.isExplosion())
				{
					dropBumper();
					this.remove();
					return false;
				} else if(DamageSource.IN_FIRE.equals(source))
				{
					if(this.isBurning())
					{
						this.damageBumper(source, 0.15F);
					} else
					{
						this.setFire(5);
					}
					
					return false;
				} else if(DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F)
				{
					this.damageBumper(source, 4.0F);
					return false;
				} else
				{
					boolean flag = source.getImmediateSource() instanceof AbstractArrowEntity;
					boolean flag1 = flag && ((AbstractArrowEntity) source.getImmediateSource()).getPierceLevel() > 0;
					boolean flag2 = "player".equals(source.getDamageType());
					if(!flag2 && !flag)
					{
						return false;
					} else if(source.getTrueSource() instanceof PlayerEntity && !((PlayerEntity) source.getTrueSource()).abilities.allowEdit)
					{
						return false;
					} else if(source.isCreativePlayer())
					{
						this.playBrokenSound();
						this.playParticles();
						this.remove();
						return flag1;
					} else
					{
						long i = this.world.getGameTime();
						if(i - this.punchCooldown > 5L && !flag)
						{
							this.world.setEntityState(this, (byte) 32);
							this.punchCooldown = i;
						} else
						{
							this.dropBumper();
							this.playParticles();
							this.remove();
						}
						
						return true;
					}
				}
			} else
			{
				return false;
			}
		} else
		{
			return false;
		}
	}
	
	private void playParticles() {
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.getDefaultState()), this.getPosX(), this.getPosYHeight(0.6666666666666666D), this.getPosZ(), 10, (double)(this.getWidth() / 4.0F), (double)(this.getHeight() / 4.0F), (double)(this.getWidth() / 4.0F), 0.05D);
		}
		
	}
	
	private void playPopParticles()
	{
		for(int i = 0; i < 10; i++)
			world.addParticle(new InkSplashParticleData( getColor(), 2), getPosX(), getPosY() + getHeight() * 0.5, getPosZ(), rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25);
		world.addParticle(new InkExplosionParticleData(getColor(), 2),getPosX(), getPosY()+getHeight() * 0.5, getPosZ(), 0, 0, 0);

	}
	
	private void playBrokenSound()
	{
		this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
	}
	
	private void damageBumper(DamageSource source, float dmg)
	{
		float f = this.getHealth();
		f = f - dmg;
		if (f <= 0.5F)
		{
			this.dropBumper();
			this.remove();
		} else this.setHealth(f);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		switch(id)
		{
			case 31:
				if (this.world.isRemote)
					hurtCooldown = world.getGameTime();
			break;
			case 32:
				if (this.world.isRemote) {
					this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
					this.punchCooldown = this.world.getGameTime();
				}
			break;
			case 34:
				if(this.world.isRemote)
				{
					this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, this.getSoundCategory(), 0.5F, 20.0F, false);
					InkOverlayCapability.get(this).setAmount(0);
					playPopParticles();
				}
			break;
			
			
			
			default: super.handleStatusUpdate(id);
		}
		
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return getInkHealth() > 0;
	}
	
	@Override
	protected void collideWithEntity(Entity entityIn)
	{
		if(getInkHealth() > 0)
			this.applyEntityCollision(entityIn);
	}
	
	@Override
	public void applyEntityCollision(Entity entityIn)
	{
		if (!this.isRidingSameEntity(entityIn))
		{
			if (!entityIn.noClip && !this.noClip)
			{
				double d0 = entityIn.getPosX() - this.getPosX();
				double d1 = entityIn.getPosZ() - this.getPosZ();
				double d2 = MathHelper.absMax(d0, d1);
				
				if (d2 >= 0.009999999776482582D)
				{
					d2 = (double)MathHelper.sqrt(d2);
					d0 = d0 / d2;
					d1 = d1 / d2;
					double d3 = 1.0D / d2;
					
					if (d3 > 1.0D)
					{
						d3 = 1.0D;
					}
					
					d0 = d0 * d3;
					d1 = d1 * d3;
					d0 = d0 * 0.05000000074505806D;
					d1 = d1 * 0.05000000074505806D;
					d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
					d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
					d0 *= 3;
					d1 *= 3;
					
					if (!entityIn.isBeingRidden())
						entityIn.addVelocity(d0, 0.0D, d1);
				}
			}
		}
	}
	
	public void dropBumper()
	{
		Block.spawnAsEntity(this.world, this.getPosition(), ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.squidBumper), getColor()), true));
	}
	
	@Override
	public ItemStack getPickedResult(RayTraceResult target)
	{
		return ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.squidBumper), getColor()), true);
	}
	
	
	@Override
	public Iterable<ItemStack> getArmorInventoryList()
	{
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack)
	{
	
	}
	
	@Override
	public HandSide getPrimaryHand()
	{
		return HandSide.RIGHT;
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt)
	{
		super.readAdditional(nbt);
		if(nbt.contains("Color"))
			setColor(nbt.getInt("Color"));
		else setColor(ColorUtils.getRandomStarterColor());
		
		if(nbt.contains("Inkproof"))
			inkproof = nbt.getBoolean("Inkproof");
	}
	
	@Override
	public void writeAdditional(CompoundNBT nbt)
	{
		super.writeAdditional(nbt);
		nbt.putInt("Color", getColor());
		if(inkproof)
			nbt.putBoolean("Inkproof", inkproof);
	}
	
	@Override
	public int getColor()
	{
		return dataManager.get(COLOR);
	}
	
	@Override
	public void setColor(int color)
	{
		dataManager.set(COLOR, color);
	}
	
	public float getInkHealth()
	{
		return dataManager.get(SPLAT_HEALTH);
	}
	
	public void setInkHealth(float value)
	{
		dataManager.set(SPLAT_HEALTH, value);
	}
	
	public int getRespawnTime()
	{
		return dataManager.get(RESPAWN_TIME);
	}
	
	public void setRespawnTime(int value)
	{
		dataManager.set(RESPAWN_TIME, value);
	}
	
	public void ink(float damage)
	{
		setInkHealth(getInkHealth() - damage);
		setRespawnTime(maxRespawnTime);
		this.world.setEntityState(this, (byte) 31);
		hurtCooldown = world.getGameTime();
		hurtResistantTime = maxHurtResistantTime;
	}
	
	
	public void respawn()
	{
		if(getInkHealth() <= 0)
			world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME, getSoundCategory(), 1, 4);
		setInkHealth(maxInkHealth);
		setRespawnTime(0);

		InkOverlayCapability.get(this).setAmount(0);

		//updateBoundingBox();
		
	}
	
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
