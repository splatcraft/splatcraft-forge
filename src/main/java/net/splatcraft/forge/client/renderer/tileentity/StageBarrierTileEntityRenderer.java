package net.splatcraft.forge.client.renderer.tileentity;

import net.minecraft.client.renderer.texture.AtlasTexture;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.blocks.StageBarrierBlock;
import net.splatcraft.forge.tileentities.ColoredBarrierTileEntity;
import net.splatcraft.forge.tileentities.StageBarrierTileEntity;
import net.splatcraft.forge.util.ColorUtils;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
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
    public static final RenderType BARRIER_RENDER = RenderType.create("splatcraft:stage_barriers", DefaultVertexFormats.BLOCK, 7, 262144, false, true, RenderType.State.builder()
            .setShadeModelState(new RenderState.ShadeModelState(true)).setLightmapState(new RenderState.LightmapState(true)).setTextureState(new RenderState.TextureState(new ResourceLocation(Splatcraft.MODID, "textures/blocks/allowed_color_barrier_fancy.png"), false, true))
            .setAlphaState(new RenderState.AlphaState(0.003921569F)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));

    public StageBarrierTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private static boolean canRenderSide(TileEntity te, Direction side)
    {
        BlockPos pos = te.getBlockPos().relative(side);
        BlockState state = te.getLevel().getBlockState(pos);

        if(state.getBlock() instanceof ColoredBarrierBlock && te.getLevel().getBlockState(te.getBlockPos()).getBlock() instanceof ColoredBarrierBlock)
            return ((ColoredBarrierBlock)state.getBlock()).canAllowThrough(pos, Minecraft.getInstance().player) !=
                    ((ColoredBarrierBlock)te.getLevel().getBlockState(te.getBlockPos()).getBlock()).canAllowThrough(te.getBlockPos(), Minecraft.getInstance().player);

        return !(state.getBlock() instanceof StageBarrierBlock);
    }

    private void addVertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a)
    {
        builder.vertex(matrixStack.last().pose(), x, y, z)
                .color(r, g, b, a)
                .uv(textureX, textureY)
                .uv2(0, 240)
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

        ResourceLocation textureLoc = new ResourceLocation(Splatcraft.MODID, "blocks/" + block.getRegistryName().getPath() + (Minecraft.getInstance().options.graphicsMode.getId() > 0 ? "_fancy" : ""));

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(textureLoc);
        IVertexBuilder builder = buffer.getBuffer(Minecraft.useShaderTransparency() ? BARRIER_RENDER : RenderType.translucentNoCrumbling());

        float alpha = activeTime / tileEntity.getMaxActiveTime();
        float[] rgb = new float[] {1,1,1};
        if(tileEntity.getBlockState().getBlock() instanceof IColoredBlock)
            rgb = ColorUtils.hexToRGB(((IColoredBlock) tileEntity.getBlockState().getBlock()).getColor(tileEntity.getLevel(), tileEntity.getBlockPos()));

        if (canRenderSide(tileEntity, Direction.NORTH))
        {
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.SOUTH))
        {
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.WEST))
        {
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.EAST))
        {
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.DOWN))
        {
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (canRenderSide(tileEntity, Direction.UP))
        {
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
        }
    }
}