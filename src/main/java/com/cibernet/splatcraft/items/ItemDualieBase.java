package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemDualieBase extends ItemWeaponBase
{
	
	public float projectileSize;
	public float inaccuracy;
	public float projectileSpeed;
	public int firingSpeed;
	public float damage;
	
	public int maxRolls;
	public float rollSpeed;
	public float rollConsumption;
	
	public int offhandFiringOffset;
	
	public ItemDualieBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage,  float inkConsumption, int rolls, float rollSpeed, float rollConsuption)
	{
		super(unlocName, registryName, inkConsumption);
		this.inaccuracy = inaccuracy;
		this.projectileSize = projectileSize;
		this.projectileSpeed = projectileSpeed;
		this.firingSpeed = firingSpeed;
		this.damage = damage;
		
		this.maxRolls = rolls;
		this.rollSpeed = rollSpeed;
		this.rollConsumption = rollConsuption;
		
		offhandFiringOffset = firingSpeed/2;
	}
	
	@Override
	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
		if(hasInk(playerIn, ColorItemUtils.getInkColor(stack)))
		{
			if((getMaxItemUseDuration(stack) - useTime) % firingSpeed == 1)
			{
				if(!worldIn.isRemote)
				{
					reduceInk(playerIn);
					EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, ColorItemUtils.getInkColor(stack), damage);
					proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, playerIn.getCooldownTracker().getCooldown(stack.getItem(), 0) > 0 ? 0 : inaccuracy);
					proj.setProjectileSize(projectileSize);
					worldIn.spawnEntity(proj);
				}
				//else worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SplatCraftSounds.shooterShot, SoundCategory.PLAYERS, 0.8F, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
			}
		} else playerIn.sendStatusMessage(new TextComponentTranslation("status.noInk").setStyle(new Style().setColor(TextFormatting.RED)), true);
	}
	
	@Override
	public ModelPlayerOverride.EnumAnimType getAnimType()
	{
		return ModelPlayerOverride.EnumAnimType.DUALIES;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		
		int rollCooldown = getRollCooldown(stack);
		if(rollCooldown > 0)
			setRollCooldown(stack, rollCooldown-1);
		else if(getRollString(stack) > 0)
			setRollString(stack, 0);
			
	}
	
	public static int getRollString(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("rollString"))
			return 0;
		return stack.getTagCompound().getInteger("rollString");
	}
	
	public static ItemStack setRollString(ItemStack stack, int rollString)
	{
		ColorItemUtils.checkTagCompound(stack).setInteger("rollString", rollString);
		return stack;
	}
	
	public static int getRollCooldown(ItemStack stack)
	{
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("rollCooldown"))
			return 0;
		return stack.getTagCompound().getInteger("rollCooldown");
	}
	
	public static ItemStack setRollCooldown(ItemStack stack, int rollCooldown)
	{
		ColorItemUtils.checkTagCompound(stack).setInteger("rollCooldown", rollCooldown);
		return stack;
	}
}
