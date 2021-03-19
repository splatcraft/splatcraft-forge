package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.InkDamageUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class RollerItem extends WeaponBaseItem
{
    public static final ArrayList<RollerItem> rollers = Lists.newArrayList();

    public float rollConsumptionMin;
    public float rollConsumptionMax;
    public int dashTime = 1;
    public float rollDamage;
    public int rollSize;

    public double mobility;
    public double dashMobility;
    public double swingMobility;

    public float swingConsumption;
    public float swingDamage;
    public float swingProjectileSpeed;
    public int swingTime;

    public float flingConsumption;
    public float flingDamage;
    public float flingProjectileSpeed;
    public int flingTime;

    public boolean isBrush;

    public RollerItem(String name, int rollSize, float rollConsumption, float rollDamage, double mobility, boolean isBrush)
    {
        super();

        setRegistryName(name);
        rollers.add(this);

        this.rollSize = rollSize;
        this.rollConsumptionMin = rollConsumption;
        this.rollConsumptionMax = rollConsumption;
        this.rollDamage = rollDamage;
        this.mobility = mobility;
        this.dashMobility = mobility;
        this.swingMobility = mobility;
        this.isBrush = isBrush;
    }

    public RollerItem setDashStats(double dashMobility, float rollConsumptionDash, int dashTime)
    {
        this.dashMobility = dashMobility;
        this.rollConsumptionMax = rollConsumptionDash;
        this.dashTime = dashTime;
        return this;
    }

    public RollerItem setSwingStats(double swingMobility, float swingConsumption, float swingDamage, float swingProjectileSpeed, int swingTime, float flingConsumption, float flingDamage, float flingProjectileSpeed, int flingTime)
    {
        this.swingMobility = swingMobility;
        this.swingConsumption = swingConsumption;
        this.swingDamage = swingDamage;
        this.swingProjectileSpeed = swingProjectileSpeed;
        this.swingTime = swingTime;
        this.flingConsumption = flingConsumption;
        this.flingDamage = flingDamage;
        this.flingProjectileSpeed = flingProjectileSpeed;
        this.flingTime = flingTime;
        return this;
    }

    public RollerItem setSwingStats(double swingMobility, float swingConsumption, float swingDamage, float swingProjectileSpeed, int swingTime)
    {
        return setSwingStats(swingMobility, swingConsumption, swingDamage, swingProjectileSpeed, swingTime, swingConsumption, swingDamage, swingProjectileSpeed, swingTime);
    }

    public RollerItem(String name, RollerItem parent)
    {
        this(name, parent.rollSize, parent.rollConsumptionMin, parent.rollDamage, parent.mobility, parent.isBrush);
        setDashStats(parent.dashMobility, parent.rollConsumptionMax, parent.dashTime);
        setSwingStats(parent.swingMobility, parent.swingConsumption, parent.swingDamage, parent.swingProjectileSpeed, parent.swingTime, parent.flingConsumption, parent.flingDamage, parent.flingProjectileSpeed, parent.flingTime);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        setAttackCooldown(stack, Math.max(0, getAttackCooldown(stack)-1));
    }

    public IItemPropertyGetter getUnfolded()
    {
        return (stack, world, entity) ->
        {
            if(!isBrush && entity instanceof PlayerEntity && PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity))
            {
                PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown((PlayerEntity) entity);
                return cooldown.isGrounded() ? 1 : 0;
            }
            return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1 : 0;
        };
    }

    @Override
    public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        if(!(entity instanceof PlayerEntity))
            return;

        if(timeLeft >= getUseDuration(stack) - (flingTime))
        {
            //if (getInkAmount(entity, stack) > inkConsumption){

                int startupTicks = entity.isOnGround() ? swingTime : flingTime;
                if (entity instanceof PlayerEntity)
                {
                    PlayerCooldown cooldown = new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.currentItem, true, false, true, entity.isOnGround());
                    cooldown.storedItem = this;
                    PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, cooldown);
                }
            //} else
            if (getInkAmount(entity, stack) < (entity.isOnGround() ? swingConsumption : flingConsumption)) sendNoInkMessage(entity);
            else if(isBrush)
            {
                int total = rollSize*2+1;
                for (int i = 0; i < total; i++)
                {
                    InkProjectileEntity proj = new InkProjectileEntity(world, entity, stack, InkBlockUtils.getInkType(entity), 1.6f,
                            entity.isOnGround() ? flingDamage : swingDamage);
                    proj.shoot(entity, entity.rotationPitch, entity.rotationYaw + (i-total/2f)*20, 0, entity.isOnGround() ? flingProjectileSpeed : swingProjectileSpeed, 0.05f);
                    proj.setPositionAndUpdate(proj.getPosX(), proj.getPosY() - entity.getEyeHeight()/2f, proj.getPosZ());
                    world.addEntity(proj);
                    reduceInk(entity, (entity.isOnGround() ? swingConsumption : flingConsumption));
                }
            }
        }
        else
        {
            //TODO roll
            double dxOff = Math.cos(Math.toRadians(entity.rotationYaw+90))*2;
            double dzOff = Math.sin(Math.toRadians(entity.rotationYaw+90))*2;
            boolean hasInk = (getInkAmount(entity, stack) > Math.min(rollConsumptionMax, rollConsumptionMin));
            boolean isMoving = world.isRemote ? Math.abs(entity.getMotion().getX()) > 0 || Math.abs(entity.getMotion().getZ()) > 0
                    : entity.getPositionVec().mul(1, 0, 1).distanceTo(WeaponHandler.getPlayerPrevPos((PlayerEntity) entity).mul(1, 0, 1)) > 0;

            boolean doPush = false;
            for(int i = 0; i < rollSize; i++)
            {
                double off = (double) i - (rollSize-1)/2d;
                double xOff = Math.cos(Math.toRadians(entity.rotationYaw))*off;
                double zOff = Math.sin(Math.toRadians(entity.rotationYaw))*off;

                if(hasInk)
                for(int yOff = 0; yOff >= -1; yOff--)
                {
                    BlockPos pos = new BlockPos( entity.getPosX() + xOff+dxOff, entity.getPosY() + yOff, entity.getPosZ() + zOff+dzOff);
                    if(!InkBlockUtils.canInkPassthrough(world, pos))
                    {
                        InkBlockUtils.inkBlock(world, pos, ColorUtils.getInkColor(stack), rollDamage, InkBlockUtils.getInkType(entity));
                        break;
                    }
                }

                if(isMoving && getAttackCooldown(stack) <= 0)
                {
                    BlockPos attackPos = new BlockPos(entity.getPosX() + xOff+dxOff, entity.getPosY()-1, entity.getPosZ() + zOff+dzOff);
                    for(LivingEntity target : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(attackPos, attackPos.add(1, 2, 1))))
                    {
                        InkDamageUtils.doRollDamage(world, target, rollDamage * (hasInk ? 1 : 0.4f), ColorUtils.getInkColor(stack), entity, stack, false, InkBlockUtils.getInkType(entity));
                        setAttackCooldown(stack, 10);
                        if(!InkDamageUtils.isSplatted(world, target))
                            doPush = true;
                    }
                }
            }
            if(doPush)
                applyRecoilKnockback(entity, 0.8);//SplatcraftPacketHandler.sendToPlayer(new RollerRecoilPacket(), (ServerPlayerEntity) entity);

            //if(isMoving)
            {
                if(hasInk) reduceInk(entity, (Math.min(1, (float)(getUseDuration(stack)-timeLeft)/(float)dashTime)*(rollConsumptionMax-rollConsumptionMin)) + rollConsumptionMin);
                else if(timeLeft % 4 == 0) sendNoInkMessage(entity);
            }

        }
    }

    public static void applyRecoilKnockback(LivingEntity entity, double pow)
    {
        entity.setMotion(entity.getMotion().add(Math.cos(Math.toRadians(entity.rotationYaw+90))*-pow, 0.1, Math.sin(Math.toRadians(entity.rotationYaw+90))*-pow));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        ItemStack stackA = oldStack.copy();
        if(stackA.getOrCreateTag().contains("AttackCooldown"))
            stackA.getTag().remove("AttackCooldown");
        ItemStack stackB = newStack.copy();
        if(stackB.getOrCreateTag().contains("AttackCooldown"))
            stackB.getTag().remove("AttackCooldown");

        return super.shouldCauseReequipAnimation(stackA, stackB, slotChanged);
    }

    public int getAttackCooldown(ItemStack stack)
    {
        return stack.getOrCreateTag().getInt("AttackCooldown");
    }

    public ItemStack setAttackCooldown(ItemStack stack, int v)
    {
        stack.getOrCreateTag().putInt("AttackCooldown", v);
        return stack;
    }

    @Override
    public void onPlayerCooldownEnd(World world, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown) {
        boolean airborne = !cooldown.isGrounded();

        if (!isBrush)
        for(int i = 0; i < rollSize; i++)
        {

            InkProjectileEntity proj = new InkProjectileEntity(world, player, stack, InkBlockUtils.getInkType(player), 1.6f, airborne ? flingDamage : swingDamage);proj.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, airborne ? flingProjectileSpeed : swingProjectileSpeed, 0.05f);
            if(airborne)
                proj.setPositionAndUpdate(proj.getPosX(), proj.getPosY()+(double) i - i/2d, proj.getPosZ());
            else
            {
                double off = (double) i - (rollSize-1)/2d;
                double xOff = Math.cos(Math.toRadians(player.rotationYaw))*off;
                double zOff = Math.sin(Math.toRadians(player.rotationYaw))*off;
                proj.setPositionAndUpdate(proj.getPosX() + xOff, proj.getPosY() - player.getEyeHeight()/2f, proj.getPosZ() + zOff);
            }
            proj.shoot(player, 0, player.rotationYaw, airborne ? 0.0f : -67.5f, airborne ? flingProjectileSpeed : swingProjectileSpeed, 0);
            world.addEntity(proj);
            reduceInk(player, (airborne ? swingConsumption : flingConsumption));

        }
    }

    @Override
    public boolean hasSpeedModifier(LivingEntity entity, int useTime)
    {
        if(entity instanceof PlayerEntity && PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity))
            return false;
        return super.hasSpeedModifier(entity, useTime);
    }

    @Override
    public AttributeModifier getSpeedModifier(LivingEntity entity, int timeLeft)
    {
        double appliedMobility;
        int useTime = USE_DURATION-timeLeft;

        if(!((getInkAmount(entity, entity.getActiveItemStack()) > Math.min(rollConsumptionMax, rollConsumptionMin))))
            appliedMobility = 0.7;
        else if(entity instanceof PlayerEntity && PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity))
            appliedMobility = swingMobility;
        else
        {
            appliedMobility = (Math.min(1, (float)(useTime)/(float)dashTime)*(dashMobility-mobility)) + mobility;
        }

        return new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Roller Mobility", appliedMobility-1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose() {
        return isBrush ? PlayerPosingHandler.WeaponPose.BRUSH : PlayerPosingHandler.WeaponPose.ROLL;
    }
}
