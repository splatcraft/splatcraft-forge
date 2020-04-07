package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.models.ModelInklingSquid;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LayerSquidColor implements LayerRenderer<EntityPlayer>
{
	private final ModelInklingSquid squidModel = new ModelInklingSquid();
	private final RenderInklingSquid squidRender;
	private String name;
	
	public LayerSquidColor(RenderInklingSquid renderIn) {
		this.squidRender = renderIn;
	}
	
	public void doRenderLayer(EntityPlayer playerIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!playerIn.isInvisible()) {
			this.squidRender.bindTexture(this.getTexture());
			int color = SplatCraftPlayerData.getInkColor(playerIn);
			float r = (float) (Math.floor(color / (256*256))/255f);
			float g = (float) ((Math.floor(color / 256) % 256)/255f);
			float b = (color % 256)/255f;
			
			GlStateManager.color(r, g, b, 1.0F);
			
			
			this.squidModel.setModelAttributes(this.squidRender.getMainModel());
			this.squidModel.setLivingAnimations(playerIn, limbSwing, limbSwingAmount, partialTicks);
			this.squidModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1.0F, playerIn);
			this.squidModel.render(playerIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.disableBlend();
		}
		
	}
	
	public ResourceLocation getTexture()
	{
		return new ResourceLocation(SplatCraft.MODID, "textures/mobs/inkling_squid.png");
	}
	
	public boolean shouldCombineTextures() {
		return false;
	}
}
