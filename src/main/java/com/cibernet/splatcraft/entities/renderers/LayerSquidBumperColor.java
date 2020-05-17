package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.classes.EntitySquidBumper;
import com.cibernet.splatcraft.entities.models.ModelSquidBumper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerSquidBumperColor implements LayerRenderer<EntitySquidBumper>
{
	private final ModelSquidBumper bumperModel = new ModelSquidBumper();
	private final RenderSquidBumper bumperRender;
	private String name;
	
	public LayerSquidBumperColor(RenderSquidBumper renderIn) {
		this.bumperRender = renderIn;
	}
	
	public void doRenderLayer(EntitySquidBumper bumper, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!bumper.isInvisible()) {
			this.bumperRender.bindTexture(this.getTexture());
			int color = bumper.getColor();
			float r = (float) (Math.floor(color / (256*256))/255f);
			float g = (float) ((Math.floor(color / 256) % 256)/255f);
			float b = (color % 256)/255f;
			
			GlStateManager.color(r, g, b, 1.0F);
			
			
			this.bumperModel.setModelAttributes(this.bumperRender.getMainModel());
			this.bumperModel.setLivingAnimations(bumper, limbSwing, limbSwingAmount, partialTicks);
			this.bumperModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1.0F, bumper);
			this.bumperModel.render(bumper, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.disableBlend();
		}
		
	}
	
	public ResourceLocation getTexture()
	{
		return new ResourceLocation(SplatCraft.MODID, "textures/mobs/squid_bumper.png");
	}
	
	public boolean shouldCombineTextures() {
		return false;
	}
}
