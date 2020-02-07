package com.cibernet.splatcraft.entities.models;

import com.cibernet.splatcraft.items.ItemRollerBase;
import com.cibernet.splatcraft.items.ItemShooterBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

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

		if(player.getItemInUseCount() > 0)
		{
			if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemShooterBase) {
				this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
				this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
				this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
				this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;

				this.bipedLeftArmwear.rotateAngleX = this.bipedLeftArm.rotateAngleX;
				this.bipedLeftArmwear.rotateAngleY = this.bipedLeftArm.rotateAngleY;
				this.bipedRightArmwear.rotateAngleY = this.bipedRightArm.rotateAngleY;
				this.bipedRightArmwear.rotateAngleX = this.bipedRightArm.rotateAngleX;
			}
			if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemRollerBase) {
				this.bipedRightArm.rotateAngleX = 0.1F * 0.5F - ((float)Math.PI / 10F);
				this.bipedRightArm.rotateAngleY = 0.0F;
				this.bipedRightArm.rotateAngleZ = 0.0F;
				//this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
				//this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;

				this.bipedLeftArmwear.rotateAngleX = this.bipedLeftArm.rotateAngleX;
				this.bipedLeftArmwear.rotateAngleY = this.bipedLeftArm.rotateAngleY;
				this.bipedRightArmwear.rotateAngleY = this.bipedRightArm.rotateAngleY;
				this.bipedRightArmwear.rotateAngleX = this.bipedRightArm.rotateAngleX;
			}
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
	
}
