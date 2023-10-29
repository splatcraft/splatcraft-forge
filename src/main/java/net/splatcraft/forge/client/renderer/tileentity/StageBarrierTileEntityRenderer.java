package net.splatcraft.forge.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.blocks.StageBarrierBlock;
import net.splatcraft.forge.tileentities.StageBarrierTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;

public class StageBarrierTileEntityRenderer implements BlockEntityRenderer<StageBarrierTileEntity>
{

    protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    public static final RenderType BARRIER_RENDER = RenderType.create("splatcraft:stage_barriers", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.CompositeState.builder()
            .setLightmapState(new RenderStateShard.LightmapStateShard(true)).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Splatcraft.MODID, "textures/blocks/allowed_color_barrier_fancy.png"), false, true))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));


    public StageBarrierTileEntityRenderer(BlockEntityRendererProvider.Context context)
    {
    }

    private static boolean shouldRenderSide(StageBarrierTileEntity te, Direction side) {
        BlockPos relativePos = te.getBlockPos().relative(side);
        BlockState relativeState = te.getLevel().getBlockState(relativePos);

        if (!ClientUtils.shouldRenderSide(te, side)) return false;

        if (relativeState.getBlock() instanceof ColoredBarrierBlock && te.getLevel().getBlockState(te.getBlockPos()).getBlock() instanceof ColoredBarrierBlock)
            return ((ColoredBarrierBlock) relativeState.getBlock()).canAllowThrough(relativePos, Minecraft.getInstance().player) !=
                    ((ColoredBarrierBlock) te.getLevel().getBlockState(te.getBlockPos()).getBlock()).canAllowThrough(te.getBlockPos(), Minecraft.getInstance().player);

        return !(relativeState.getBlock() instanceof StageBarrierBlock);
    }

    private void addVertex(VertexConsumer builder, PoseStack matrixStack, float x, float y, float z, float textureX, float textureY, float r, float g, float b, float a)
    {
        builder.vertex(matrixStack.last().pose(), x, y, z)
                .color(r, g, b, a)
                .uv(textureX, textureY)
                .uv2(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }

    @Override
    public void render(StageBarrierTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {

        float activeTime = tileEntity.getActiveTime();
        Block block = tileEntity.getBlockState().getBlock();

        if (activeTime <= 0 || !(block instanceof StageBarrierBlock))
        {
            return;
        }

        ResourceLocation textureLoc = new ResourceLocation(Splatcraft.MODID, "blocks/" + block.getRegistryName().getPath() + (Minecraft.getInstance().options.graphicsMode.getId() > 0 ? "_fancy" : ""));

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLoc);
        VertexConsumer builder = buffer.getBuffer(Minecraft.useShaderTransparency() ? RenderType.translucentMovingBlock() : RenderType.translucentNoCrumbling());

        float alpha = activeTime / tileEntity.getMaxActiveTime();
        float[] rgb = new float[] {1,1,1};
        if(tileEntity.getBlockState().getBlock() instanceof IColoredBlock)
            rgb = ColorUtils.hexToRGB(ColorUtils.getInkColorOrInverted(tileEntity.getLevel(), tileEntity.getBlockPos()));

        if (shouldRenderSide(tileEntity, Direction.NORTH)) {
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (shouldRenderSide(tileEntity, Direction.SOUTH)) {
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (shouldRenderSide(tileEntity, Direction.WEST)) {
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (shouldRenderSide(tileEntity, Direction.EAST)) {
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (shouldRenderSide(tileEntity, Direction.DOWN)) {
            addVertex(builder, matrixStack, 0, 0, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 0, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 0, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
        }

        if (shouldRenderSide(tileEntity, Direction.UP)) {
            addVertex(builder, matrixStack, 0, 1, 1, sprite.getU0(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 1, sprite.getU1(), sprite.getV1(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 1, 1, 0, sprite.getU1(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
            addVertex(builder, matrixStack, 0, 1, 0, sprite.getU0(), sprite.getV0(), rgb[0], rgb[1], rgb[2], alpha);
        }
    }
}
