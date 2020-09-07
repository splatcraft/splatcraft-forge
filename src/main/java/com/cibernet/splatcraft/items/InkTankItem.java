package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.client.model.AbstractInkTankModel;
import com.cibernet.splatcraft.data.tags.SplatcraftTags;
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
import net.minecraft.item.IDyeableArmorItem;
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

public class InkTankItem extends ColoredArmorItem implements IDyeableArmorItem
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
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		
		if(entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity) entity;
			float ink = getInkAmount(stack);
			
			if(player.getItemStackFromSlot(EquipmentSlotType.CHEST).equals(stack) && ColorUtils.colorEquals(player, stack) && ink < capacity
			&& !(player.getActiveItemStack().getItem() instanceof WeaponBaseItem))
					setInkAmount(stack, Math.min(capacity, ink + (InkBlockUtils.canSquidHide(player) && PlayerInfoCapability.isSquid(player) ? 1 : 0.1f)));
		}
	}
	
	public InkTankItem(String name, float capacity, IArmorMaterial material)
	{
		this(name, capacity, material, new Properties().group(SplatcraftItemGroups.GROUP_WEAPONS).maxStackSize(1));
		
	}
	
	public InkTankItem(String name, InkTankItem parent)
	{
		this(name, parent.capacity, new SplatcraftArmorMaterial(name, (SplatcraftArmorMaterial) parent.material), parent.properties);
	}
	
	public InkTankItem(String name, float capacity)
	{
		this(name, capacity, new SplatcraftArmorMaterial(name, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0, 0, 0));
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		if(flag.isAdvanced())
			tooltip.add(new TranslationTextComponent("item.splatcraft.ink_tank.ink", String.format("%.1f",getInkAmount(stack)), capacity));
		
	}
	
	@Nullable
	@Override
	public <A extends BipedModel<?>> A getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlotType armorSlot, A _default)
	{
		if(entity.getEntityWorld().isRemote)
		{
			BipedModel model = getInkTankModel(entity, stack, slot, _default);
			return model != null ? (A) model : super.getArmorModel(entity, stack, slot, _default);
		}
		
		return super.getArmorModel(entity, stack, slot, _default);
	}
	
	@OnlyIn(Dist.CLIENT)
	private BipedModel getInkTankModel(LivingEntity entity, ItemStack stack, EquipmentSlotType slot,
									   BipedModel _default)
	{
		if(!(stack.getItem() instanceof InkTankItem))
			return super.getArmorModel(entity, stack, slot, _default);
		
		if(model == null)
			return super.getArmorModel(entity, stack, slot, _default);
		
		if(!stack.isEmpty())
		{
			if(stack.getItem() instanceof InkTankItem)
			{
				model.bipedRightLeg.showModel = slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET;
				model.bipedLeftLeg.showModel = slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET;
				
				model.bipedBody.showModel = slot == EquipmentSlotType.CHEST;
				model.bipedLeftArm.showModel = slot == EquipmentSlotType.CHEST;
				model.bipedRightArm.showModel = slot == EquipmentSlotType.CHEST;
				
				model.bipedHead.showModel = slot == EquipmentSlotType.HEAD;
				model.bipedHeadwear.showModel = slot == EquipmentSlotType.HEAD;
				
				model.isSneak = _default.isSneak;
				model.isSitting = _default.isSitting;
				model.isChild = _default.isChild;
				
				model.rightArmPose = _default.rightArmPose;
				model.leftArmPose = _default.leftArmPose;
				
				model.setInkLevels(InkTankItem.getInkAmount(stack)/ ((InkTankItem) stack.getItem()).capacity);
				
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
		return ColorUtils.getInkColor(stack);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().contains("Ink") && getInkAmount(stack) < capacity;
	}
	
	@Override
	public boolean isRepairable(ItemStack stack)
	{
		return false;
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
	
	public boolean canUse(Item item)
	{
		boolean hasWhitelist = SplatcraftTags.Items.getTag(SplatcraftTags.Items.INK_TANK_WHITELIST.get(this)).getAllElements().size() > 0;
		boolean inWhitelist = SplatcraftTags.Items.getTag(SplatcraftTags.Items.INK_TANK_WHITELIST.get(this)).contains(item);
		boolean inBlacklist = SplatcraftTags.Items.getTag(SplatcraftTags.Items.INK_TANK_BLACKLIST.get(this)).contains(item);
		
		return !inBlacklist && ((hasWhitelist && inWhitelist) || !hasWhitelist);
	}
	
	public void refill(ItemStack stack)
	{
		setInkAmount(stack, capacity);
	}
	
	public static void deplete(ItemStack stack)
	{
		setInkAmount(stack, 0);
	}
}
