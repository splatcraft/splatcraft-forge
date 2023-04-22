package net.splatcraft.forge.items.weapons;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import org.jetbrains.annotations.NotNull;

public class CurlingSubWeaponItem extends SubWeaponItem {
    public static final float MAX_INK_RECOVERY_COOLDOWN = 70f / 3f;
    public static final float INK_RECOVERY_COOLDOWN_MULTIPLIER = 40f / 3f;

    public CurlingSubWeaponItem(EntityType<? extends AbstractSubWeaponEntity> entityType, WeaponSettings settings, int maxUseTime, SubWeaponAction useTick) {
        super(entityType, settings, maxUseTime, useTick);
    }

    @Override
    protected void throwSub(@NotNull ItemStack stack, @NotNull World level, LivingEntity entity) {
        entity.swing(entity.getOffhandItem().equals(stack) ? Hand.OFF_HAND : Hand.MAIN_HAND, false);

        if (!level.isClientSide()) {
            AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType, level, entity, stack.copy());

            stack.getOrCreateTag().remove("EntityData");

            proj.setItem(stack);
            proj.shoot(entity, entity.xRot, entity.yRot, throwAngle, throwVelocity, 0);
            proj.setDeltaMovement(proj.getDeltaMovement().add(entity.getDeltaMovement().multiply(1, 0, 1)));
            level.addFreshEntity(proj);
        }
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        if (SubWeaponItem.singleUse(stack)) {
            if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative())
                stack.shrink(1);
        } else {
            int cookTime = stack.getTag().getCompound("EntityData").getInt("CookTime");
            reduceInk(entity, this, settings.inkConsumption, (int) (MAX_INK_RECOVERY_COOLDOWN - cookTime / CurlingBombEntity.MAX_COOK_TIME * INK_RECOVERY_COOLDOWN_MULTIPLIER), false);
        }
    }
}
