package com.cibernet.splatcraft.client.renderer.tileentity;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
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
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

public class InkedBlockTileEntityRenderer extends TileEntityRenderer<InkedBlockTileEntity>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "blocks/inked_block");
    public static final ResourceLocation TEXTURE_GLOWING = new ResourceLocation(Splatcraft.MODID, "blocks/glitter");

    protected static InkBlockUtils.InkType type;

    public InkedBlockTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private static void renderBlock(InkedBlockTileEntity te, BlockRendererDispatcher blockRendererDispatcher, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn)
    {
        type = InkBlockUtils.getInkType(te.getBlockState());
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

            if(type.equals(InkBlockUtils.InkType.CLEAR))
                buffer.addQuad(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
            else
            {
                if (type.equals(InkBlockUtils.InkType.GLOWING))
                    addQuad(buffer, Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE_GLOWING), matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
                addQuad(buffer, Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE), matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
            }
        }

    }


    private static void addQuad(IVertexBuilder bufferIn, TextureAtlasSprite sprite, MatrixStack.Entry matrixEntryIn, BakedQuad quadIn, float redIn, float greenIn, float blueIn, int combinedLightIn, int combinedOverlayIn)
    {
        float[] colorMuls = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
        int[] combinedLights = new int[]{combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn};
        boolean mulColor = false;

        int[] aint = quadIn.getVertexData();
        Vector3i vector3i = quadIn.getFace().getDirectionVec();
        Vector3f vector3f = new Vector3f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ());
        Matrix4f matrix4f = matrixEntryIn.getMatrix();
        vector3f.transform(matrixEntryIn.getNormal());
        int i = 8;
        int j = aint.length / 8;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int k = 0; k < j; ++k) {
                ((Buffer) intbuffer).clear();
                intbuffer.put(aint, k * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float f3;
                float f4;
                float f5;
                if (mulColor) {
                    float f6 = (float) (bytebuffer.get(12) & 255) / 255.0F;
                    float f7 = (float) (bytebuffer.get(13) & 255) / 255.0F;
                    float f8 = (float) (bytebuffer.get(14) & 255) / 255.0F;
                    f3 = f6 * colorMuls[k] * redIn;
                    f4 = f7 * colorMuls[k] * greenIn;
                    f5 = f8 * colorMuls[k] * blueIn;
                } else {
                    f3 = colorMuls[k] * redIn;
                    f4 = colorMuls[k] * greenIn;
                    f5 = colorMuls[k] * blueIn;
                }

                int l = bufferIn.applyBakedLighting(combinedLights[k], bytebuffer);
                float texU = (((k+1)/2) % (j/2)) == 0 ? sprite.getMinU() : sprite.getMaxU();//bytebuffer.getFloat(16);
                float texV = (k / (j/2)) == 1 ? sprite.getMinV() : sprite.getMaxV();//bytebuffer.getFloat(16);

                Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
                vector4f.transform(matrix4f);
                bufferIn.applyBakedNormals(vector3f, bytebuffer, matrixEntryIn.getNormal());
                bufferIn.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f3, f4, f5, 1.0F, texU, texV, combinedOverlayIn, l, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        }
    }

    @Override
    public void render(InkedBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        renderBlock(tileEntityIn, blockRenderer, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        //blockRenderer.renderBlock(SplatcraftBlocks.sardiniumBlock.getDefaultState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        //blockRenderer.renderBlock(tileEntityIn.getSavedState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }


}
