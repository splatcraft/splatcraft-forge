package com.cibernet.splatcraft.entities.models;

import com.cibernet.splatcraft.items.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelPlayerOverride extends ModelPlayer
{
	private ModelBase oldModel;
	private float thirdPersonPartialTicks;
	
	public ModelPlayerOverride(ModelBase oldModel, float modelSize, boolean smallArmsIn)
	{
		super(modelSize, smallArmsIn);
		this.oldModel = oldModel;
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
		oldModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
		thirdPersonPartialTicks = partialTickTime;
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		oldModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		
		EntityPlayer player = (EntityPlayer) entityIn;
		if(player == null) return;
		
		boolean isClient = player.getEntityId() == Minecraft.getMinecraft().player.getEntityId();
		//EnumHand hand = player.getActiveHand();
		
		ItemStack stack = player.getActiveItemStack();
		Item activeItem = stack.getItem();
		int useTime = activeItem.getMaxItemUseDuration(stack) - player.getItemInUseCount();
		
		if(useTime > 0)
		{
			EnumHandSide handSide = player.getPrimaryHand();
			if(player.getActiveHand() == EnumHand.OFF_HAND)
				handSide = handSide.opposite();
			
			ModelRenderer mainHand = getArmForSide(handSide);
			ModelRenderer offHand = getArmForSide(handSide.opposite());
			ItemStack offhandStack = player.getHeldItem(player.getHeldItemMainhand().equals(player.getActiveItemStack()) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
			
			if(!(activeItem instanceof ItemWeaponBase))
				return;
				
			EnumAnimType type = ((ItemWeaponBase) activeItem).getAnimType();
			
			/*
			if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemShooterBase)
				type = EnumAnimType.SHOOTER;
			else if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemShooterBase)
			{
				type = EnumAnimType.SHOOTER;
				handSide = handSide.opposite();
				mainHand = getArmForSide(handSide);
				offHand = getArmForSide(handSide.opposite());
			}
			if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemRollerBase)
				type = EnumAnimType.ROLLER;
			else if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemRollerBase)
			{
				type = EnumAnimType.ROLLER;
				handSide = handSide.opposite();
				mainHand = getArmForSide(handSide);
				offHand = getArmForSide(handSide.opposite());
			}
			*/
			
			switch(type)
			 {
			 	case DUALIES:
					 if(offhandStack.getItem() instanceof ItemDualieBase)
					 {
						 offHand.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
						 offHand.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
					 }
			 	case SHOOTER:
					mainHand.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
					mainHand.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
				break;
			 	case ROLLER:
					mainHand.rotateAngleX = 0.1F * 0.5F - ((float)Math.PI / 10F);
					mainHand.rotateAngleY = 0.0F;
					mainHand.rotateAngleZ = 0.0F;
					//this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
					//this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
				break;
				 case CHARGER:
					 mainHand.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
					 mainHand.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
					 offHand.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
					 offHand.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
				break;
				case BUCKET:
					float animTime = ((ItemSlosherBase)activeItem).startupTicks*2;
					mainHand.rotateAngleY = 0;
					mainHand.rotateAngleX = -0.36f;
					
					float angle = (useTime/1.5f) * (animTime/6f);
					
					if(angle < 6.5f)
						mainHand.rotateAngleX = MathHelper.cos(angle * 0.6662F) * 2.0F * 0.5F;
				break;
			}
			
			this.bipedLeftArmwear.rotateAngleX = this.bipedLeftArm.rotateAngleX;
			this.bipedLeftArmwear.rotateAngleY = this.bipedLeftArm.rotateAngleY;
			this.bipedRightArmwear.rotateAngleY = this.bipedRightArm.rotateAngleY;
			this.bipedRightArmwear.rotateAngleX = this.bipedRightArm.rotateAngleX;
			
			/*
			if (isClient) {
				if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {

					float heldPercent = 1.0F;

					GlStateManager.rotate(-50F * heldPercent, 1, 0, 0);
					GlStateManager.rotate(30F * heldPercent, 0, 1, 0);
					GlStateManager.rotate(-30F * heldPercent, 0, 0, 1);
					GlStateManager.translate(-0.3 * heldPercent, -0.2 * heldPercent, -0.5 * heldPercent);

				}
			}
			*/
		}
	}
	
	public enum EnumAnimType
	{
		NONE,
		SHOOTER,
		ROLLER,
		CHARGER,
		DUALIES,
		BUCKET;
		
	}
	
}
