package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntityNPCSquid;
import com.cibernet.splatcraft.entities.models.ModelInklingSquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerNPCSquidColor implements LayerRenderer<EntityNPCSquid>
{
	private final ModelInklingSquid squidModel = new ModelInklingSquid();
	private final RenderNPCSquid squidRender;
	private String name;
	
	public LayerNPCSquidColor(RenderNPCSquid renderIn) {
		this.squidRender = renderIn;
	}
	
	public void doRenderLayer(EntityNPCSquid squid, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!squid.isInvisible()) {
			this.squidRender.bindTexture(this.getTexture());
			int color = squid.getColor();
			float r = (float) (Math.floor(color / (256*256))/255f);
			float g = (float) ((Math.floor(color / 256) % 256)/255f);
			float b = (color % 256)/255f;
			
			GlStateManager.color(r, g, b, 1.0F);
			
			
			this.squidModel.setModelAttributes(this.squidRender.getMainModel());
			this.squidModel.setLivingAnimations(squid, limbSwing, limbSwingAmount, partialTicks);
			this.squidModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1.0F, squid);
			this.squidModel.render(squid, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
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
