package net.splatcraft.forge.items.weapons;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.DodgeRollPacket;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkExplosion;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
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
    public float rollDamage;

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
        this.rollDamage = damage;
        this.inkConsumption = inkConsumption;

        this.maxRolls = rolls;
        this.rollSpeed = rollSpeed;
        this.rollConsumption = rollConsuption;

        offhandFiringOffset = firingSpeed / 2;

        this.rollCooldown = rollCooldown;
        this.finalRollCooldown = finalRollCooldown;
        this.canRollFire = canRollFire;

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (projectileSpeed / 1.2f * 100)));
        addStat(new WeaponTooltip("damage", (stack, level) -> (int) (damage / 20 * 100)));
        addStat(new WeaponTooltip("mobility", (stack, level) -> (int) (rollSpeed * 100)));

        dualies.add(this);
    }

    public DualieItem(String name, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, float inkConsumption, int rolls, float rollSpeed, float rollConsuption, int rollCooldown, int finalRollCooldown)
    {
        this(name, projectileSize, projectileSpeed, inaccuracy, firingSpeed, damage, inkConsumption, rolls, rollSpeed, rollConsuption, rollCooldown, finalRollCooldown, false);
    }

    public DualieItem(String name, DualieItem parent)
    {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.inaccuracy, parent.firingSpeed, parent.damage, parent.inkConsumption, parent.maxRolls, parent.rollSpeed, parent.rollConsumption, parent.rollCooldown, parent.finalRollCooldown, parent.canRollFire);
        parent.rollDamage = rollDamage;
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

        if (reduceInk(player, getInkForRoll(activeDualie), !player.level.isClientSide))
        {
            PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(activeDualie, getRollCooldown(activeDualie, maxRolls, rollCount), player.inventory.selected, player.getUsedItemHand(), false, true, false, player.isOnGround()));
            if (!player.level.isClientSide)
            {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.dualieDodge, SoundCategory.PLAYERS, 0.7F, ((player.level.random.nextFloat() - player.level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                InkExplosion.createInkExplosion(player.level, player, player.blockPosition(), 1.2f, 0, 0, false, ColorUtils.getInkColor(activeDualie), InkBlockUtils.getInkType(player), activeDualie);
            }
            setRollString(mainDualie, rollCount + 1);
            setRollCooldown(mainDualie, (int) (getRollCooldown(mainDualie, maxRolls, maxRolls) * 0.75f));
            return activeDualie.getItem() instanceof DualieItem ? ((DualieItem) activeDualie.getItem()).rollSpeed : 0;
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
        //noinspection ConstantConditions
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
        return (stack, level, entity) ->
        {
            if (entity == null)
            {
                return 0;
            } else
            {
                entity.getMainArm();
            }
            boolean mainLeft = entity.getMainArm().equals(HandSide.LEFT);
            return mainLeft && entity.getMainHandItem().equals(stack) || !mainLeft && entity.getOffhandItem().equals(stack) ? 1 : 0;
        };
    }



    @Override
    public @NotNull String getDescriptionId(ItemStack stack)
    {
        if (stack.getOrCreateTag().getBoolean("IsPlural"))
        {
            return getDescriptionId() + ".plural";
        }
        return super.getDescriptionId(stack);
    }

    @Override
    public @NotNull ITextComponent getName(@NotNull ItemStack stack)
    {
        return super.getName(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World level, @NotNull Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        CompoundNBT nbt = stack.getOrCreateTag();

        nbt.putBoolean("IsPlural", false);
        if (entity instanceof LivingEntity)
        {
            Hand hand = ((LivingEntity) entity).getItemInHand(Hand.MAIN_HAND).equals(stack) ? Hand.MAIN_HAND : Hand.OFF_HAND;

            if (((LivingEntity) entity).getItemInHand(hand).equals(stack) && ((LivingEntity) entity).getItemInHand(Hand.values()[(hand.ordinal() + 1) % Hand.values().length]).getItem().equals(stack.getItem()))
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
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        ItemStack offhandDualie = ItemStack.EMPTY;
        if (entity.getUsedItemHand().equals(Hand.MAIN_HAND) && entity.getMainHandItem().equals(stack) && entity.getOffhandItem().getItem() instanceof DualieItem)
        {
            offhandDualie = entity.getOffhandItem();
        }

        if (level.isClientSide)
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

                if (enoughInk(entity, getInkForRoll(activeDualie), false))
                {
                    entity.moveRelative(performRoll((PlayerEntity) entity, stack, offhandDualie), ClientUtils.getDodgeRollVector((PlayerEntity) entity));
                    entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.05, entity.getDeltaMovement().z());
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
                    ((DualieItem) offhandDualie.getItem()).fireDualie(level, entity, offhandDualie, timeLeft + ((DualieItem) offhandDualie.getItem()).offhandFiringOffset, entity.isOnGround() && hasCooldown);
                }
            }
            if (!entity.isOnGround() && (rollFire || !hasCooldown) || entity.isOnGround())
            {
                fireDualie(level, entity, stack, timeLeft, onRollCooldown);
            }
        }
    }

    protected void fireDualie(World level, LivingEntity entity, ItemStack stack, int timeLeft, boolean onRollCooldown)
    {
        if (!level.isClientSide && (getUseDuration(stack) - timeLeft - 1) % (onRollCooldown ? 2 : firingSpeed) == 0)
        {
            if (reduceInk(entity, inkConsumption, true))
            {
                InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), projectileSize, onRollCooldown ? rollDamage : damage).setShooterTrail();
                proj.shootFromRotation(entity, entity.xRot, entity.yRot, 0.0f, projectileSpeed, entity instanceof PlayerEntity && PlayerCooldown.hasPlayerCooldown((PlayerEntity) entity) ? 0 : inaccuracy);
                level.addFreshEntity(proj);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.dualieShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.DUAL_FIRE;
    }
}
