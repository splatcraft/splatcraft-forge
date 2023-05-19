package net.splatcraft.forge.items.weapons;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.client.renderer.SubWeaponItemRenderer;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SubWeaponItem extends WeaponBaseItem implements IAnimatable
{
    public final RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType;
    public final SubWeaponAction useTick;
    public final int maxUseTime;
    public final WeaponSettings settings;

    public static final ArrayList<SubWeaponItem> subs = new ArrayList<>();
    public static final float throwVelocity = 0.75f;
    public static final float throwAngle = -30f;
    private final AnimationFactory animFactory = GeckoLibUtil.createFactory(this);

    public SubWeaponItem(RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType, WeaponSettings settings, int maxUseTime, SubWeaponAction useTick) {
        super(settings);
        setRegistryName(settings.name);
        this.settings = settings;
        this.entityType = entityType;
        this.useTick = useTick;
        this.maxUseTime = maxUseTime;

        subs.add(this);

        addStat(new WeaponTooltip("damage", (stack, level) -> (int) settings.baseDamage / 20 * 100));
        addStat(new WeaponTooltip("impact", (stack, level) -> (int) (settings.projectileSize / 4 * 100)));
        addStat(new WeaponTooltip("ink_consumption", (stack, level) -> (int) (settings.inkConsumption)));


        DispenserBlock.registerBehavior(this, new SubWeaponItem.DispenseBehavior());
    }

    public SubWeaponItem(RegistryObject<? extends EntityType<? extends AbstractSubWeaponEntity>> entityType, WeaponSettings settings) {
        this(entityType, settings, USE_DURATION, (level, entity, stack, useTime) -> {
        });
    }

    public static boolean singleUse(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("SingleUse");
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
        if (!(player.isSwimming() && !player.isInWater()) && (singleUse(player.getItemInHand(hand)) || enoughInk(player, this, settings.inkConsumption, 0, true, true)))
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
        int useTime = getUseDuration(itemStack)-timeLeft;
        if(useTime == maxUseTime)
            throwSub(itemStack, level, entity);
        else if(useTime < maxUseTime)
            useTick.onUseTick(level, entity, itemStack, timeLeft);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity, int timeLeft)
    {
        super.releaseUsing(stack, level, entity, timeLeft);
        if(getUseDuration(stack)-timeLeft < maxUseTime)
            throwSub(stack, level, entity);
    }

    protected void throwSub(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity)
    {
        entity.swing(entity.getOffhandItem().equals(stack) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, false);

        if(!level.isClientSide())
        {
            AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType.get(), level, entity, stack.copy());

            stack.getOrCreateTag().remove("EntityData");

            proj.setItem(stack);
            proj.shoot(entity, entity.getXRot(), entity.getYRot(), throwAngle, throwVelocity, 0);
            proj.setDeltaMovement(proj.getDeltaMovement().add(entity.getDeltaMovement().multiply(1, 0, 1)));
            level.addFreshEntity(proj);
        }
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.subThrow, SoundSource.PLAYERS, 0.7F, 1);
        if (SubWeaponItem.singleUse(stack)) {
            if (entity instanceof Player && !((Player) entity).isCreative())
                stack.shrink(1);
        } else reduceInk(entity, this, settings.inkConsumption, settings.inkRecoveryCooldown, false);

    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AnimationFactory getFactory() {
        return animFactory;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties()
        {
            private final BlockEntityWithoutLevelRenderer renderer = new SubWeaponItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
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
    public PlayerPosingHandler.WeaponPose getPose() {
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
