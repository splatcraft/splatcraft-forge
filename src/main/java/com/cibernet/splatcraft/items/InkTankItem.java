package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.model.inktanks.AbstractInkTankModel;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.weapons.IChargeableWeapon;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.SplatcraftArmorMaterial;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InkTankItem extends ColoredArmorItem
{
    public static final ArrayList<InkTankItem> inkTanks = new ArrayList<>();

    public final float capacity;
    public final Properties properties;

    @OnlyIn(Dist.CLIENT)
    private AbstractInkTankModel model;

    public InkTankItem(String name, float capacity, IArmorMaterial material, Properties properties)
    {
        super(name, material, EquipmentSlotType.CHEST, properties);
        this.capacity = capacity;
        this.properties = properties;

        SplatcraftItems.weapons.add(this);
        inkTanks.add(this);
        SplatcraftTags.Items.putInkTankTags(this, name);
    }

    public InkTankItem(String name, float capacity, IArmorMaterial material)
    {
        this(name, capacity, material, new Properties().tab(SplatcraftItemGroups.GROUP_WEAPONS).stacksTo(1));

    }

    public InkTankItem(String name, InkTankItem parent)
    {
        this(name, parent.capacity, new SplatcraftArmorMaterial(name, (SplatcraftArmorMaterial) parent.material), parent.properties);
    }

    public InkTankItem(String name, float capacity)
    {
        this(name, capacity, new SplatcraftArmorMaterial(name, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, 0));
    }

    public static float getInkAmount(ItemStack stack)
    {
        return stack.getOrCreateTag().getFloat("Ink");
    }

    public static float getInkAmount(ItemStack tank, ItemStack weapon)
    {
        return ((InkTankItem) tank.getItem()).canUse(weapon.getItem()) ? getInkAmount(tank) : 0;
    }

    public static ItemStack setInkAmount(ItemStack stack, float value)
    {
        stack.getTag().putFloat("Ink", value);
        return stack;
    }

    public static boolean canRecharge(ItemStack stack)
    {
        return !stack.getOrCreateTag().contains("CanRecharge") || stack.getTag().getBoolean("CanRecharge");
    }

    public static void deplete(ItemStack stack)
    {
        setInkAmount(stack, 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) entity;
            float ink = getInkAmount(stack);

            if (canRecharge(stack) && player.getItemBySlot(EquipmentSlotType.CHEST).equals(stack) && ColorUtils.colorEquals(player, stack) && ink < capacity
                    && (!(player.getUseItem().getItem() instanceof WeaponBaseItem) || player.getUseItem().getItem() instanceof IChargeableWeapon))
            {
                setInkAmount(stack, Math.min(capacity, ink + (InkBlockUtils.canSquidHide(player) && PlayerInfoCapability.isSquid(player) ? 1 : 0.1f)));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);
        if (!canRecharge(stack))
        {
            tooltip.add(new TranslationTextComponent("item.splatcraft.ink_tank.cant_recharge"));
        }
        if (flag.isAdvanced())
        {
            tooltip.add(new TranslationTextComponent("item.splatcraft.ink_tank.ink", String.format("%.1f", getInkAmount(stack)), capacity));
        }

    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType armorSlot, A _default)
    {
        if (entity.level.isClientSide)
        {
            BipedModel<?> model = getInkTankModel(entity, stack, slot, _default);
            return model != null ? (A) model : super.getArmorModel(entity, stack, slot, _default);
        }

        return super.getArmorModel(entity, stack, slot, _default);
    }

    @OnlyIn(Dist.CLIENT)
    private BipedModel<?> getInkTankModel(LivingEntity entity, ItemStack stack, EquipmentSlotType slot, BipedModel<?> _default)
    {
        if (!(stack.getItem() instanceof InkTankItem))
        {
            return super.getArmorModel(entity, stack, slot, _default);
        }

        if (model == null)
        {
            return super.getArmorModel(entity, stack, slot, _default);
        }

        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof InkTankItem)
            {
                model.rightLeg.visible = slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET;
                model.leftLeg.visible = slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET;

                model.body.visible = slot == EquipmentSlotType.CHEST;
                model.leftArm.visible = slot == EquipmentSlotType.CHEST;
                model.rightArm.visible = slot == EquipmentSlotType.CHEST;

                model.head.visible = slot == EquipmentSlotType.HEAD;
                model.hat.visible = slot == EquipmentSlotType.HEAD;

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
    public Item setArmorModel(AbstractInkTankModel model)
    {
        this.model = model;
        return this;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1 - getInkAmount(stack) / capacity;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return !SplatcraftConfig.Client.vanillaInkDurability.get() ? ColorUtils.getInkColor(stack) : super.getRGBDurabilityForDisplay(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return (SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.DURABILITY)) &&
                stack.hasTag() && stack.getTag().contains("Ink") && getInkAmount(stack) < capacity;
    }

    @Override
    public boolean isRepairable(ItemStack stack)
    {
        return false;
    }

    public boolean canUse(Item item)
    {
        boolean hasWhitelist = SplatcraftTags.Items.INK_TANK_WHITELIST.get(this).getValues().size() > 0;
        boolean inWhitelist = SplatcraftTags.Items.INK_TANK_WHITELIST.get(this).contains(item);
        boolean inBlacklist = SplatcraftTags.Items.INK_TANK_BLACKLIST.get(this).contains(item);

        return !inBlacklist && (!hasWhitelist || inWhitelist);
    }

    public void refill(ItemStack stack)
    {
        setInkAmount(stack, capacity);
    }
}
