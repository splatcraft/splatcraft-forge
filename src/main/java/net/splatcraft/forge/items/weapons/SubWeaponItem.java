package net.splatcraft.forge.items.weapons;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SubWeaponItem extends WeaponBaseItem
{
    public float inkConsumption;
    public int inkRecoveryCooldown;
    public final EntityType<? extends AbstractSubWeaponEntity> entityType;

    public static final ArrayList<SubWeaponItem> subs = new ArrayList<>();
    public static final float throwVelocity = 0.75f;
    public static final float throwAngle = -30f;

    public SubWeaponItem(String name, EntityType<? extends AbstractSubWeaponEntity> entityType, float directDamage, float explosionSize, float inkConsumption, int inkRecoveryCooldown) {
        super(WeaponSettings.DEFAULT); //it's either keeping this here or making another interface for main weapons >_>
        setRegistryName(name);
        this.inkConsumption = inkConsumption;
        this.inkRecoveryCooldown = inkRecoveryCooldown;
        this.entityType = entityType;
        subs.add(this);

        addStat(new WeaponTooltip("damage", (stack, level) -> (int) (directDamage / 20 * 100)));
        addStat(new WeaponTooltip("impact", (stack, level) -> (int) (explosionSize / 4 * 100)));
        addStat(new WeaponTooltip("ink_consumption", (stack, level) -> (int) (inkConsumption)));
    }

    public static boolean singleUse(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("SingleUse");
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World level, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flag) {
        if (SubWeaponItem.singleUse(stack))
            tooltip.add(new TranslationTextComponent("item.splatcraft.tooltip.single_use"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(@NotNull World level, PlayerEntity player, @NotNull Hand hand)
    {
        if (!(player.isSwimming() && !player.isInWater()) && (singleUse(player.getItemInHand(hand)) || enoughInk(player, this, inkConsumption, inkRecoveryCooldown, true, true)))
            player.startUsingItem(hand);
        return useSuper(level, player, hand);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return SubWeaponItem.singleUse(stack) ? 16 : 1;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !SubWeaponItem.singleUse(stack) && super.showDurabilityBar(stack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull World level, LivingEntity entity, int timeLeft)
    {
        super.releaseUsing(stack, level, entity, timeLeft);

        entity.swing(entity.getOffhandItem().equals(stack) ? Hand.OFF_HAND : Hand.MAIN_HAND, false);

        AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType, level, entity, stack);
        proj.setItem(stack);
        proj.shoot(entity, entity.xRot, entity.yRot, throwAngle, throwVelocity, 0);
        proj.setDeltaMovement(proj.getDeltaMovement().add(entity.getDeltaMovement().multiply(1,0,1)));

        level.addFreshEntity(proj);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        if (SubWeaponItem.singleUse(stack)) {
            if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative())
                stack.shrink(1);
        } else reduceInk(entity, this, inkConsumption, 0, false);

    }

    public static class DispenseBehavior extends DefaultDispenseItemBehavior
    {
        @Override
        public ItemStack execute(IBlockSource source, ItemStack stack)
        {
            if (SubWeaponItem.singleUse(stack)) {
                World world = source.getLevel();
                IPosition iposition = DispenserBlock.getDispensePosition(source);
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                AbstractSubWeaponEntity projectileentity = this.getProjectile(world, iposition, stack);
                projectileentity.shoot(direction.getStepX(), (float) direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
                world.addFreshEntity(projectileentity);
                stack.shrink(1);
                return stack;
            }

            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            IPosition iposition = DispenserBlock.getDispensePosition(source);
            ItemStack itemstack = stack.split(1);
            spawnItem(source.getLevel(), itemstack, 6, direction, iposition);
            return stack;
        }

        protected AbstractSubWeaponEntity getProjectile(World levelIn, IPosition position, ItemStack stackIn)
        {
            if(!(stackIn.getItem() instanceof SubWeaponItem))
                return null;

            return AbstractSubWeaponEntity.create(((SubWeaponItem) stackIn.getItem()).entityType,  levelIn, position.x(), position.y(), position.z(), ColorUtils.getInkColor(stackIn), InkBlockUtils.InkType.NORMAL, stackIn);
        }


        @Override
        protected void playSound(IBlockSource source) {
            source.getLevel().playSound(null, source.x(), source.y(), source.z(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        }

        protected float getPower() {
            return 0.7f;
        }

        protected float getUncertainty() {
            return 0;
        }

    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose() {
        return PlayerPosingHandler.WeaponPose.SUB_HOLD;
    }
}
