package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.models.ModelAbstractTank;
import com.cibernet.splatcraft.entities.models.ModelInkTank;
import com.cibernet.splatcraft.utils.*;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;

import static com.cibernet.splatcraft.utils.ColorItemUtils.*;

public class ItemInkTank extends ItemInkColoredArmor
{
	
	public final float capacity;
	private static ModelAbstractTank model;
	
	public ItemInkTank(String unlocalizedName, String registryName, float capacity, ArmorMaterial materialIn)
	{
		super(unlocalizedName, registryName, materialIn, 0, EntityEquipmentSlot.CHEST);
		
		this.capacity = capacity;
		
		this.addPropertyOverride(new ResourceLocation("ink_stage"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				float pctg = getInkAmount(stack)/capacity;
				return pctg > 0.6f ? 2 : pctg <= 0.3f ? 0 : 1;
			}
		});
	}
	
	public ItemInkTank(String unlocalizedName, String registryName, float capacity)
	{
		this(unlocalizedName, registryName, capacity, EnumHelper.addArmorMaterial(unlocalizedName, SplatCraft.MODID+":"+registryName, -1, new int[] {0,0,0,0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0));
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag advanced)
	{
		if(advanced.isAdvanced())
		{
			tooltip.add(TextFormatting.GRAY + I18n.format("item.inkTank.ink", getInkAmount(stack), capacity));
		}
		
		if(isColorLocked(stack))
		{
			int color = getInkColor(stack);
			tooltip.add(SplatCraftUtils.getColorName(color));
		}
		
	}
	
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot,
									ModelBiped _default)
	{
		if(!(stack.getItem() instanceof ItemInkTank))
			return super.getArmorModel(entity, stack, slot, _default);
		
		if(model == null)
			return super.getArmorModel(entity, stack, slot, _default);
		
		model.setInk(ItemInkTank.getInkAmount(stack)/ ((ItemInkTank) stack.getItem()).capacity);
		
		if(!stack.isEmpty())
		{
			if(stack.getItem() instanceof ItemInkTank)
			{
				model.bipedRightLeg.showModel = slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET;
				model.bipedLeftLeg.showModel = slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET;
				
				model.bipedBody.showModel = slot == EntityEquipmentSlot.CHEST;
				model.bipedLeftArm.showModel = slot == EntityEquipmentSlot.CHEST;
				model.bipedRightArm.showModel = slot == EntityEquipmentSlot.CHEST;
				
				model.bipedHead.showModel = slot == EntityEquipmentSlot.HEAD;
				model.bipedHeadwear.showModel = slot == EntityEquipmentSlot.HEAD;
				
				
				model.isSneak = _default.isSneak;
				model.isRiding = _default.isRiding;
				model.isChild = _default.isChild;
				
				model.rightArmPose = _default.rightArmPose;
				model.leftArmPose = _default.leftArmPose;
				
				return model;
			}
		}
		
		return super.getArmorModel(entity, stack, slot, _default);
	}
	
	public Item setArmorModelClass(ModelAbstractTank model)
	{
		this.model = model;
		return this;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(!(entityIn instanceof EntityPlayer))
			return;
		
		
		EntityPlayer player = (EntityPlayer) entityIn;
		float ink = getInkAmount(stack);
		
		if(SplatCraftPlayerData.getInkColor(player) == getInkColor(stack) && (player).getItemStackFromSlot(EntityEquipmentSlot.CHEST).equals(stack) &&
				ink < capacity && ((player).getActiveItemStack().isEmpty() || ((player).getActiveItemStack().getItem() instanceof ICharge)))
		{
			float rechargeAmnt = SplatCraftPlayerData.getIsSquid(player) && SplatCraftUtils.canSquidHide(worldIn, player) ? 1f : 0.1f;
			setInkAmount(stack, Math.min(capacity, ink + rechargeAmnt));
		}
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1 - getInkAmount(stack) / capacity;
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ColorItemUtils.getInkColor(stack);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("ink") && getInkAmount(stack) < capacity;
	}
	
	@Override
	public boolean isRepairable()
	{
		return false;
	}
	
	public static float getInkAmount(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("ink"))
			return 0;
		return stack.getTagCompound().getFloat("ink");
	}
	
	public static ItemStack setInkAmount(ItemStack stack, float ink)
	{
		ColorItemUtils.checkTagCompound(stack).setFloat("ink", ink);
		return stack;
	}
}
