package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.subs.AbstractSubWeaponEntity;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubWeaponItem extends WeaponBaseItem
{
    public float inkConsumption;
    public final EntityType<? extends AbstractSubWeaponEntity> entityType;

    public static final ArrayList<SubWeaponItem> subs = new ArrayList<>();

    public SubWeaponItem(String name, EntityType<? extends AbstractSubWeaponEntity> entityType, float inkConsumption)
    {
        super();
        setRegistryName(name);
        this.inkConsumption = inkConsumption;
        this.entityType = entityType;
        subs.add(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        if(stack.getOrCreateTag().getBoolean("SingleUse"))
            tooltip.add(new TranslationTextComponent("item.splatcraft.tooltip.single_use"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
    {

        if(!(player.isSwimming() && !player.isInWater()))
        {
            if(getInkAmount(player, player.getItemInHand(hand)) >= inkConsumption)
                player.startUsingItem(hand);
            else sendNoInkMessage(player, SplatcraftSounds.noInkSub);
        }
        return useSuper(level, player, hand);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("SingleUse") ? 16 : 1;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("SingleUse") ? false : super.showDurabilityBar(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, World level, LivingEntity entity, int timeLeft)
    {
        super.releaseUsing(stack, level, entity, timeLeft);

        entity.swing(entity.getOffhandItem().equals(this) ? Hand.OFF_HAND : Hand.MAIN_HAND, false);

        AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType, level, entity, stack);
        proj.shoot(entity, entity.xRot, entity.yRot, -30f, 0.5f, 0);
        level.addFreshEntity(proj);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        if(stack.getOrCreateTag().getBoolean("SingleUse"))
        {
            if(entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative())
                stack.shrink(1);
        }
        else reduceInk(entity, inkConsumption);

    }

    public static class DispenseBehavior extends ProjectileDispenseBehavior
    {
        @Override
        public ItemStack execute(IBlockSource source, ItemStack stack)
        {
            if(stack.getOrCreateTag().getBoolean("SingleUse"))
                return super.execute(source, stack);

            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            IPosition iposition = DispenserBlock.getDispensePosition(source);
            ItemStack itemstack = stack.split(1);
            spawnItem(source.getLevel(), itemstack, 6, direction, iposition);
            return stack;
        }

        @Override
        protected ProjectileEntity getProjectile(World levelIn, IPosition position, ItemStack stackIn)
        {
            if(!(stackIn.getItem() instanceof SubWeaponItem))
                return null;

            return AbstractSubWeaponEntity.create(((SubWeaponItem) stackIn.getItem()).entityType,  levelIn, position.x(), position.y(), position.z(), ColorUtils.getInkColor(stackIn), InkBlockUtils.InkType.NORMAL, stackIn);
        }

        @Override
        protected void playSound(IBlockSource source) {
            source.getLevel().playSound(null, source.x(), source.y(), source.z(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        }

        @Override
        protected float getPower() {
            return 0.7f;
        }

        @Override
        protected float getUncertainty() {
            return 0;
        }
    }

}
