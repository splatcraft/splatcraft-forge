package com.cibernet.splatcraft.client.renderer;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.StageBarrierBlock;
import com.cibernet.splatcraft.tileentities.ColoredBarrierTileEntity;
import com.cibernet.splatcraft.tileentities.StageBarrierTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class StageBarrierTileEntityRenderer extends TileEntityRenderer<StageBarrierTileEntity>
{
    public StageBarrierTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private static boolean canRenderSide(TileEntity te, Direction side)
    {
        BlockPos pos = te.getPos().offset(side);
        BlockState state = te.getWorld().getBlockState(pos);
        return !(state.getBlock() instanceof StageBarrierBlock);
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

    @Override
    public void render(StageBarrierTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {

        float activeTime = tileEntity.getActiveTime();
        Block block = tileEntity.getBlockState().getBlock();

        if (activeTime <= 0 || !(block instanceof StageBarrierBlock))
        {
            return;
        }

        ResourceLocation textureLoc = new ResourceLocation(Splatcraft.MODID, "blocks/" + block.getRegistryName().getPath() + (Minecraft.getInstance().gameSettings.graphicFanciness.func_238162_a_() > 0 ? "_fancy" : ""));

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(textureLoc);
        IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucentNoCrumbling());

        float alpha = activeTime / tileEntity.getMaxActiveTime();

        matrixStack.push();

        float[] rgb = new float[] {1, 1, 1};

        if(tileEntity instanceof ColoredBarrierTileEntity)
            rgb = ColorUtils.hexToRGB(((ColoredBarrierTileEntity) tileEntity).getColor());

        if (canRenderSide(tileEntity, Direction.NORTH))
        {
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getMinU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getMaxU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.SOUTH))
        {
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getMinU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getMaxU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getMinU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.WEST))
        {
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getMaxU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getMaxU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.EAST))
        {
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getMinU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getMaxU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getMinU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.DOWN))
        {
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getMaxU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.UP))
        {
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getMinU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getMaxU(), sprite.getMaxV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getMaxU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getMinU(), sprite.getMinV(), rgb[0], rgb[1], rgb[2], alpha);
        }
        matrixStack.pop();
    }
}
