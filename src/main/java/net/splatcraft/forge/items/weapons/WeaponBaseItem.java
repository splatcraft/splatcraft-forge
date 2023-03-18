package net.splatcraft.forge.items.weapons;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.blocks.InkedBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.IColoredItem;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.items.weapons.settings.IDamageCalculator;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeaponBaseItem extends Item implements IColoredItem
{
    public static final int USE_DURATION = 72000;
    protected final List<WeaponTooltip> stats = new ArrayList<>();
    protected boolean secret = false;

    public IDamageCalculator damageCalculator;

    public WeaponBaseItem(IDamageCalculator damageCalculator) {
        super(new Properties().stacksTo(1).tab(SplatcraftItemGroups.GROUP_WEAPONS));
        SplatcraftItems.inkColoredItems.add(this);
        SplatcraftItems.weapons.add(this);
        this.damageCalculator = damageCalculator;
    }

    public static boolean reduceInk(LivingEntity player, Item item, float amount, int recoveryCooldown, boolean sendMessage) {
        if (!enoughInk(player, item, amount, recoveryCooldown, sendMessage, false)) return false;
        ItemStack tank = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (tank.getItem() instanceof InkTankItem)
            InkTankItem.setInkAmount(tank, InkTankItem.getInkAmount(tank) - amount);
        return true;
    }

    public static boolean enoughInk(LivingEntity player, Item item, float consumption, int recoveryCooldown, boolean sendMessage) {
        return enoughInk(player, item, consumption, recoveryCooldown, sendMessage, false);
    }

    public static boolean enoughInk(LivingEntity player, Item item, float consumption, int recoveryCooldown, boolean sendMessage, boolean sub) {
        ItemStack tank = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (!SplatcraftGameRules.getBooleanRuleValue(player.level, SplatcraftGameRules.REQUIRE_INK_TANK)
                || player instanceof PlayerEntity && ((PlayerEntity) player).isCreative()
                && SplatcraftGameRules.getBooleanRuleValue(player.level, SplatcraftGameRules.INFINITE_INK_IN_CREATIVE)) {
            return true;
        }
        if (tank.getItem() instanceof InkTankItem) {
            boolean enoughInk = InkTankItem.getInkAmount(tank) - consumption >= 0
                    && (item == null || ((InkTankItem) tank.getItem()).canUse(item));
            if (!sub || enoughInk)
                InkTankItem.setRecoveryCooldown(tank, recoveryCooldown);
            if (!enoughInk && sendMessage)
                sendNoInkMessage(player, sub ? SplatcraftSounds.noInkSub : SplatcraftSounds.noInkMain);
            return enoughInk;
        }
        if (sendMessage)
            sendNoInkMessage(player, sub ? SplatcraftSounds.noInkSub : SplatcraftSounds.noInkMain);
        return false;
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World level, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (ColorUtils.isColorLocked(stack))
        {
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        } else
        {
            tooltip.add(new StringTextComponent(""));
        }

        for (WeaponTooltip stat : stats) {
            tooltip.add(stat.getTextComponent(stack, level).withStyle(TextFormatting.DARK_GREEN));
        }
    }

    public void addStat(WeaponTooltip stat) {
        stats.add(stat);
    }

    @Override
    public void fillItemCategory(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> list)
    {
        if (!secret)
        {
            super.fillItemCategory(group, list);
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World level, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor(player)
                    && PlayerInfoCapability.hasCapability(player))
                ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor(player));

            if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                if (PlayerInfoCapability.isSquid(player)) {
                    PlayerInfoCapability.get(player).setIsSquid(false);
                    if (!level.isClientSide)
                        SplatcraftPacketHandler.sendToTrackers(new PlayerSetSquidClientPacket(player.getUUID(), false), player);
                }
                player.setSprinting(false);
                player.inventory.selected = itemSlot;
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition().below();

        if (entity.level.getBlockState(pos).getBlock() instanceof InkwellBlock) {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te)) {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        } else if ((stack.getItem() instanceof SubWeaponItem && !SubWeaponItem.singleUse(stack) || !(stack.getItem() instanceof SubWeaponItem))
                && InkedBlock.causesClear(entity.level.getBlockState(pos)) && ColorUtils.getInkColor(stack) != 0xFFFFFF) {
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
    public int getUseDuration(@NotNull ItemStack stack)
    {
        return USE_DURATION;
    }

    public final ActionResult<ItemStack> useSuper(World level, PlayerEntity player, Hand hand)
    {
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(@NotNull World level, PlayerEntity player, @NotNull Hand hand)
    {
        if(!(player.isSwimming() && !player.isInWater()))
            player.startUsingItem(hand);
        return useSuper(level, player, hand);
    }

    @Override
    public @NotNull ActionResultType useOn(ItemUseContext context)
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
    public void onUseTick(@NotNull World p_219972_1_, @NotNull LivingEntity p_219972_2_, @NotNull ItemStack p_219972_3_, int p_219972_4_) {
        super.onUseTick(p_219972_1_, p_219972_2_, p_219972_3_, p_219972_4_);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull World level, LivingEntity entity, int timeLeft)
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

    public WeaponBaseItem setSecret()
    {
        secret = true;
        return this;
    }
}
