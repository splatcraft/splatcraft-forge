package com.cibernet.splatcraft.entities.renderers;

import com.cibernet.splatcraft.blocks.BlockSCBarrier;
import com.cibernet.splatcraft.entities.models.ModelBarrier;
import com.cibernet.splatcraft.tileentities.TileEntityStageBarrier;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChestRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderStageBarrier extends TileEntitySpecialRenderer<TileEntityStageBarrier>
{
	
	private final ModelBarrier model = new ModelBarrier();
	
	@Override
	public void render(TileEntityStageBarrier te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		
		float activeTime = Minecraft.getMinecraft().player.isCreative() ? te.getMaxActiveTime() : te.getActiveTime();
		Block block = te.getBlockType();
		
		if(activeTime <= 0 || !(block instanceof BlockSCBarrier))
			return;
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		
		bindTexture(((BlockSCBarrier)block).getModelTexture());
		GlStateManager.translate(x+0.5f, y-0.5f, z+0.5f);
		GlStateManager.color(1,1,1, (activeTime / te.getMaxActiveTime()));
		
		model.render();
		
		GlStateManager.disableBlend();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
	}
}
