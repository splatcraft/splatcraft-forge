package net.splatcraft.forge.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.client.particles.SquidSoulParticleData;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;

public class InkSquidEntity extends PathfinderMob implements IColoredEntity
{
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(InkSquidEntity.class, EntityDataSerializers.INT);

    public InkSquidEntity(EntityType<? extends PathfinderMob> type, Level level)
    {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes()
    {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(COLOR, ColorUtils.DEFAULT);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }



    @Override
    public void die(DamageSource source)
    {
        level.broadcastEntityEvent(this, (byte) 60);
        super.die(source);
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 60)
        {
            level.addParticle(new SquidSoulParticleData(getColor()), this.getX(), this.getY(), this.getZ(), 0, 1, 0);
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected int getExperienceReward(Player player)
    {
        return 0;
    }

    @Override
    protected boolean shouldDropExperience() {
        return false;
    }



    @Override
    public void tick()
    {
        super.tick();

        BlockPos pos = getBlockPosBelowThatAffectsMyMovement();

        if (level.getBlockState(pos).getBlock() == SplatcraftBlocks.inkwell.get() && level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(pos);
            if (te.getColor() != getColor())
            {
                setColor(te.getColor());
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state)
    {
        playSound(SoundEvents.HONEY_BLOCK_FALL, 0.15F, 1.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        else setColor(ColorUtils.getRandomStarterColor());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Color", getColor());
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

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }
}
