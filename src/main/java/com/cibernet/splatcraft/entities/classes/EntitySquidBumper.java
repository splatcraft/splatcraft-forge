package com.cibernet.splatcraft.entities.classes;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftDamageSource;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class EntitySquidBumper extends EntityLivingBase
{
	private static final float maxInkHealth = 20.0F;
	public static final int maxRespawnTime = 60;
	private static final Iterable<ItemStack> armorInv = NonNullList.<ItemStack>withSize(0, ItemStack.EMPTY);
	private static final Predicate<Entity> IS_RIDEABLE_MINECART = entity -> entity instanceof EntityMinecart && ((EntityMinecart)entity).canBeRidden();
	
	private static final DataParameter<Float> RESPAWN_TIME = EntityDataManager.createKey(EntitySquidBumper.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> COLOR = EntityDataManager.createKey(EntitySquidBumper.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> SPLAT_HEALTH = EntityDataManager.createKey(EntitySquidBumper.class, DataSerializers.FLOAT);
	
	/** After punching the stand, the cooldown before you can punch it again without breaking it. */
	public long punchCooldown;
	public long hurtCooldown;
	
	public EntitySquidBumper(World worldIn)
	{
		super(worldIn);
		setSize(0.6f, 1.8f);
	}
	
	public EntitySquidBumper(World world, double x, double y, double z, int color)
	{
		super(world);
		setPosition(x,y,z);
		setColor(color);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataManager.register(COLOR, (float) SplatCraft.DEFAULT_INK);
		dataManager.register(SPLAT_HEALTH, maxInkHealth);
		dataManager.register(RESPAWN_TIME, 0f);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(world.isRemote || isDead) return false;
		
		
		if(hurtResistantTime > maxHurtResistantTime) return false;
		
		if(source instanceof SplatCraftDamageSource)
		{
			if(getInkHealth() > 0 && hurtResistantTime <= 0)
			{
				ink(amount);
				if(getInkHealth() <= 0)
					this.world.setEntityState(this, (byte)34);
			}
			
			return false;
		}
		
		else if(source.equals(DamageSource.OUT_OF_WORLD))
		{
			setDead();
			return false;
		}
		if(!isEntityInvulnerable(source))
		{
			if(source.equals(DamageSource.IN_FIRE))
			{
				if(isBurning())
					damage(0.15f);
				else setFire(5);
				return false;
			}
			else if(source.equals(DamageSource.ON_FIRE))
			{
				damage(4);
				return false;
			}
			else if(source.getImmediateSource() instanceof EntityArrow)
			{
				setDead();
				dropAsItem();
				return false;
			}
			else if (source.getTrueSource() instanceof EntityPlayer && !((EntityPlayer)source.getTrueSource()).capabilities.allowEdit)
			{
				return false;
			}
			else if (source.isCreativePlayer())
			{
				this.playBrokenSound();
				this.playParticles();
				this.setDead();
				return false;
			}
			else
			{
				long i = this.world.getTotalWorldTime();
				
				if (i - this.punchCooldown > 5L && !"arrow".equals(source.getDamageType()))
				{
					this.world.setEntityState(this, (byte)32);
					this.punchCooldown = i;
				}
				else
				{
					this.dropAsItem();
					this.playParticles();
					this.setDead();
				}
				
				return false;
			}
		}
		
		
		return false;
	}
	
	/**
	 * Handler for {@link World#setEntityState}
	 */
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id)
	{
		switch(id)
		{
			case 31:
				if (this.world.isRemote)
					hurtCooldown = world.getTotalWorldTime();
			break;
			case 32:
				if (this.world.isRemote)
				{
					this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ARMORSTAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
					this.punchCooldown = this.world.getTotalWorldTime();
				}
			break;
			case 34:
				if(this.world.isRemote)
				{
					this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, this.getSoundCategory(), 0.5F, 20.0F, false);
					playPopParticles();
				}
			break;
			default:
				super.handleStatusUpdate(id);
			break;
		}
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		hurtResistantTime = Math.max(hurtResistantTime-1, 0);
		
		if(getRespawnTime() > 1)
			setRespawnTime(getRespawnTime()-1);
		else if(getRespawnTime() == 1)
			respawn();
		
		BlockPos pos = new BlockPos(posX, posY-1, posZ);
		
		if(world.getBlockState(pos).getBlock().equals(SplatCraftBlocks.inkwell) && world.getTileEntity(pos) instanceof TileEntityColor)
		{
			TileEntityColor te = (TileEntityColor) world.getTileEntity(pos);
			if(te.getColor() != getColor())
				setColor(te.getColor());
		}
	}
	
	
	@Override
	public boolean canBeCollidedWith()
	{
		return getInkHealth() > 0;
	}
	
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
				double d0 = entityIn.posX - this.posX;
				double d1 = entityIn.posZ - this.posZ;
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
	
	@Override
	public ItemStack getPickedResult(RayTraceResult target)
	{
		return ColorItemUtils.setInkColor(new ItemStack(SplatCraftItems.squidBumper), getColor());
	}
	
	private void updateBoundingBox()
	{
		setSize(width, getRespawnTime() > 0 ? 1 : height);
	}
	
	private void damage(float damage)
	{
		float health = getHealth() - damage;
		
		if(health < .5F)
			setDead();
		else setHealth(health);
	}
	
	private void ink(float damage)
	{
		setInkHealth(getInkHealth()-damage);
		setRespawnTime(maxRespawnTime);
		this.world.setEntityState(this, (byte)31);
		hurtCooldown = world.getTotalWorldTime();
		hurtResistantTime = maxHurtResistantTime;
		
		//updateBoundingBox();
	}
	
	private void respawn()
	{
		if(getInkHealth() <= 0)
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_CHIME, getSoundCategory(), 1, 4);
		setInkHealth(maxInkHealth);
		setRespawnTime(0);
		//updateBoundingBox();
		
	}
	
	private void dropAsItem()
	{
		Block.spawnAsEntity(this.world, new BlockPos(this), ColorItemUtils.setInkColor(new ItemStack(SplatCraftItems.squidBumper),getColor()));
	}
	
	private void playBrokenSound()
	{
		this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ARMORSTAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
	}
	
	private void playParticles()
	{
		if (this.world instanceof WorldServer)
			((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY + (double)this.height / 1.5D, this.posZ, 10, (double)(this.width / 4.0F), (double)(this.height / 4.0F), (double)(this.width / 4.0F), 0.05D, Block.getStateId(Blocks.WOOL.getDefaultState()));
	}
	
	private void playPopParticles()
	{
		for(int i = 0; i < 10; i++)
			SplatCraftParticleSpawner.spawnInkParticle(posX, posY + height * 0.5, posZ, rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25, getColor(), 2);
		SplatCraftParticleSpawner.spawnInksplosionParticle(posX, posY + height * 0.5, posZ, 0, 0, 0, getColor(), 2);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		
		if(compound.hasKey("Color"))
			setColor(compound.getInteger("Color"));
		if(compound.hasKey("InkHealth"))
			setInkHealth(compound.getFloat("InkHealth"));
		if(compound.hasKey("RespawnTime"))
			setRespawnTime(compound.getInteger("RespawnTime"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		
		compound.setInteger("Color", getColor());
		compound.setInteger("RespawnTime", getRespawnTime());
		compound.setFloat("InkHealth", getInkHealth());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey("Color"))
			setColor(compound.getInteger("Color"));
		else setColor(InkColors.getRandomStarterColor().getColor());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("Color", getColor());
		return super.writeToNBT(compound);
	}
	
	
	public int getColor() {return dataManager.get(COLOR).intValue();}
	public void setColor(int color) {dataManager.set(COLOR, (float)color);}
	public float getInkHealth() {return dataManager.get(SPLAT_HEALTH);}
	public void setInkHealth(float health) {dataManager.set(SPLAT_HEALTH, health);}
	public int getRespawnTime() {return dataManager.get(RESPAWN_TIME).intValue();}
	public void setRespawnTime(int time) {dataManager.set(RESPAWN_TIME, (float) time);}
	
	@Override
	public Iterable<ItemStack> getArmorInventoryList()
	{
		return armorInv;
	}
	
	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
	{
	
	}
	
	@Override
	public EnumHandSide getPrimaryHand()
	{
		return EnumHandSide.LEFT;
	}
	
}
