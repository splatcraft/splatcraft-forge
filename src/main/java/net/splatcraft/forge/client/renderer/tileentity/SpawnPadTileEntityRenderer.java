package net.splatcraft.forge.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.blocks.StageBarrierBlock;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.SpawnPadTileEntity;
import net.splatcraft.forge.tileentities.StageBarrierTileEntity;
import net.splatcraft.forge.util.ColorUtils;

public class SpawnPadTileEntityRenderer extends TileEntityRenderer<SpawnPadTileEntity>
{
    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static final RenderType BARRIER_RENDER = RenderType.create("splatcraft:stage_barriers", DefaultVertexFormats.BLOCK, 7, 262144, false, true, RenderType.State.builder()
            .setShadeModelState(new RenderState.ShadeModelState(true)).setLightmapState(new RenderState.LightmapState(true)).setTextureState(new RenderState.TextureState(new ResourceLocation(Splatcraft.MODID, "textures/blocks/allowed_color_barrier_fancy.png"), false, true))
            .setAlphaState(new RenderState.AlphaState(0.003921569F)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));

    private static final RenderType RENDER_TYPE = RenderType.entityCutout(new ResourceLocation(Splatcraft.MODID, "textures/blocks/allowed_color_barrier_fancy.png"));

    public SpawnPadTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private void addVertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a)
    {
        builder.vertex(matrixStack.last().pose(), x + .5f, y + .5f, z + .5f)
                .color(r, g, b, a)
                .uv(textureX, textureY)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0, 240)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(SpawnPadTileEntity tileEntity)
    {
        return tileEntity.getActiveTime() > 0 && ColorUtils.getPlayerColor(Minecraft.getInstance().player) != tileEntity.getColor();
    }

    @Override
    public void render(SpawnPadTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        float activeTime = tileEntity.getActiveTime();

        if (activeTime <= 0 || ColorUtils.getPlayerColor(Minecraft.getInstance().player) == tileEntity.getColor())
        {
            return;
        }

        IVertexBuilder builder = buffer.getBuffer(BARRIER_RENDER);

        float alpha = activeTime / tileEntity.getMaxActiveTime();
        float[] rgb = ColorUtils.hexToRGB(tileEntity.getColor());

        float radius = tileEntity.radius;
        float uvCorner = radius*2;


        addVertex(builder, matrixStack, -radius, radius, -radius, 0, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, radius, -radius, uvCorner, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, -radius, -radius, uvCorner, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, -radius, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
    

        addVertex(builder, matrixStack, -radius, -radius, radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, -radius, radius, uvCorner, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, radius, radius, uvCorner, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, radius, radius, 0, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
    
        addVertex(builder, matrixStack, -radius, -radius, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, -radius, radius, 0, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, radius, radius, uvCorner, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, radius, -radius, uvCorner, 0, rgb[0], rgb[1], rgb[2], alpha);
    

        addVertex(builder, matrixStack, radius, -radius, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, radius, -radius, uvCorner, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, radius, radius, uvCorner, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, -radius, radius, 0, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
    

        addVertex(builder, matrixStack, -radius, -radius, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, -radius, -radius, uvCorner, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, -radius, radius, uvCorner, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, -radius, radius, 0, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
    

        addVertex(builder, matrixStack, -radius, radius, radius, 0, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, radius, radius, uvCorner, uvCorner, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, radius, radius, -radius, uvCorner, 0, rgb[0], rgb[1], rgb[2], alpha);
        addVertex(builder, matrixStack, -radius, radius, -radius, 0, 0, rgb[0], rgb[1], rgb[2], alpha);
    }
}