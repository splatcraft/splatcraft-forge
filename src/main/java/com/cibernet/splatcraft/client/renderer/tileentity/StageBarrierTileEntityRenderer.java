package com.cibernet.splatcraft.client.renderer.tileentity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.StageBarrierBlock;
import com.cibernet.splatcraft.tileentities.StageBarrierTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class StageBarrierTileEntityRenderer extends TileEntityRenderer<StageBarrierTileEntity>
{
    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static final RenderType BARRIER_RENDER = RenderType.makeType("splatcraft:stage_barriers", DefaultVertexFormats.BLOCK, 7, 262144, false, true, RenderType.State.getBuilder()
            .shadeModel(new RenderState.ShadeModelState(true)).lightmap(new RenderState.LightmapState(true)).texture(new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, true))
            .alpha(new RenderState.AlphaState(0.003921569F)).transparency(TRANSLUCENT_TRANSPARENCY).build(true));

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
        IVertexBuilder builder = buffer.getBuffer(Minecraft.isFabulousGraphicsEnabled() ? BARRIER_RENDER : RenderType.getTranslucentNoCrumbling());

        float alpha = activeTime / tileEntity.getMaxActiveTime();
        float[] rgb = new float[] {1,1,1};
        if(tileEntity.getBlockState().getBlock() instanceof IColoredBlock)
            rgb = ColorUtils.hexToRGB(((IColoredBlock) tileEntity.getBlockState().getBlock()).getColor(tileEntity.getWorld(), tileEntity.getPos()));

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
    }
}