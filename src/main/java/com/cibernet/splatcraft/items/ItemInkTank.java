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
import java.util.List;

import static com.cibernet.splatcraft.utils.ColorItemUtils.isColorLocked;
import static com.cibernet.splatcraft.utils.ColorItemUtils.setInkColor;

public class ItemInkTank extends ItemArmor implements IBattleItem
{
	
	public final float capacity;
	private static Class<? extends ModelAbstractTank> modelClass;
	
	public ItemInkTank(String unlocalizedName, String registryName, float capacity, ArmorMaterial materialIn)
	{
		super(materialIn, 0, EntityEquipmentSlot.CHEST);
		
		setCreativeTab(TabSplatCraft.main);
		setUnlocalizedName(unlocalizedName);
		setRegistryName(registryName);
		
		this.capacity = capacity;
		ColorItemUtils.inkColorItems.add(this);
		
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
		super.addInformation(stack, worldIn, tooltip, advanced);
		
		if(advanced.isAdvanced())
		{
			tooltip.add(TextFormatting.GRAY + I18n.format("item.inkTank.ink", getInkAmount(stack), capacity));
		}
	}
	
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot,
									ModelBiped _default)
	{
		return (modelClass == null) ? super.getArmorModel(entity, stack, slot, _default) : ClientUtils.getInkTankModel(modelClass, entity, stack, slot, _default);
	}
	
	public Item setArmorModelClass(Class<? extends ModelAbstractTank> model) {this.modelClass = model; return this;}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(!(entityIn instanceof EntityPlayer))
			return;
		
		
		EntityPlayer player = (EntityPlayer) entityIn;
		setInkColor(stack, SplatCraftPlayerData.getInkColor(player));
		
		float ink = getInkAmount(stack);
		if((player).getItemStackFromSlot(EntityEquipmentSlot.CHEST).equals(stack) &&
				ink < capacity && ((player).getActiveItemStack().isEmpty() || ((player).getActiveItemStack().getItem() instanceof ICharge)))
		{
			float rechargeAmnt = SplatCraftPlayerData.getIsSquid(player) && SplatCraftUtils.canSquidHide(worldIn, player) ? 1f : 0.1f;
			setInkAmount(stack, Math.min(capacity, ink+rechargeAmnt));
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
	
	@Override
	public boolean hasColor(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getColor(ItemStack stack)
	{
		return ColorItemUtils.getInkColor(stack);
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
