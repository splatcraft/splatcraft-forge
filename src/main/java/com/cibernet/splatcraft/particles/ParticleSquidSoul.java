package com.cibernet.splatcraft.particles;

import com.cibernet.splatcraft.SplatCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleSquidSoul extends Particle
{
	public int color;
	protected TextureManager textureManager;
	private final int lifeTime;
	private int life;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(SplatCraft.MODID,"textures/entity/squid_soul.png");
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
	
	protected ParticleSquidSoul(World worldIn, double posXIn, double posYIn, double posZIn, int color)
	{
		super(worldIn, posXIn, posYIn, posZIn, 0, 0, 0);
		this.color = color;
		
		double r = Math.floor(color / (256*256));
		double g = Math.floor(color / 256) % 256;
		double b = color % 256;
		
		this.particleRed = Math.max(5/255f,(float) (r/255) - 5/255f);
		this.particleGreen = Math.max(5/255f,(float) (g/255) - 5/255f);
		this.particleBlue = Math.max(5/255f,(float) (b/255) - 5/255f);
		
		
		this.particleMaxAge = 40;//(int)(16.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D)) + 2;
		
		this.textureManager = Minecraft.getMinecraft().getTextureManager();
		this.lifeTime = 40;//6 + this.rand.nextInt(4);
		
		this.motionY = 0.15f;
		this.motionX = 0;
		this.motionZ = 0;
	}
	
	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		
		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}
		
		
		
		this.move(this.motionX, this.motionY, this.motionZ);
		
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		++this.life;
	}
	
	@Override
	public int getFXLayer()
	{
		return 3;
	}
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		int life = (int)(((float)this.life + partialTicks) * 15.0F / (float)this.lifeTime);
		
		if (life <= 15)
		{
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			
			float alpha = Math.max(0, (particleMaxAge - Math.max(particleMaxAge-10, particleAge))/10f - 0.2f);
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1);
			for(int i = 0; i < 3; i++)
			{
				float r = i == 1 ? particleRed : 1;
				float g = i == 1 ? particleGreen : 1;
				float b = i == 1 ? particleBlue : 1;
				
				this.textureManager.bindTexture(TEXTURE);
				float f = (float) (i % 4) / 4.0F;
				float f1 = f + 0.24975F;
				float f2 = (float) (i / 4) / 4.0F;
				float f3 = f2 + 0.24975F;
				float f4 = 2.0F * 0.2f;
				float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
				float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
				float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ); // + (i == 2 ? 1 : 0);
				//GlStateManager.color(1.0F, 1.0F, 0F, (40 - Math.max(30, particleAge))/10f);
				//GlStateManager.disableLighting();
				RenderHelper.disableStandardItemLighting();
				buffer.begin(7, VERTEX_FORMAT);
				buffer.pos((double) (f5 - rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4), (double) (f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double) f1, (double) f3).color(r, g, b, alpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
				buffer.pos((double) (f5 - rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4), (double) (f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double) f1, (double) f2).color(r, g, b, alpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
				buffer.pos((double) (f5 + rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4), (double) (f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double) f, (double) f2).color(r, g, b, alpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
				buffer.pos((double) (f5 + rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4), (double) (f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double) f, (double) f3).color(r, g, b, alpha).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
				Tessellator.getInstance().draw();
				//GlStateManager.enableLighting();
			}
			
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
