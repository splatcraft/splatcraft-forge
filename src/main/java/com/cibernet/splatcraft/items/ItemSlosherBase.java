package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import javax.annotation.Nullable;

public class ItemSlosherBase extends ItemWeaponBase
{
	public float projectileSize;
	public float projectileSpeed;
	public float damage;
	public int startupTicks;
	public int projectileCount;
	public float diffAngle;
	
	
	public ItemSlosherBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, int projectileCount, float offsetBetweenProj, float damage, int startupTicks, float inkConsumption)
	{
		super(unlocName, registryName, inkConsumption);
		
		this.projectileSize = projectileSize;
		this.projectileSpeed = projectileSpeed;
		this.damage = damage;
		this.startupTicks = startupTicks;
		this.projectileCount = projectileCount;
		this.diffAngle = offsetBetweenProj;
		
		addPropertyOverride(new ResourceLocation("active"), new IItemPropertyGetter()
		{
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if(entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack)
					return getMaxItemUseDuration(stack)-entityIn.getItemInUseCount() < startupTicks*2 ? 1 : 0 ;
				return 0;
			}
		});
	}
	
	public ItemSlosherBase(String unlocName, String registryName, ItemSlosherBase parent)
	{
		this(unlocName, registryName, parent.projectileSize, parent.projectileSpeed, parent.projectileCount, parent.diffAngle, parent.damage, parent.startupTicks, parent.inkConsumption);
	}
	
	public ItemSlosherBase(String unlocName, String registryName, Item parent)
	{
		this(unlocName, registryName, (ItemSlosherBase) parent);
	}
	
	@Override
	public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
	{
		super.onItemTickUse(worldIn, playerIn, stack, useTime);
		
		int actualUseTime = getMaxItemUseDuration(stack)-useTime;
		if(hasInk(playerIn, stack))
		{
			if(actualUseTime == startupTicks)
			{
				if(!worldIn.isRemote)
				{
					reduceInk(playerIn);
					
					for(int i = 0; i < projectileCount; i++)
					{
						boolean hasTrail = i == Math.floor((projectileCount - 1) / 2f) || i == Math.ceil((projectileCount - 1) / 2f);
						float angle = (diffAngle * i) - (diffAngle * (projectileCount - 1)/2);
						
						EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, ColorItemUtils.getInkColor(stack), damage);
						proj.setTrail(hasTrail);
						proj.shoot(playerIn, Math.max(0f, (playerIn.rotationPitch) + 90 * 0.7f) - 90, playerIn.rotationYaw + angle, 0.0F, projectileSpeed * (hasTrail ? 1 : 0.95f), 2);
						proj.setProjectileSize(projectileSize * (hasTrail ? 1 : 0.8f));
						worldIn.spawnEntity(proj);
						
					}
				}
			}
		} else playerIn.sendStatusMessage(new TextComponentTranslation("status.noInk").setStyle(new Style().setColor(TextFormatting.RED)), true);
	}
	@Override
	public ModelPlayerOverride.EnumAnimType getAnimType()
	{
		return ModelPlayerOverride.EnumAnimType.BUCKET;
	}
}
