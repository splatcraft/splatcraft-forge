package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.client.audio.RollerRollTickableSound;
import com.cibernet.splatcraft.client.particles.InkSplashParticleData;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.*;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

        addStat(new WeaponStat("range", (stack, world) -> (int) ((flingProjectileSpeed + swingProjectileSpeed) * 50)));
        addStat(new WeaponStat("ink_speed", (stack, world) -> (int) (dashMobility / 2f * 100)));
        addStat(new WeaponStat("handling", (stack, world) -> (int) ((20 - (flingTime + swingTime) / 2f) * 5)));

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

    public RollerItem(String name, RollerItem parent)
    {
        this(name, parent.rollSize, parent.rollConsumptionMin, parent.rollDamage, parent.mobility, parent.isBrush);
        setDashStats(parent.dashMobility, parent.rollConsumptionMax, parent.dashTime);
        setSwingStats(parent.swingMobility, parent.swingConsumption, parent.swingDamage, parent.swingProjectileSpeed, parent.swingTime, parent.flingConsumption, parent.flingDamage, parent.flingProjectileSpeed, parent.flingTime);
    }

    public static void applyRecoilKnockback(LivingEntity entity, double pow)
    {
        entity.setMotion(new Vector3d(Math.cos(Math.toRadians(entity.rotationYaw + 90)) * -pow, entity.getMotion().getY(), Math.sin(Math.toRadians(entity.rotationYaw + 90)) * -pow));
    }

    private float getSwingTime()
    {
        return swingTime;
    }

    private float getFlingTime()
    {
        return flingTime;
    }

    private float getSwingProjSpeed()
    {
        return swingProjectileSpeed;
    }

    private float getFlingProjSpeed()
    {
        return flingProjectileSpeed;
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
        return setSwingStats(swingMobility, swingConsumption, swingDamage, swingProjectileSpeed, swingTime, swingConsumption, swingDamage, swingProjectileSpeed * (isBrush ? 1 : 1.3f), swingTime);
    }

    public IItemPropertyGetter getUnfolded()
    {
        return (stack, world, entity) ->
        {
            if (entity instanceof PlayerEntity && PlayerCooldown.hasOverloadedPlayerCooldown((PlayerEntity) entity))
            {

                PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown((PlayerEntity) entity);
                if(cooldown.getTime() > (cooldown.isGrounded() ? -10 : 0))
                {
                    ItemStack cooldownStack = cooldown.getHand().equals(Hand.MAIN_HAND) ? ((PlayerEntity) entity).inventory.mainInventory.get(cooldown.getSlotIndex())
                            : entity.getHeldItemOffhand();
                    return stack.equals(cooldownStack) && (isBrush || cooldown.isGrounded()) ? 1 : 0;
                }
            }
            return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1 : 0;
        };
    }

    @Override
    public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        if (!(entity instanceof PlayerEntity))
            return;

        if (timeLeft >= getUseDuration(stack) - flingTime)
        {
            //if (getInkAmount(entity, stack) > inkConsumption){

            int startupTicks = entity.isOnGround() ? swingTime : flingTime;
            PlayerCooldown cooldown = new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.currentItem, entity.getActiveHand(), true, false, true, entity.isOnGround());
            cooldown.storedItem = this;
            PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, cooldown);
            //} else
            if (getInkAmount(entity, stack) < (entity.isOnGround() ? swingConsumption : flingConsumption))
                sendNoInkMessage(entity);
            else if (isBrush)
            {
                world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SplatcraftSounds.brushFling, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
                int total = rollSize * 2 + 1;
                for (int i = 0; i < total; i++)
                {
                    InkProjectileEntity proj = new InkProjectileEntity(world, entity, stack, InkBlockUtils.getInkType(entity), 1.6f,
                            entity.isOnGround() ? flingDamage : swingDamage);
                    proj.setProjectileType(InkProjectileEntity.Types.ROLLER);
                    proj.shoot(entity, entity.rotationPitch, entity.rotationYaw + (i - total / 2f) * 20, 0, entity.isOnGround() ? flingProjectileSpeed : swingProjectileSpeed, 0.05f);
                    proj.setPositionAndUpdate(proj.getPosX(), proj.getPosY() - entity.getEyeHeight() / 2f, proj.getPosZ());
                    world.addEntity(proj);
                }
                reduceInk(entity, entity.isOnGround() ? swingConsumption : flingConsumption);
            }
        } else
        {
            boolean hasInk = getInkAmount(entity, stack) > Math.min(rollConsumptionMax, rollConsumptionMin);
            boolean isMoving = Math.abs(entity.prevRotationYawHead - entity.rotationYawHead) > 0 || (world.isRemote ? Math.abs(entity.getMotion().getX()) > 0 || Math.abs(entity.getMotion().getZ()) > 0
                    : entity.getPositionVec().mul(1, 0, 1).distanceTo(WeaponHandler.getPlayerPrevPos((PlayerEntity) entity).mul(1, 0, 1)) > 0);

            double dxOff = 0;
            double dzOff = 0;
            for(int i = 1; i <= 2; i++)
            {
                dxOff = Math.cos(Math.toRadians(entity.rotationYaw + 90)) * i;
                dzOff = Math.sin(Math.toRadians(entity.rotationYaw + 90)) * i;

                    BlockPos pos = new BlockPos(entity.getPosX() + dxOff, entity.getPosY(), entity.getPosZ() + dzOff);
                    if (!InkBlockUtils.canInkPassthrough(world, pos))
                        break;
            }

            boolean doPush = false;
            if (isMoving)
            {
                for (int i = 0; i < rollSize; i++)
                {
                    double off = (double) i - (rollSize - 1) / 2d;
                    double xOff = Math.cos(Math.toRadians(entity.rotationYaw)) * off;
                    double zOff = Math.sin(Math.toRadians(entity.rotationYaw)) * off;

                    if (hasInk)
                    {
                        for (float yOff = 0; yOff >= -3; yOff--)
                        {
                            if(yOff == -3)
                            {
                                dxOff = Math.cos(Math.toRadians(entity.rotationYaw + 90));
                                dzOff = Math.sin(Math.toRadians(entity.rotationYaw + 90));
                            }

                            BlockPos pos = new BlockPos(entity.getPosX() + xOff + dxOff, entity.getPosY() + yOff, entity.getPosZ() + zOff + dzOff);
                            if (!InkBlockUtils.canInkPassthrough(world, pos))
                            {
                                InkBlockUtils.inkBlock(world, pos, ColorUtils.getInkColor(stack), rollDamage, InkBlockUtils.getInkType(entity));
                                double blockHeight = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty() ? 0 : world.getBlockState(pos).getCollisionShape(world, pos).getBoundingBox().maxY;

                                world.addParticle(new InkSplashParticleData(ColorUtils.getInkColor(stack), 1), entity.getPosX() + xOff + dxOff, pos.getY() + blockHeight + 0.1, entity.getPosZ() + zOff + dzOff, 0, 0, 0);

                                if (i > 0)
                                {
                                    double xhOff = dxOff + Math.cos(Math.toRadians(entity.rotationYaw)) * (off - 0.5);
                                    double zhOff = dzOff + Math.sin(Math.toRadians(entity.rotationYaw)) * (off - 0.5);
                                    world.addParticle(new InkSplashParticleData(ColorUtils.getInkColor(stack), 1), entity.getPosX() + xhOff, pos.getY() + blockHeight + 0.1, entity.getPosZ() + zhOff, 0, 0, 0);
                                }

                                break;
                            }
                        }
                    }

                    BlockPos attackPos = new BlockPos(entity.getPosX() + xOff + dxOff, entity.getPosY() - 1, entity.getPosZ() + zOff + dzOff);
                    for (LivingEntity target : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(attackPos, attackPos.add(1, 2, 1)), EntityPredicates.NOT_SPECTATING.and((e) ->
                    {
                        if(e instanceof LivingEntity && ColorUtils.getEntityColor((LivingEntity) e) != -1)
                            return InkDamageUtils.canDamageColor(world, ColorUtils.getEntityColor((LivingEntity) e), ColorUtils.getInkColor(stack));
                        return true;
                    })))
                    {
                        if(target.equals(entity))
                            continue;
                        InkDamageUtils.doRollDamage(world, target, rollDamage * (hasInk ? 1 : 0.4f), ColorUtils.getInkColor(stack), entity, stack, false, InkBlockUtils.getInkType(entity));
                        if (!InkDamageUtils.isSplatted(world, target))
                            doPush = true;
                    }
                }
                if (hasInk)
                    reduceInk(entity, Math.min(1, (float) (getUseDuration(stack) - timeLeft) / (float) dashTime) * (rollConsumptionMax - rollConsumptionMin) + rollConsumptionMin);
                else if (timeLeft % 4 == 0)
                    sendNoInkMessage(entity, null);
            }
            if (doPush)
                applyRecoilKnockback(entity, 0.8);
        }
    }

    @Override
    public void onPlayerCooldownEnd(World world, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown)
    {
        boolean airborne = !cooldown.isGrounded();

        if (world.isRemote)
        {
            playRollSound(player);
        }

        if (getInkAmount(player, stack) >= (player.isOnGround() ? swingConsumption : flingConsumption) && !isBrush)
        {
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SplatcraftSounds.rollerFling, SoundCategory.PLAYERS, 0.8F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
            for (int i = 0; i < rollSize; i++)
            {

                InkProjectileEntity proj = new InkProjectileEntity(world, player, stack, InkBlockUtils.getInkType(player), 1.6f, airborne ? flingDamage : swingDamage);
                proj.shoot(player, player.rotationPitch, player.rotationYaw, airborne ? 0.0f : -67.5f, airborne ? flingProjectileSpeed : swingProjectileSpeed, 0.05f);
                proj.setRollerSwingStats(airborne);
                if (airborne)
                {
                    double off = (double) i - (rollSize - 1) / 2d;
                    double yOff = Math.sin(Math.toRadians(player.rotationPitch+90));
                    double y2Off = Math.cos(Math.toRadians(player.rotationPitch+90));
                    double xOff = Math.cos(Math.toRadians(player.rotationYaw+90)) * off * y2Off;
                    double zOff = Math.sin(Math.toRadians(player.rotationYaw+90)) * off * y2Off;
                    proj.setPositionAndUpdate(proj.getPosX() + xOff, proj.getPosY() + yOff*off, proj.getPosZ() + zOff);
                }
                else
                {
                    double off = (double) i - (rollSize - 1) / 2d;
                    double xOff = Math.cos(Math.toRadians(player.rotationYaw)) * off;
                    double zOff = Math.sin(Math.toRadians(player.rotationYaw)) * off;
                    proj.setPositionAndUpdate(proj.getPosX() + xOff, proj.getPosY() - player.getEyeHeight() / 2f, proj.getPosZ() + zOff);
                }
                world.addEntity(proj);
            }
            reduceInk(player, airborne ? swingConsumption : flingConsumption);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void playRollSound(PlayerEntity player)
    {
        Minecraft.getInstance().getSoundHandler().play(new RollerRollTickableSound(player, isBrush));
    }

    @Override
    public boolean hasSpeedModifier(LivingEntity entity, ItemStack stack)
    {
        if (entity instanceof PlayerEntity && PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity) || !entity.getActiveItemStack().equals(stack))
            return false;
        return super.hasSpeedModifier(entity, stack);
    }

    @Override
    public AttributeModifier getSpeedModifier(LivingEntity entity, ItemStack stack)
    {
        double appliedMobility;
        int useTime = entity.getItemInUseCount() - entity.getItemInUseCount();

        if (!(getInkAmount(entity, entity.getActiveItemStack()) > Math.min(rollConsumptionMax, rollConsumptionMin)))
            appliedMobility = 0.7;
        else if (entity instanceof PlayerEntity && (PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity)))
            appliedMobility = swingMobility;
        else appliedMobility = Math.min(1, (float) useTime / (float) dashTime) * (dashMobility - mobility) + mobility;

        return new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Roller Mobility", appliedMobility - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return isBrush ? PlayerPosingHandler.WeaponPose.BRUSH : PlayerPosingHandler.WeaponPose.ROLL;
    }
}
