package net.splatcraft.forge.entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.data.capabilities.inkoverlay.IInkOverlayInfo;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateInkOverlayPacket;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.CommonUtils;
import net.splatcraft.forge.util.InkDamageUtils;

import java.util.Collections;

public class SquidBumperEntity extends LivingEntity implements IColoredEntity {
    public static final float maxInkHealth = 20.0F;
    public static final int maxRespawnTime = 60;
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(SquidBumperEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> RESPAWN_TIME = EntityDataManager.defineId(SquidBumperEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> SPLAT_HEALTH = EntityDataManager.defineId(SquidBumperEntity.class, DataSerializers.FLOAT);
    public boolean inkproof = false;
    /**
     * After punching the stand, the cooldown before you can punch it again without breaking it.
     */
    public long punchCooldown;
    public long hurtCooldown;

    public int prevRespawnTime = 0;

    public SquidBumperEntity(EntityType<? extends LivingEntity> type, World levelIn)
    {
        super(type, levelIn);
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes()
    {
        return MobEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(COLOR, ColorUtils.DEFAULT);
        entityData.define(SPLAT_HEALTH, maxInkHealth);
        entityData.define(RESPAWN_TIME, maxRespawnTime);
    }

    @Override
    public void tick()
    {
        super.tick();

        hurtCooldown = Math.max(hurtCooldown - 1, 0);

        prevRespawnTime = entityData.get(RESPAWN_TIME);

        if (getRespawnTime() > 1)
        {
            setRespawnTime(getRespawnTime() - 1);
        } else if (getRespawnTime() == 10)
            level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.squidBumperRespawning, getSoundSource(), 1, 1);
        else if(getRespawnTime() == 1)
            respawn();

        BlockPos pos = getBlockPosBelowThatAffectsMyMovement();

        if (level.getBlockState(pos).getBlock() == SplatcraftBlocks.inkwell && level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(pos);
            if (te.getColor() != getColor())
                setColor(te.getColor());
        }
    }

    @Override
    public boolean onEntityInked(InkDamageUtils.InkDamageSource source, float damage, int color)
    {
        if (hurtCooldown <= 0 && getInkHealth() > 0 && !inkproof)
        {
            ink(damage, color);
            if (getInkHealth() <= 0)
            {
                this.level.broadcastEntityEvent(this, (byte) 34);
            }
            return true;
        }
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!this.level.isClientSide && this.isAlive())
        {
            if (DamageSource.OUT_OF_WORLD.equals(source))
            {
                this.remove();
                return false;
            } else if (!this.isInvulnerableTo(source))
            {
                if (source.isExplosion())
                {
                    dropBumper();
                    this.remove();
                    return false;
                } else if (DamageSource.IN_FIRE.equals(source))
                {
                    if (this.isOnFire())
                    {
                        this.damageBumper(source, 0.15F);
                    } else
                    {
                        this.setSecondsOnFire(5);
                    }

                    return false;
                } else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F)
                {
                    this.damageBumper(source, 4.0F);
                    return false;
                } else
                {
                    boolean flag = source.getDirectEntity() instanceof AbstractArrowEntity;
                    boolean flag1 = flag && ((AbstractArrowEntity) source.getDirectEntity()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(source.getMsgId());
                    if (!flag2 && !flag)
                    {
                        return false;
                    } else if (source.getEntity() instanceof PlayerEntity && !((PlayerEntity) source.getEntity()).abilities.mayBuild)
                    {
                        return false;
                    } else if (source.isCreativePlayer())
                    {
                        this.playBrokenSound();
                        this.playParticles();
                        this.remove();
                        return flag1;
                    } else
                    {
                        long i = this.level.getGameTime();
                        if (i - this.punchCooldown > 5L && !flag)
                        {
                            this.level.broadcastEntityEvent(this, (byte) 32);
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

    private void playParticles()
    {
        if (this.level instanceof ServerWorld)
        {
            ((ServerWorld) this.level).sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.defaultBlockState()), this.getX(), this.getEyePosition(0.6666666666666666f).y(), this.getZ(), 10, this.getBbWidth() / 4.0F, this.getBbHeight() / 4.0F, this.getBbWidth() / 4.0F, 0.05D);
        }

    }

    private void playPopParticles()
    {
        for (int i = 0; i < 10; i++)
        {
            level.addParticle(new InkSplashParticleData(getColor(), 2), getX(), getY() + getBbHeight() * 0.5, getZ(), random.nextDouble() * 0.5 - 0.25, random.nextDouble() * 0.5 - 0.25, random.nextDouble() * 0.5 - 0.25);
        }
        level.addParticle(new InkExplosionParticleData(getColor(), 2), getX(), getY() + getBbHeight() * 0.5, getZ(), 0, 0, 0);

    }

    private void playBrokenSound()
    {
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SplatcraftSounds.squidBumperBreak, this.getSoundSource(), 1.0F, 1.0F);
    }

    private void damageBumper(DamageSource source, float dmg)
    {
        float f = this.getHealth();
        f = f - dmg;
        if (f <= 0.5F)
        {
            this.dropBumper();
            this.remove();
        } else
        {
            this.setHealth(f);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id)
    {
        switch (id)
        {
            case 31:
                if (this.level.isClientSide)
                {
                    hurtCooldown = level.getGameTime();
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SplatcraftSounds.squidBumperInk, this.getSoundSource(), 0.3F, 1.0F, false);
                }
                break;
            case 32:
                if (this.level.isClientSide)
                {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SplatcraftSounds.squidBumperHit, this.getSoundSource(), 0.3F, 1.0F, false);
                    this.punchCooldown = this.level.getGameTime();
                }
                break;
            case 34:
                if (this.level.isClientSide)
                {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SplatcraftSounds.squidBumperPop, this.getSoundSource(), 0.5F, 20.0F, false);
                    InkOverlayCapability.get(this).setAmount(0);
                    playPopParticles();
                }
                break;


            default:
                super.handleEntityEvent(id);
        }

    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void doPush(Entity entityIn)
    {
        if(getInkHealth() <= 0)
            return;

        if (!this.isPassengerOfSameVehicle(entityIn))
        {
            if (!entityIn.noPhysics && !this.noPhysics)
            {
                double d0 = entityIn.getX() - this.getX();
                double d1 = entityIn.getZ() - this.getZ();
                double d2 = MathHelper.absMax(d0, d1);

                if (d2 >= 0.009999999776482582D)
                {
                    d2 = MathHelper.sqrt(d2);
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
                    d0 = d0 * (double) (1.0F - this.pushthrough);
                    d1 = d1 * (double) (1.0F - this.pushthrough);
                    d0 *= 3;
                    d1 *= 3;

                    if (!entityIn.isVehicle())
                    {
                        entityIn.push(d0, 0.0D, d1);
                    }
                }
            }
        }
    }

    @Override
    public void knockback(float p_233627_1_, double p_233627_2_, double p_233627_4_)
    {
    }

    public void dropBumper()
    {
        CommonUtils.blockDrop(this.level, this.blockPosition(), ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.squidBumper), getColor()), true));
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.squidBumper), getColor()), true);
    }


    @Override
    public Iterable<ItemStack> getArmorSlots()
    {
        return Collections.EMPTY_LIST;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slotIn)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType slotIn, ItemStack stack)
    {

    }

    @Override
    public HandSide getMainArm()
    {
        return HandSide.RIGHT;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt)
    {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        else setColor(ColorUtils.getRandomStarterColor());

        if (nbt.contains("Inkproof"))
            inkproof = nbt.getBoolean("Inkproof");

        if(nbt.contains("InkHealth"))
            setInkHealth(nbt.getFloat("InkHealth"));
        if(nbt.contains("RegenTicks"))
            setRespawnTime(nbt.getInt("RegenTicks"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Color", getColor());
        nbt.putBoolean("Inkproof", inkproof);

        nbt.putFloat("InkHealth", getInkHealth());
        nbt.putInt("RegenTicks", getRespawnTime());
    }

    @Override
    public int getColor()
    {
        return entityData.get(COLOR);
    }

    @Override
    public void setColor(int color)
    {
        entityData.set(COLOR, color);
    }

    public float getInkHealth()
    {
        return entityData.get(SPLAT_HEALTH);
    }

    public void setInkHealth(float value)
    {
        entityData.set(SPLAT_HEALTH, value);
    }

    public int getRespawnTime()
    {
        return entityData.get(RESPAWN_TIME);
    }

    public float getBumperScale(float partialTicks)
    {
        return getInkHealth() <= 0 ? (10 - Math.min(MathHelper.lerp(partialTicks, prevRespawnTime, getRespawnTime()), 10)) / 10f : 1;
    }

    public void setRespawnTime(int value)
    {
        entityData.set(RESPAWN_TIME, value);
    }

    public void ink(float damage, int color)
    {
        setInkHealth(getInkHealth() - damage);
        setRespawnTime(maxRespawnTime);
        this.level.broadcastEntityEvent(this, (byte) 31);
        hurtCooldown = invulnerableTime;

        if (!level.isClientSide)
            if(!isInWater() && InkOverlayCapability.hasCapability(this))
            {
                IInkOverlayInfo info = InkOverlayCapability.get(this);

                if (getInkHealth() > 0)
                {
                    if (info.getAmount() < maxInkHealth * 1.5)
                        info.addAmount(damage);
                }
                else info.setAmount(0);

                info.setColor(color);
                SplatcraftPacketHandler.sendToAll(new UpdateInkOverlayPacket(this, info));
            }
    }


    public void respawn()
    {
        if (getInkHealth() <= 0)
            level.playSound(null, getX(), getY(), getZ(), SplatcraftSounds.squidBumperReady, getSoundSource(), 1, 1);
        setInkHealth(maxInkHealth);
        setRespawnTime(0);

        InkOverlayCapability.get(this).setAmount(0);

        //updateBoundingBox();

    }

    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
