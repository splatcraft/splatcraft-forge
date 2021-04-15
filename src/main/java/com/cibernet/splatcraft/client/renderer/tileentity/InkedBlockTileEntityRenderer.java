package com.cibernet.splatcraft.client.renderer.tileentity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class InkedBlockTileEntityRenderer extends TileEntityRenderer<InkedBlockTileEntity>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "blocks/power_egg_block");

    public InkedBlockTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private static void renderBlock(InkedBlockTileEntity te, BlockRendererDispatcher blockRendererDispatcher, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn)
    {

        BlockState blockStateIn = te.getBlockState();
        BlockRenderType blockrendertype = te.getSavedState().getRenderType();
        if (blockrendertype.equals(BlockRenderType.MODEL))
        {
            blockStateIn = te.getSavedState();
        }

        IBakedModel ibakedmodel = blockRendererDispatcher.getModelForState(blockStateIn);
        int i = ColorUtils.getInkColor(te);
        float f = (float) (i >> 16 & 255) / 255.0F;
        float f1 = (float) (i >> 8 & 255) / 255.0F;
        float f2 = (float) (i & 255) / 255.0F;

        //f = 0;
        //f1 = 1;
        //f2 = 1;

        renderModel(matrixStackIn.getLast(), bufferTypeIn.getBuffer(RenderTypeLookup.func_239220_a_(blockStateIn, false)), blockStateIn, ibakedmodel, f, f1, f2, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);

    }

    private static void renderModel(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData)
    {
        Random random = new Random();
        long i = 42L;

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderModelBrightnessColorQuads(matrixEntry, buffer, red, green, blue, modelIn.getQuads(state, direction, random, modelData), combinedLightIn, combinedOverlayIn);
        }

        random.setSeed(42L);
        renderModelBrightnessColorQuads(matrixEntry, buffer, red, green, blue, modelIn.getQuads(state, null, random, modelData), combinedLightIn, combinedOverlayIn);
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IVertexBuilder buffer, float red, float green, float blue, List<BakedQuad> quads, int combinedLightIn, int combinedOverlayIn)
    {
        for (BakedQuad bakedquad : quads)
        {
            float f = MathHelper.clamp(red, 0.0F, 1.0F);
            float f1 = MathHelper.clamp(green, 0.0F, 1.0F);
            float f2 = MathHelper.clamp(blue, 0.0F, 1.0F);

            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);

            buffer.addQuad(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
        }

    }

    @Override
    public void render(InkedBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        //renderBlock(tileEntityIn, blockRenderer, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        blockRenderer.renderBlock(SplatcraftBlocks.sardiniumBlock.getDefaultState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        //blockRenderer.renderBlock(tileEntityIn.getSavedState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }


}
