package net.splatcraft.forge.items.weapons;

import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.WeaponStat;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubWeaponItem extends WeaponBaseItem
{
    public float inkConsumption;
    public final EntityType<? extends AbstractSubWeaponEntity> entityType;

    public static final ArrayList<SubWeaponItem> subs = new ArrayList<>();

    public SubWeaponItem(String name, EntityType<? extends AbstractSubWeaponEntity> entityType, float directDamage, float explosionSize, float inkConsumption) {
        super();
        setRegistryName(name);
        this.inkConsumption = inkConsumption;
        this.entityType = entityType;
        subs.add(this);

        addStat(new WeaponStat("damage", (stack, level) -> (int) (directDamage / 20 * 100)));
        addStat(new WeaponStat("impact", (stack, level) -> (int) (explosionSize / 4 * 100)));
        addStat(new WeaponStat("ink_consumption", (stack, level) -> (int) (inkConsumption)));
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
        return !stack.getOrCreateTag().getBoolean("SingleUse") && super.showDurabilityBar(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, World level, LivingEntity entity, int timeLeft)
    {
        super.releaseUsing(stack, level, entity, timeLeft);

        entity.swing(entity.getOffhandItem().equals(stack) ? Hand.OFF_HAND : Hand.MAIN_HAND, false);

        AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType, level, entity, stack);
        proj.setItem(stack);
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

    public static class DispenseBehavior extends DefaultDispenseItemBehavior
    {
        @Override
        public ItemStack execute(IBlockSource source, ItemStack stack)
        {
            if(stack.getOrCreateTag().getBoolean("SingleUse")) 
            {
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

}
