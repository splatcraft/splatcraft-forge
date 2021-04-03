package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.IInkOverlayInfo;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
import com.cibernet.splatcraft.entities.IColoredEntity;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.network.UpdateInkOverlayPacket;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class InkDamageUtils
{

    public static final DamageSource ENEMY_INK = new DamageSource(Splatcraft.MODID+":enemyInk");
    public static final DamageSource WATER = new DamageSource(Splatcraft.MODID+":water");
    public static final DamageSource VOID_DAMAGE = new DamageSource(Splatcraft.MODID+":outOfStage").setDamageBypassesArmor();


    public static boolean doSplatDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType)
    {
        return doDamage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "splat", false);
    }

    public static boolean doRollDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType)
    {
        return doDamage(world, target, damage, color, source, sourceItem, damageMobs, inkType, "roll", true);
    }

    public static boolean canDamageColor(World world, int targetColor, int sourceColor)
    {
        return SplatcraftGameRules.getBooleanRuleValue(world, SplatcraftGameRules.INK_FRIENDLY_FIRE) || !ColorUtils.colorEquals(world, targetColor, sourceColor);
    }

    public static boolean doDamage(World world, LivingEntity target, float damage, int color, Entity source, ItemStack sourceItem, boolean damageMobs, InkBlockUtils.InkType inkType, String name, boolean applyHurtCooldown)
    {

        if (damage == 0)
            return false;

        float mobDmgPctg = SplatcraftGameRules.getIntRuleValue(world, SplatcraftGameRules.INK_MOB_DAMAGE_PERCENTAGE) * 0.01f;
        boolean doDamage = target instanceof PlayerEntity || damageMobs || mobDmgPctg > 0;
        boolean applyInkCoverage = true;
        int targetColor = ColorUtils.getEntityColor(target);

        if (targetColor > -1)
        {
            doDamage = canDamageColor(world, color, targetColor);
            applyInkCoverage = doDamage;
        }

        InkDamageSource damageSource = new InkDamageSource(Splatcraft.MODID+":"+name, source, source, sourceItem);
        if (target instanceof IColoredEntity)
        {
            doDamage = ((IColoredEntity) target).onEntityInked(damageSource, damage, color);
            applyInkCoverage = doDamage;
        }
        else if (target instanceof SheepEntity)
        {
            if (!((SheepEntity) target).getSheared())
            {
                doDamage = false;
                applyInkCoverage = false;
            }
        }

        if (doDamage)
            target.attackEntityFrom(damageSource, damage * (target instanceof PlayerEntity || target instanceof IColoredEntity || damageMobs ? 1 : mobDmgPctg));

        if (applyInkCoverage && !target.isInWater())
        {
            if (InkOverlayCapability.hasCapability(target))
            {
                IInkOverlayInfo info = InkOverlayCapability.get(target);

                if (info.getAmount() < (target instanceof SquidBumperEntity ? SquidBumperEntity.maxInkHealth : target.getMaxHealth()) * 1.5)
                    info.addAmount(damage * (target instanceof IColoredEntity || damageMobs ? 1 : Math.max(0.5f, mobDmgPctg)));
                info.setColor(color);
                if (!world.isRemote)
                    SplatcraftPacketHandler.sendToAll(new UpdateInkOverlayPacket(target, info));
            }
        }



        if (!applyHurtCooldown && !SplatcraftGameRules.getBooleanRuleValue(world, SplatcraftGameRules.INK_DAMAGE_COOLDOWN))
            target.hurtResistantTime = 1;

        return doDamage;
    }

    public static boolean isSplatted(World world, LivingEntity target)
    {
        return target instanceof SquidBumperEntity ? ((SquidBumperEntity) target).getInkHealth() <= 0 : target.getHealth() <= 0;
    }

    public static class InkDamageSource extends IndirectEntityDamageSource
    {
        private final ItemStack weapon;

        public InkDamageSource(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn, ItemStack weapon)
        {
            super(damageTypeIn, source, indirectEntityIn);
            this.weapon = weapon;
        }

        @Override
        public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn)
        {
            ITextComponent itextcomponent = this.getTrueSource() == null ? Objects.requireNonNull(this.damageSourceEntity).getDisplayName() : this.getTrueSource().getDisplayName();
            String s = "death.attack." + this.damageType;
            String s1 = s + ".item";
            return !weapon.isEmpty() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), itextcomponent, weapon.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), itextcomponent);
        }
    }
}
