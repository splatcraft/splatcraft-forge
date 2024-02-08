package net.splatcraft.forge.items.weapons;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.DualieWeaponSettings;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.DodgeRollPacket;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("UnusedReturnValue")
public class DualieItem extends WeaponBaseItem<DualieWeaponSettings>
{

    public static final ArrayList<DualieItem> dualies = Lists.newArrayList();

    public String settings;

    public static RegistryObject<DualieItem> create(DeferredRegister<Item> registry, String settings)
    {
        return registry.register(settings, () -> new DualieItem(settings));
    }

    public static RegistryObject<DualieItem> create(DeferredRegister<Item> registry, RegistryObject<DualieItem> parent, String name)
    {
        return registry.register(name, () -> new DualieItem(parent.get().settingsId.toString()));
    }

    public static RegistryObject<DualieItem> create(DeferredRegister<Item> registry, String settings, String name)
    {
        return registry.register(name, () -> new DualieItem(settings));
    }

    protected DualieItem(String settings) {
        super(settings);

        this.settings = settings;

        dualies.add(this);
    }

    @Override
    public Class<DualieWeaponSettings> getSettingsClass() {
        return DualieWeaponSettings.class;
    }

    private static float getInkForRoll(ItemStack stack)
    {
        return stack.getItem() instanceof DualieItem ? ((DualieItem) stack.getItem()).getSettings(stack).rollInkConsumption : 0;
    }

    public static int getRollCooldown(ItemStack stack, int maxRolls, int rollCount)
    {
        if (!(stack.getItem() instanceof DualieItem))
        {
            return 0;
        }

        DualieItem dualie = (DualieItem) stack.getItem();
        return rollCount >= maxRolls - 1 ? dualie.getSettings(stack).lastRollCooldown : dualie.getSettings(stack).rollCooldown;
    }

