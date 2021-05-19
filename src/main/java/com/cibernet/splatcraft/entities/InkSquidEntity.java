package com.cibernet.splatcraft.entities;

import com.cibernet.splatcraft.client.particles.SquidSoulParticleData;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
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
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(InkSquidEntity.class, DataSerializers.VARINT);


    public InkSquidEntity(EntityType<? extends CreatureEntity> type, World world)
    {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes()
    {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 20).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        dataManager.register(COLOR, ColorUtils.DEFAULT);
    }

    @Override
    public AttributeModifierManager getAttributeManager()
    {
        return super.getAttributeManager();
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    }

    @Override
    public void onDeath(DamageSource p_70645_1_)
    {
        world.setEntityState(this, (byte) 60);
        super.onDeath(p_70645_1_);
    }

    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 60)
        {
            world.addParticle(new SquidSoulParticleData(getColor()), this.getPosX(), this.getPosY(), this.getPosZ(), 0, 1, 0);
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player)
    {
        return 0;
    }

    @Override
    public void livingTick()
    {
        super.livingTick();

        BlockPos pos = getPositionUnderneath();

        if (world.getBlockState(pos).getBlock() == SplatcraftBlocks.inkwell && world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            InkColorTileEntity te = (InkColorTileEntity) world.getTileEntity(pos);
            if (te.getColor() != getColor())
            {
                setColor(te.getColor());
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state)
    {
        playSound(SoundEvents.BLOCK_HONEY_BLOCK_FALL, 0.15F, 1.0F);
    }

    @Override
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        if (nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        else setColor(ColorUtils.getRandomStarterColor());
    }

    @Override
    public void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
        nbt.putInt("Color", getColor());
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

    @Override
    public boolean canDespawn(double distanceToClosestPlayer)
    {
        return false;
    }
}
