package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.subs.AbstractSubWeaponEntity;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        if(stack.getOrCreateTag().getBoolean("SingleUse"))
            tooltip.add(new TranslationTextComponent("item.splatcraft.tooltip.single_use"));
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {

        if(!(player.isActualySwimming() && !player.isInWater()))
        {
            if(getInkAmount(player, player.getHeldItem(hand)) >= inkConsumption)
                player.setActiveHand(hand);
            else sendNoInkMessage(player, SplatcraftSounds.noInkSub);
        }
        return onItemRightClickSuper(world, player, hand);
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
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
    {
        super.onPlayerStoppedUsing(stack, world, entity, timeLeft);

        entity.swing(entity.getHeldItemOffhand().equals(this) ? Hand.OFF_HAND : Hand.MAIN_HAND, false);

        AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType, world, entity, stack);
        proj.shoot(entity, entity.rotationPitch, entity.rotationYaw, -30f, 0.5f, 0);
        world.addEntity(proj);
        world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
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
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            if(stack.getOrCreateTag().getBoolean("SingleUse"))
                return super.dispenseStack(source, stack);

            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            IPosition iposition = DispenserBlock.getDispensePosition(source);
            ItemStack itemstack = stack.split(1);
            doDispense(source.getWorld(), itemstack, 6, direction, iposition);
            return stack;
        }

        @Override
        protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
        {
            if(!(stackIn.getItem() instanceof SubWeaponItem))
                return null;

            return AbstractSubWeaponEntity.create(((SubWeaponItem) stackIn.getItem()).entityType,  worldIn, position.getX(), position.getY(), position.getZ(), ColorUtils.getInkColor(stackIn), InkBlockUtils.InkType.NORMAL, stackIn);
        }

        @Override
        protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playSound(null, source.getX(), source.getY(), source.getZ(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        }

        @Override
        protected float getProjectileVelocity() {
            return 0.7f;
        }

        @Override
        protected float getProjectileInaccuracy() {
            return 0;
        }
    }

}