    public float performRoll(Player player, ItemStack mainDualie, ItemStack offhandDualie) {
        int rollCount = getRollString(mainDualie);
        float maxRolls = 0;
        ItemStack activeDualie;

        if (mainDualie.getItem() instanceof DualieItem) {
            maxRolls += ((DualieItem) mainDualie.getItem()).getSettings(mainDualie).rollCount;
        }
        if (offhandDualie.getItem() instanceof DualieItem)
        {
            maxRolls += ((DualieItem) offhandDualie.getItem()).getSettings(offhandDualie).rollCount;
        }


        if (rollCount >= maxRolls - 1)
        {
            activeDualie = getRollCooldown(mainDualie, (int) maxRolls, rollCount) >= getRollCooldown(offhandDualie, (int) maxRolls, rollCount) ? mainDualie : offhandDualie;
        } else
        {
            activeDualie = maxRolls % 2 == 1 && offhandDualie.getItem() instanceof DualieItem ? offhandDualie : mainDualie;
        }

        DualieWeaponSettings activeSettings = getSettings(activeDualie);

        if (reduceInk(player, this, getInkForRoll(activeDualie), activeSettings.rollInkRecoveryCooldown, !player.level.isClientSide))
        {
            PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(activeDualie, getRollCooldown(activeDualie, (int) maxRolls, rollCount), player.getInventory().selected, player.getUsedItemHand(), activeSettings.canMoveAsTurret, true, false, player.isOnGround()));
            if (!player.level.isClientSide) {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.dualieDodge, SoundSource.PLAYERS, 0.7F, ((player.level.random.nextFloat() - player.level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                InkExplosion.createInkExplosion(player.level, player, player.blockPosition(), 1.2f, 0, 0, false, ColorUtils.getInkColor(activeDualie), InkBlockUtils.getInkType(player), activeDualie);
            }
            setRollString(mainDualie, rollCount + 1);
            setRollCooldown(mainDualie, (int) (getRollCooldown(mainDualie, (int) maxRolls, (int) maxRolls) * 0.75f));
            return activeDualie.getItem() instanceof DualieItem ? activeSettings.rollSpeed : 0;
        }

        return 0;
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

    public ClampedItemPropertyFunction getIsLeft()
    {
        return (stack, level, entity, seed) ->
        {
            if (entity == null)
            {
                return 0;
            } else
            {
                entity.getMainArm();
            }
            boolean mainLeft = entity.getMainArm().equals(HumanoidArm.LEFT);
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
    public Component getName(@NotNull ItemStack stack)
    {
        return super.getName(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        CompoundTag nbt = stack.getOrCreateTag();

        nbt.putBoolean("IsPlural", false);
        if (entity instanceof LivingEntity)
        {
            InteractionHand hand = ((LivingEntity) entity).getItemInHand(InteractionHand.MAIN_HAND).equals(stack) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

            if (((LivingEntity) entity).getItemInHand(hand).equals(stack) && ((LivingEntity) entity).getItemInHand(InteractionHand.values()[(hand.ordinal() + 1) % InteractionHand.values().length]).getItem().equals(stack.getItem()))
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
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        ItemStack offhandDualie = ItemStack.EMPTY;
        if (entity.getUsedItemHand().equals(InteractionHand.MAIN_HAND) && entity.getMainHandItem().equals(stack) && entity.getOffhandItem().getItem() instanceof DualieItem)
        {
            offhandDualie = entity.getOffhandItem();
        }

        if (level.isClientSide)
        {
            if (entity == ClientUtils.getClientPlayer() && ClientUtils.canPerformRoll((Player) entity))
            {
                ItemStack activeDualie;
                int rollCount = getRollString(stack);
                float maxRolls = 0;
                if (stack.getItem() instanceof DualieItem)
                {
                    maxRolls += ((DualieItem) stack.getItem()).getSettings(stack).rollCount;
                }
                if (offhandDualie.getItem() instanceof DualieItem)
                {
                    maxRolls += ((DualieItem) offhandDualie.getItem()).getSettings(offhandDualie).rollCount;
                }
                if (rollCount >= maxRolls - 1)
                {
                    activeDualie = getRollCooldown(stack, (int) maxRolls, rollCount) >= getRollCooldown(offhandDualie, (int) maxRolls, rollCount) ? stack : offhandDualie;
                } else
                {
                    activeDualie = maxRolls % 2 == 1 && offhandDualie.getItem() instanceof DualieItem ? offhandDualie : stack;
                }

                if (enoughInk(entity, this, getInkForRoll(activeDualie), 0, false)) {
                    entity.moveRelative(performRoll((Player) entity, stack, offhandDualie), ClientUtils.getDodgeRollVector((Player) entity));
                    entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.05, entity.getDeltaMovement().z());
                }
                SplatcraftPacketHandler.sendToServer(new DodgeRollPacket((Player) entity, stack, offhandDualie));
            }
        } else
        {
            int rollCount = getRollString(stack);
            float maxRolls = 0;

            if (stack.getItem() instanceof DualieItem)
            {
                maxRolls += ((DualieItem) stack.getItem()).getSettings(stack).rollCount;
            }
            if (offhandDualie.getItem() instanceof DualieItem)
            {
                maxRolls += ((DualieItem) offhandDualie.getItem()).getSettings(offhandDualie).rollCount;
            }

            boolean hasCooldown = PlayerInfoCapability.get(entity).hasPlayerCooldown();
            boolean onRollCooldown = entity.isOnGround() && hasCooldown && rollCount >= Math.max(2, maxRolls);

            if (offhandDualie.getItem() instanceof DualieItem) {
                if (!entity.isOnGround() && !hasCooldown || entity.isOnGround())
                {
                    DualieWeaponSettings settings = ((DualieItem) offhandDualie.getItem()).getSettings(offhandDualie);
                    DualieWeaponSettings.FiringData firingData = onRollCooldown ? settings.turretData : settings.standardData;

                    ((DualieItem) offhandDualie.getItem()).fireDualie(level, entity, offhandDualie, timeLeft + firingData.firingSpeed /2, entity.isOnGround() && hasCooldown);
                }
            }
            if (!entity.isOnGround() && !hasCooldown || entity.isOnGround()) {
                fireDualie(level, entity, stack, timeLeft, onRollCooldown);
            }
        }
    }

    protected void fireDualie(Level level, LivingEntity entity, ItemStack stack, int timeLeft, boolean onRollCooldown)
    {
        DualieWeaponSettings settings = getSettings(stack);
        DualieWeaponSettings.FiringData firingData = onRollCooldown ? settings.turretData : settings.standardData;

        if (!level.isClientSide && (getUseDuration(stack) - timeLeft - 1) % (firingData.firingSpeed) == 0)
        {

            if (reduceInk(entity, this, firingData.inkConsumption, firingData.inkRecoveryCooldown, true))
            {
                for(int i = 0; i < firingData.projectileCount; i++)
                {
                    InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), firingData.projectileSize, settings);
                    proj.setDualieStats(firingData);
                    proj.isOnRollCooldown = onRollCooldown;
                    proj.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), firingData.pitchCompensation, firingData.projectileSpeed, entity.isOnGround() ? firingData.groundInaccuracy : firingData.airInaccuracy);
                    level.addFreshEntity(proj);
                }

                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.dualieShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose(ItemStack stack)
    {
        return PlayerPosingHandler.WeaponPose.DUAL_FIRE;
    }
}