package net.splatcraft.forge.items.weapons;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.client.SplatcraftItemRenderer;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.SubWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SubWeaponItem extends WeaponBaseItem<SubWeaponSettings>
{
    public final RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType;
    public final SubWeaponAction useTick;

    public static final ArrayList<SubWeaponItem> subs = new ArrayList<>();

    public SubWeaponItem(RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType, String settings, SubWeaponAction useTick) {
        super(settings);
        this.entityType = entityType;
        this.useTick = useTick;

        subs.add(this);
        DispenserBlock.registerBehavior(this, new SubWeaponItem.DispenseBehavior());
    }

    public SubWeaponItem(RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType, String settings) {
        this(entityType, settings, (level, entity, stack, useTime) -> {
        });
    }

    public static boolean singleUse(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("SingleUse");
    }

    @Override
    public Class<SubWeaponSettings> getSettingsClass() {
        return SubWeaponSettings.class;
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> list)
    {
        super.fillItemCategory(group, list);
        if(!isSecret && group == CreativeModeTab.TAB_SEARCH)
        {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putBoolean("SingleUse", true);
            list.add(stack);
        }

    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (SubWeaponItem.singleUse(stack))
            tooltip.add(new TranslatableComponent("item.splatcraft.tooltip.single_use"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand)
    {
        if (!(player.isSwimming() && !player.isInWater()) && (singleUse(player.getItemInHand(hand)) || enoughInk(player, this, getSettings(player.getItemInHand(hand)).inkConsumption, 0, true, true)))
            player.startUsingItem(hand);
        return useSuper(level, player, hand);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return SubWeaponItem.singleUse(stack) ? 16 : 1;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !SubWeaponItem.singleUse(stack) && super.isBarVisible(stack);
    }

    @Override
    public void weaponUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack itemStack, int timeLeft)
    {
        SubWeaponSettings settings = getSettings(itemStack);

        int useTime = getUseDuration(itemStack)-timeLeft;
        if(useTime == settings.holdTime)
            throwSub(itemStack, level, entity);
        else if(useTime < settings.holdTime)
            useTick.onUseTick(level, entity, itemStack, timeLeft);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity, int timeLeft)
    {
        super.releaseUsing(stack, level, entity, timeLeft);
        if(getUseDuration(stack)-timeLeft < getSettings(stack).holdTime)
            throwSub(stack, level, entity);
    }

    protected void throwSub(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity)
    {
        entity.swing(entity.getOffhandItem().equals(stack) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, false);

        if(!level.isClientSide())
        {
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
        } else reduceInk(entity, this, getSettings(stack).inkConsumption, getSettings(stack).inkRecoveryCooldown, false);

    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return SplatcraftItemRenderer.INSTANCE;
            }


        });
    }

    public static class DispenseBehavior extends DefaultDispenseItemBehavior
    {
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack)
        {
            if (SubWeaponItem.singleUse(stack)) {
                Level world = source.getLevel();
                Position iposition = DispenserBlock.getDispensePosition(source);
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                AbstractSubWeaponEntity projectileentity = this.getProjectile(world, iposition, stack);
                projectileentity.shoot(direction.getStepX(), (float) direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
                world.addFreshEntity(projectileentity);
                stack.shrink(1);
                return stack;
            }

            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Position iposition = DispenserBlock.getDispensePosition(source);
            ItemStack itemstack = stack.split(1);
            spawnItem(source.getLevel(), itemstack, 6, direction, iposition);
            return stack;
        }

        protected AbstractSubWeaponEntity getProjectile(Level levelIn, Position position, ItemStack stackIn)
        {
            if(!(stackIn.getItem() instanceof SubWeaponItem))
                return null;

            return AbstractSubWeaponEntity.create(((SubWeaponItem) stackIn.getItem()).entityType.get(),  levelIn, position.x(), position.y(), position.z(), ColorUtils.getInkColor(stackIn), InkBlockUtils.InkType.NORMAL, stackIn);
        }


        @Override
        protected void playSound(BlockSource source) {
            source.getLevel().playSound(null, source.x(), source.y(), source.z(), SplatcraftSounds.subThrow, SoundSource.PLAYERS, 0.7F, 1);
        }

        protected float getPower() {
            return 0.7f;
        }

        protected float getUncertainty() {
            return 0;
        }

    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose(ItemStack stack) {
        return PlayerPosingHandler.WeaponPose.SUB_HOLD;
    }


    public interface SubWeaponAction {
        void onUseTick(Level level, LivingEntity entity, ItemStack stack, int useTime);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if(oldStack.hasTag() && newStack.hasTag())
        {
            oldStack = oldStack.copy();
            newStack = newStack.copy();

            oldStack.getTag().remove("EntityData");
            newStack.getTag().remove("EntityData");

            return !ItemStack.isSame(oldStack, newStack);
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }
}
