package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.network.DodgeRollPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.*;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;

public class DualieItem extends WeaponBaseItem
{

    public static final ArrayList<DualieItem> dualies = Lists.newArrayList();

    public float projectileSize;
    public float inaccuracy;
    public float projectileSpeed;
    public int firingSpeed;
    public float damage;
    public float inkConsumption;

    public int maxRolls;
    public float rollSpeed;
    public float rollConsumption;

    public int rollCooldown;
    public int finalRollCooldown;

    public int offhandFiringOffset;

    public boolean canRollFire;

    public DualieItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, float inkConsumption, int rolls, float rollSpeed, float rollConsuption, int rollCooldown, int finalRollCooldown, boolean canRollFire)
    {
        super();

        setRegistryName(name);
        this.inaccuracy = inaccuracy;
        this.projectileSize = projectileSize;
        this.projectileSpeed = projectileSpeed;
        this.firingSpeed = firingSpeed;
        this.damage = damage;
        this.inkConsumption = inkConsumption;

        this.maxRolls = rolls;
        this.rollSpeed = rollSpeed;
        this.rollConsumption = rollConsuption;

        offhandFiringOffset = firingSpeed / 2;

        this.rollCooldown = rollCooldown;
        this.finalRollCooldown = finalRollCooldown;
        this.canRollFire = canRollFire;

        addStat(new WeaponStat("range", (stack, world) -> (int) (projectileSpeed / 1.2f * 100)));
        addStat(new WeaponStat("damage", (stack, world) -> (int) (damage / 20 * 100)));
        addStat(new WeaponStat("mobility", (stack, world) -> (int) (rollSpeed * 100)));

        dualies.add(this);
    }

    public DualieItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, float inkConsumption, int rolls, float rollSpeed, float rollConsuption, int rollCooldown, int finalRollCooldown)
    {
        this(name, projectileSize, projectileSpeed, inaccuracy, firingSpeed, damage, inkConsumption, rolls, rollSpeed, rollConsuption, rollCooldown, finalRollCooldown, false);
    }

    public DualieItem(String name, DualieItem parent)
    {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.firingSpeed, parent.damage, parent.inkConsumption, parent.maxRolls, parent.rollSpeed, parent.rollConsumption, parent.rollCooldown, parent.finalRollCooldown, parent.canRollFire);
    }

    public static float performRoll(PlayerEntity player, ItemStack mainDualie, ItemStack offhandDualie)
    {
        int rollCount = getRollString(mainDualie);
        int maxRolls = 0;
        ItemStack activeDualie;

        if (mainDualie.getItem() instanceof DualieItem)
        {
            maxRolls += ((DualieItem) mainDualie.getItem()).maxRolls;
        }
        if (offhandDualie.getItem() instanceof DualieItem)
        {
            maxRolls += ((DualieItem) offhandDualie.getItem()).maxRolls;
        }


        if (rollCount >= maxRolls - 1)
        {
            activeDualie = getRollCooldown(mainDualie, maxRolls, rollCount) >= getRollCooldown(offhandDualie, maxRolls, rollCount) ? mainDualie : offhandDualie;
        } else
        {
            activeDualie = maxRolls % 2 == 1 && offhandDualie.getItem() instanceof DualieItem ? offhandDualie : mainDualie;
        }

        if (getInkAmount(player, activeDualie) >= getInkForRoll(activeDualie))
        {
            PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(getRollCooldown(activeDualie, maxRolls, rollCount), player.inventory.currentItem, false, true, false, player.isOnGround()));
            if (!player.world.isRemote)
            {
                player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SplatcraftSounds.dualieDodge, SoundCategory.PLAYERS, 0.7F, ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
                InkExplosion.createInkExplosion(player.world, player, new DamageSource("ink"), player.getPosition(), 1.2f, 0, 0, false, ColorUtils.getInkColor(activeDualie), InkBlockUtils.getInkType(player), activeDualie);
            }
            reduceInk(player, getInkForRoll(activeDualie));
            setRollString(mainDualie, rollCount + 1);
            setRollCooldown(mainDualie, (int) (getRollCooldown(mainDualie, maxRolls, maxRolls) * 0.75f));
            return activeDualie.getItem() instanceof DualieItem ? ((DualieItem) activeDualie.getItem()).rollSpeed : 0;
        } else if (!player.world.isRemote)
        {
            sendNoInkMessage(player);
        }

        return 0;
    }

    private static float getInkForRoll(ItemStack stack)
    {
        return stack.getItem() instanceof DualieItem ? ((DualieItem) stack.getItem()).rollConsumption : 0;
    }

    public static int getRollCooldown(ItemStack stack, int maxRolls, int rollCount)
    {
        if (!(stack.getItem() instanceof DualieItem))
        {
            return 0;
        }

        DualieItem dualie = (DualieItem) stack.getItem();
        return rollCount >= maxRolls - 1 ? dualie.finalRollCooldown : dualie.rollCooldown;
    }

    public static boolean canRollFire(ItemStack stack)
    {
        return stack.getItem() instanceof DualieItem && ((DualieItem) stack.getItem()).canRollFire;
    }

    public static int getRollString(ItemStack stack)
    {
        if (!stack.hasTag() || !Objects.requireNonNull(stack.getTag()).contains("RollString"))
        {
            return 0;
        }
        return stack.getTag().getInt("RollString");
    }

    public static ItemStack setRollString(ItemStack stack, int rollString)
    {
        stack.getOrCreateTag().putInt("RollString", rollString);
        return stack;
    }

    public static int getRollCooldown(ItemStack stack)
    {
        if (!stack.hasTag() || !stack.getTag().contains("RollCooldown"))
        {
            return 0;
        }
        return stack.getTag().getInt("RollCooldown");
    }

    public static ItemStack setRollCooldown(ItemStack stack, int rollCooldown)
    {
        stack.getOrCreateTag().putInt("RollCooldown", rollCooldown);
        return stack;
    }

    public IItemPropertyGetter getIsLeft()
    {
        return (stack, world, entity) ->
        {
            if (entity == null)
            {
                return 0;
            } else
            {
                entity.getPrimaryHand();
            }
            boolean mainLeft = entity.getPrimaryHand().equals(HandSide.LEFT);
            return mainLeft && entity.getHeldItemMainhand().equals(stack) || !mainLeft && entity.getHeldItemOffhand().equals(stack) ? 1 : 0;
        };
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if (stack.getOrCreateTag().getBoolean("IsPlural"))
        {
            return getDefaultTranslationKey() + ".plural";
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        return super.getDisplayName(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);

        CompoundNBT nbt = stack.getOrCreateTag();

        nbt.putBoolean("IsPlural", false);
        if (entity instanceof LivingEntity)
        {
            Hand hand = ((LivingEntity) entity).getHeldItem(Hand.MAIN_HAND).equals(stack) ? Hand.MAIN_HAND : Hand.OFF_HAND;

            if (((LivingEntity) entity).getHeldItem(hand).equals(stack) && ((LivingEntity) entity).getHeldItem(Hand.values()[(hand.ordinal() + 1) % Hand.values().length]).getItem().equals(stack.getItem()))
            {
                nbt.putBoolean("IsPlural", true);
            }
        }

        int rollCooldown = getRollCooldown(stack);
        if (rollCooldown > 0)
        {
            setRollCooldown(stack, rollCooldown - 1);
        } else if (getRollString(stack) > 0)
        {
            setRollString(stack, 0);
        }
    }

    @Override
    public void weaponUseTick(World world, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        ItemStack offhandDualie = ItemStack.EMPTY;
        if (entity.getActiveHand().equals(Hand.MAIN_HAND) && entity.getHeldItemMainhand().equals(stack) && entity.getHeldItemOffhand().getItem() instanceof DualieItem)
        {
            offhandDualie = entity.getHeldItemOffhand();
        }

        if (world.isRemote)
        {
            if (entity == ClientUtils.getClientPlayer() && ClientUtils.canPerformRoll((PlayerEntity) entity))
            {
                ItemStack activeDualie;
                int rollCount = getRollString(stack);
                int maxRolls = 0;
                if (stack.getItem() instanceof DualieItem)
                {
                    maxRolls += ((DualieItem) stack.getItem()).maxRolls;
                }
                if (offhandDualie.getItem() instanceof DualieItem)
                {
                    maxRolls += ((DualieItem) offhandDualie.getItem()).maxRolls;
                }
                if (rollCount >= maxRolls - 1)
                {
                    activeDualie = getRollCooldown(stack, maxRolls, rollCount) >= getRollCooldown(offhandDualie, maxRolls, rollCount) ? stack : offhandDualie;
                } else
                {
                    activeDualie = maxRolls % 2 == 1 && offhandDualie.getItem() instanceof DualieItem ? offhandDualie : stack;
                }

                if (getInkAmount(entity, activeDualie) >= getInkForRoll(activeDualie))
                {
                    entity.moveRelative(performRoll((PlayerEntity) entity, stack, offhandDualie), ClientUtils.getDodgeRollVector((PlayerEntity) entity));
                    entity.setMotion(entity.getMotion().getX(), 0.05, entity.getMotion().getZ());
                }
                SplatcraftPacketHandler.sendToServer(new DodgeRollPacket((PlayerEntity) entity, stack, offhandDualie));
            }
        } else
        {
            int rollCount = getRollString(stack);
            int maxRolls = 0;

            if (stack.getItem() instanceof DualieItem)
            {
                maxRolls += ((DualieItem) stack.getItem()).maxRolls;
            }
            if (offhandDualie.getItem() instanceof DualieItem)
            {
                maxRolls += ((DualieItem) offhandDualie.getItem()).maxRolls;
            }

            boolean rollFire = canRollFire(stack);
            boolean hasCooldown = PlayerInfoCapability.get(entity).hasPlayerCooldown();
            boolean onRollCooldown = entity.isOnGround() && hasCooldown && rollCount >= Math.max(2, maxRolls);

            if (offhandDualie.getItem() instanceof DualieItem)
            {
                rollFire = canRollFire(offhandDualie);
                if (!entity.isOnGround() && (rollFire || !hasCooldown) || entity.isOnGround())
                {
                    ((DualieItem) offhandDualie.getItem()).fireDualie(world, entity, offhandDualie, timeLeft + ((DualieItem) offhandDualie.getItem()).offhandFiringOffset, entity.isOnGround() && hasCooldown);
                }
            }
            if (!entity.isOnGround() && (rollFire || !hasCooldown) || entity.isOnGround())
            {
                fireDualie(world, entity, stack, timeLeft, onRollCooldown);
            }
        }
    }

    protected void fireDualie(World world, LivingEntity entity, ItemStack stack, int timeLeft, boolean onRollCooldown)
    {
        if (!world.isRemote && (getUseDuration(stack) - timeLeft - 1) % (onRollCooldown ? 2 : firingSpeed) == 0)
        {
            if (getInkAmount(entity, stack) >= inkConsumption)
            {
                InkProjectileEntity proj = new InkProjectileEntity(world, entity, stack, InkBlockUtils.getInkType(entity), projectileSize, damage).setShooterTrail();
                proj.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0f, projectileSpeed, entity instanceof PlayerEntity && PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity) ? 0 : inaccuracy);
                world.addEntity(proj);
                world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SplatcraftSounds.dualieShot, SoundCategory.PLAYERS, 0.7F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
                reduceInk(entity, inkConsumption);
            } else
            {
                sendNoInkMessage(entity);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.DUAL_FIRE;
    }
}
