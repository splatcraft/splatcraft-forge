package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.items.IColoredItem;
import com.cibernet.splatcraft.items.InkTankItem;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WeaponBaseItem extends Item implements IColoredItem
{
    public static final int USE_DURATION = 72000;
    protected final List<WeaponStat> stats = new ArrayList<>();
    protected boolean secret = false;

    public WeaponBaseItem()
    {
        super(new Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_WEAPONS));
        SplatcraftItems.inkColoredItems.add(this);
        SplatcraftItems.weapons.add(this);
    }

    public static float getInkAmount(LivingEntity player, ItemStack weapon)
    {
        if (!SplatcraftGameRules.getBooleanRuleValue(player.level, SplatcraftGameRules.REQUIRE_INK_TANK))
        {
            return Float.MAX_VALUE;
        }

        ItemStack tank = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (!(tank.getItem() instanceof InkTankItem))
        {
            return 0;
        }

        return InkTankItem.getInkAmount(tank, weapon);
    }

    public static boolean hasInk(LivingEntity player, ItemStack weapon)
    {
        return getInkAmount(player, weapon) > 0;
    }

    public static void reduceInk(LivingEntity player, float amount)
    {
        ItemStack tank = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (!SplatcraftGameRules.getBooleanRuleValue(player.level, SplatcraftGameRules.REQUIRE_INK_TANK))
        {
            return;
        }
        if (!(tank.getItem() instanceof InkTankItem))
        {
            return;
        }

        InkTankItem.setInkAmount(tank, InkTankItem.getInkAmount(tank) - amount);
    }

    public static void sendNoInkMessage(LivingEntity entity)
    {
        sendNoInkMessage(entity, SplatcraftSounds.noInkMain);
    }

    public static void sendNoInkMessage(LivingEntity entity, SoundEvent sound)
    {
        if (entity instanceof PlayerEntity)
        {
            ((PlayerEntity) entity).displayClientMessage(new TranslationTextComponent("status.no_ink").withStyle(TextFormatting.RED), true);
            if (sound != null)
            {
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, 0.8F,
                        ((entity.level.getRandom().nextFloat() - entity.level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            }
        }

    }



    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (ColorUtils.isColorLocked(stack))
        {
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        } else
        {
            tooltip.add(new StringTextComponent(""));
        }

        for (WeaponStat stat : stats)
        {
            tooltip.add(stat.getTextComponent(stack, level).withStyle(TextFormatting.DARK_GREEN));
        }
    }

    public void addStat(WeaponStat stat)
    {
        stats.add(stat);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list)
    {
        if (!secret)
        {
            super.fillItemCategory(group, list);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity)
                && PlayerInfoCapability.hasCapability((LivingEntity) entity))
            ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition().below();

        if (entity.level.getBlockState(pos).getBlock() instanceof InkwellBlock)
        {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
            {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }
        else if((stack.getItem() instanceof SubWeaponItem && !stack.getOrCreateTag().getBoolean("SingleUse") || !(stack.getItem() instanceof SubWeaponItem))
        && InkedBlock.causesClear(entity.level.getBlockState(pos)) && ColorUtils.getInkColor(stack) != 0xFFFFFF)
        {
            ColorUtils.setInkColor(stack, 0xFFFFFF);
            ColorUtils.setColorLocked(stack, false);
        }

        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        try
        {
            return ClientUtils.getDurabilityForDisplay(stack);
        } catch (NoClassDefFoundError e)
        {
            return 1;
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return !SplatcraftConfig.Client.vanillaInkDurability.get() ? ColorUtils.getInkColor(stack) : super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        try
        {
            return ClientUtils.showDurabilityBar(stack);
        } catch (NoClassDefFoundError e)
        {
            return false;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return USE_DURATION;
    }

    public final ActionResult<ItemStack> useSuper(World level, PlayerEntity player, Hand hand)
    {
        return super.use(level, player, hand);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand)
    {
        if(!(player.isSwimming() && !player.isInWater()))
            player.startUsingItem(hand);
        return useSuper(level, player, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (ColorUtils.isColorLocked(context.getItemInHand()) && state.getBlock() instanceof CauldronBlock && context.getPlayer() != null && !context.getPlayer().isCrouching())
        {
            int i = state.getValue(CauldronBlock.LEVEL);

            if (i > 0)
            {
                World level = context.getLevel();
                PlayerEntity player = context.getPlayer();
                ColorUtils.setColorLocked(context.getItemInHand(), false);

                context.getPlayer().awardStat(Stats.USE_CAULDRON);

                if (!player.isCreative())
                {
                    level.setBlock(context.getClickedPos(), state.setValue(CauldronBlock.LEVEL, MathHelper.clamp(i - 1, 0, 3)), 2);
                    level.updateNeighbourForOutputSignal(context.getClickedPos(), state.getBlock());
                }

                return ActionResultType.SUCCESS;
            }

        }

        return super.useOn(context);
    }

    @Override
    public void onUseTick(World p_219972_1_, LivingEntity p_219972_2_, ItemStack p_219972_3_, int p_219972_4_) {
        super.onUseTick(p_219972_1_, p_219972_2_, p_219972_3_, p_219972_4_);
    }

    @Override
    public void releaseUsing(ItemStack stack, World level, LivingEntity entity, int timeLeft)
    {
        entity.stopUsingItem();
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft)
    {

    }

    public void onPlayerCooldownEnd(World level, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown)
    {

    }

    public boolean hasSpeedModifier(LivingEntity entity, ItemStack stack)
    {
        return getSpeedModifier(entity, stack) != null;
    }

    public AttributeModifier getSpeedModifier(LivingEntity entity, ItemStack stack)
    {
        return null;
    }

    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.NONE;
    }
}
