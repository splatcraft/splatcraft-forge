package net.splatcraft.forge.items.weapons;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.items.weapons.settings.SubWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import org.jetbrains.annotations.NotNull;

public class CurlingSubWeaponItem extends SubWeaponItem {
    public static final float MAX_INK_RECOVERY_COOLDOWN = 70f / 3f;
    public static final float INK_RECOVERY_COOLDOWN_MULTIPLIER = 40f / 3f;

    public CurlingSubWeaponItem(RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType, String settings, SubWeaponAction useTick) {
        super(entityType, settings, useTick);
    }



    @Override
    protected void throwSub(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity) {
        entity.swing(entity.getOffhandItem().equals(stack) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, false);

        if (!level.isClientSide()) {
            AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType.get(), level, entity, stack.copy());
            SubWeaponSettings settings = getSettings(stack);

            stack.getOrCreateTag().remove("EntityData");

            proj.setItem(stack);
            proj.shoot(entity, entity.getXRot(), entity.getYRot(), settings.throwAngle, settings.throwVelocity, 0);
            proj.setDeltaMovement(proj.getDeltaMovement().add(entity.getDeltaMovement().multiply(1, 0, 1)));
            level.addFreshEntity(proj);
        }
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.subThrow, SoundSource.PLAYERS, 0.7F, 1);
        if (SubWeaponItem.singleUse(stack)) {
            if (entity instanceof Player && !((Player) entity).isCreative())
                stack.shrink(1);
        } else {
            int cookTime = stack.getTag().getCompound("EntityData").getInt("CookTime");
            reduceInk(entity, this, getSettings(stack).inkConsumption, (int) (MAX_INK_RECOVERY_COOLDOWN - cookTime / Math.max(getSettings(stack).cookTime, 1) * INK_RECOVERY_COOLDOWN_MULTIPLIER), false);
        }
    }
}
