package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.StageBarrierBlock;
import com.cibernet.splatcraft.client.model.StageBarrierModel;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.StageBarrierTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

public class StageBarrierTileEntityRenderer extends TileEntityRenderer<StageBarrierTileEntity>
{
	private static final StageBarrierModel model = new StageBarrierModel();
	
	public StageBarrierTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn);
	}
	
	private void addVertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a)
	{
		builder.pos(matrixStack.getLast().getMatrix(), x, y, z)
				.color(r, g, b, a)
				.tex(textureX, textureY)
				.lightmap(0, 240)
				.normal(1, 0, 0)
				.endVertex();
	}
	
	private void addVertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, TextureAtlasSprite sprite, float alpha)
	{
		addVertex(builder, matrixStack, x, y, z, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
	}
	
	
	private static float diffFunction(long time, long delta, float scale) {
		long dt = time % (delta * 2);
		if (dt > delta) {
			dt = 2*delta - dt;
		}
		return dt * scale;
	}
	
	@Override
	public void render(StageBarrierTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
	{
		
		float activeTime = false/*Minecraft.getInstance().player.isCreative()*/ ? tileEntity.getMaxActiveTime() : tileEntity.getActiveTime();
		Block block = tileEntity.getBlockState().getBlock();
		
		if(activeTime <= 0 || !(block instanceof StageBarrierBlock))
			return;
		
		ResourceLocation textureLoc = new ResourceLocation(Splatcraft.MODID, "blocks/" + block.getRegistryName().getPath() + (Minecraft.getInstance().gameSettings.field_238330_f_.func_238162_a_() > 0 ? "_fancy" : ""));
		
		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(textureLoc);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());
		
		float alpha = (activeTime / tileEntity.getMaxActiveTime());
		
		matrixStack.push();
		
		if(canRenderSide(tileEntity, Direction.NORTH))
		{
			addVertex(builder, matrixStack, 0, 1, 0, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 1, 0, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
		}
		
		if(canRenderSide(tileEntity, Direction.SOUTH))
		{
			addVertex(builder, matrixStack, 0, 0, 1, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 0, 1, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 1, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
		}
		
		if(canRenderSide(tileEntity, Direction.WEST))
		{
			addVertex(builder, matrixStack, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 1, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
		}
		
		if(canRenderSide(tileEntity, Direction.EAST))
		{
			addVertex(builder, matrixStack, 1, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 1, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 0, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
		}
		
		if(canRenderSide(tileEntity, Direction.DOWN))
		{
			addVertex(builder, matrixStack, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 0, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
		}
		
		if(canRenderSide(tileEntity, Direction.UP))
		{
			addVertex(builder, matrixStack, 0, 1, 1, sprite.getMinU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 1, 1, 0, sprite.getMaxU(), sprite.getMinV(), 1, 1, 1, alpha);
			addVertex(builder, matrixStack, 0, 1, 0, sprite.getMinU(), sprite.getMinV(), 1, 1, 1, alpha);
		}
		matrixStack.pop();
	}
	
	private static boolean canRenderSide(TileEntity te, Direction side)
	{
		BlockPos pos = te.getPos().offset(side);
		return !te.getWorld().getBlockState(pos).isSolidSide(te.getWorld(), pos, side.getOpposite());
	}
}
