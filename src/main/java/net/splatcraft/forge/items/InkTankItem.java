package net.splatcraft.forge.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.ChargerItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.SplatcraftArmorMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InkTankItem extends ColoredArmorItem
{
    public static final ArrayList<InkTankItem> inkTanks = new ArrayList<>();

    public final float capacity;
    public final Properties properties;

    //@OnlyIn(Dist.CLIENT)
    //private AbstractInkTankModel model;

    public InkTankItem(String tagId, float capacity, ArmorMaterial material, Properties properties)
    {
        super(material, EquipmentSlot.CHEST, properties);
        this.capacity = capacity;
        this.properties = properties;

        SplatcraftItems.weapons.add(this);
        inkTanks.add(this);
        SplatcraftTags.Items.putInkTankTags(this, tagId);
    }

    public InkTankItem(String tagId, float capacity, ArmorMaterial material)
    {
        this(tagId, capacity, material, new Properties().tab(SplatcraftItemGroups.GROUP_WEAPONS).stacksTo(1));

    }

    public InkTankItem(String name, InkTankItem parent)
    {
        this(name, parent.capacity, new SplatcraftArmorMaterial(name, (SplatcraftArmorMaterial) parent.material), parent.properties);
    }

    public InkTankItem(String name, float capacity)
    {
        this(name, capacity, new SplatcraftArmorMaterial(name, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, 0));
    }


    public static float getInkAmount(ItemStack stack) {
        float capacity = ((InkTankItem) stack.getItem()).capacity;
        if (stack.getOrCreateTag().getBoolean("InfiniteInk")) return capacity;
        return Math.max(0, Math.min(capacity, stack.getOrCreateTag().getFloat("Ink")));
    }

    public static void setInkAmount(ItemStack stack, float value) {
        stack.getOrCreateTag().putFloat("Ink", Math.max(0, Math.min(((InkTankItem) stack.getItem()).capacity, value)));
    }

    public static boolean canRecharge(ItemStack stack, boolean fromTick)
    {
        CompoundTag tag = stack.getOrCreateTag();
        boolean cannotRecharge = tag.contains("CannotRecharge");
        if (!tag.contains("RecoveryCooldown"))
            tag.putInt("RecoveryCooldown", 0);
        int cooldown = tag.getInt("RecoveryCooldown");
        if (cooldown == 0 || !fromTick) return !cannotRecharge;
        tag.putInt("RecoveryCooldown", --cooldown);
        return false;
    }

    public static void setRecoveryCooldown(ItemStack stack, int recoveryCooldown) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("RecoveryCooldown", Math.max(tag.getInt("RecoveryCooldown"), recoveryCooldown));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof Player && SplatcraftGameRules.getLocalizedRule(level, entity.blockPosition(), SplatcraftGameRules.RECHARGEABLE_INK_TANK)) {
            Player player = (Player) entity;
            float ink = getInkAmount(stack);

            if (canRecharge(stack, true) && player.getItemBySlot(EquipmentSlot.CHEST).equals(stack) && ColorUtils.colorEquals(player, stack) && ink < capacity
                    && (!(player.getUseItem().getItem() instanceof WeaponBaseItem) || player.getUseItem().getItem() instanceof ChargerItem || PlayerCooldown.hasPlayerCooldown(player))) {
                setInkAmount(stack, ink + (100f / 20f / ((InkBlockUtils.canSquidHide(player) && PlayerInfoCapability.isSquid(player)) ? 3f : 10f)));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);
        if (!canRecharge(stack, false)) {
            tooltip.add(new TranslatableComponent("item.splatcraft.ink_tank.cant_recharge"));
        }
        if (flag.isAdvanced()) {
            tooltip.add(new TranslatableComponent("item.splatcraft.ink_tank.ink", String.format("%.1f", getInkAmount(stack)), capacity));
        }

    }


    /* TODO GeckoLib Models
    @SuppressWarnings("unchecked")
    @Nullable
    //@Override >:( it's an event now
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot armorSlot, A _default)
    {
        if (entity.level.isClientSide)
        {
            HumanoidModel<?> model = getInkTankModel(entity, stack, slot, _default);
            return model != null ? (A) model : null;//super.getArmorModel(entity, stack, slot, _default);
        }

        return null; //super.getArmorModel(entity, stack, slot, _default);
    }

    @OnlyIn(Dist.CLIENT)
    private HumanoidModel<?> getInkTankModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> _default)
    {
        if (!(stack.getItem() instanceof InkTankItem))
        {
            return null; //super.getArmorModel(entity, stack, slot, _default);
        }

        if (model == null)
        {
            return null;//super.getArmorModel(entity, stack, slot, _default);
        }

        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof InkTankItem)
            {
                model.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
                model.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;

                model.body.visible = slot == EquipmentSlot.CHEST;
                model.leftArm.visible = slot == EquipmentSlot.CHEST;
                model.rightArm.visible = slot == EquipmentSlot.CHEST;

                model.head.visible = slot == EquipmentSlot.HEAD;
                model.hat.visible = slot == EquipmentSlot.HEAD;

                model.crouching = _default.crouching;
                model.riding = _default.riding;
                model.young = _default.young;

                model.rightArmPose = _default.rightArmPose;
                model.leftArmPose = _default.leftArmPose;

                model.setInkLevels(InkTankItem.getInkAmount(stack) / ((InkTankItem) stack.getItem()).capacity);

                return model;
            }
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void setArmorModel(AbstractInkTankModel model)
    {
        this.model = model;
    }
    */

    @Override
    public int getBarWidth(ItemStack stack)
    {
        return (int) (1 - getInkAmount(stack) / capacity * 13);
    }


    @Override
    public int getBarColor(ItemStack stack)
    {
        return !SplatcraftConfig.Client.vanillaInkDurability.get() ? ColorUtils.getInkColor(stack) : super.getBarColor(stack);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        return (SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.DURABILITY)) &&
                stack.getOrCreateTag().contains("Ink") && getInkAmount(stack) < capacity;
    }

    @Override
    public boolean isRepairable(@Nullable ItemStack stack)
    {
        return false;
    }

    public boolean canUse(Item item)
    {
        boolean hasWhitelist = false; //SplatcraftTags.Items.INK_TANK_WHITELIST.get(this).isFor().size() > 0; TODO
        boolean inWhitelist = item.builtInRegistryHolder().is(SplatcraftTags.Items.INK_TANK_WHITELIST.get(this));
        boolean inBlacklist = item.builtInRegistryHolder().is(SplatcraftTags.Items.INK_TANK_BLACKLIST.get(this));

        return !inBlacklist && (!hasWhitelist || inWhitelist);
    }

    public void refill(ItemStack stack)
    {
        setInkAmount(stack, capacity);
    }
}
