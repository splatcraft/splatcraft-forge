package net.splatcraft.forge.entities;

import net.splatcraft.forge.client.particles.SquidSoulParticleData;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InkSquidEntity extends CreatureEntity implements IColoredEntity
{
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(InkSquidEntity.class, DataSerializers.INT);


    public InkSquidEntity(EntityType<? extends CreatureEntity> type, World level)
    {
        super(type, level);
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes()
    {
        return MobEntity.createLivingAttributes()
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
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
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
    protected int getExperienceReward(PlayerEntity player)
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

        if (level.getBlockState(pos).getBlock() == SplatcraftBlocks.inkwell && level.getBlockEntity(pos) instanceof InkColorTileEntity)
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
    public void readAdditionalSaveData(CompoundNBT nbt)
    {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        else setColor(ColorUtils.getRandomStarterColor());
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt)
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
