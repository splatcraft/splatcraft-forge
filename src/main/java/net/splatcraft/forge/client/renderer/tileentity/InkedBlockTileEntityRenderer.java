package net.splatcraft.forge.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.handlers.client.PlayerMovementHandler;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

public class InkedBlockTileEntityRenderer extends TileEntityRenderer<InkedBlockTileEntity>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "blocks/inked_block");
    public static final ResourceLocation TEXTURE_GLOWING = new ResourceLocation(Splatcraft.MODID, "blocks/glitter");
    public static final ResourceLocation TEXTURE_PERMANENT = new ResourceLocation(Splatcraft.MODID, "blocks/permanent_ink_overlay");

    protected static InkBlockUtils.InkType type;

    public InkedBlockTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    private static void renderBlock(InkedBlockTileEntity te, BlockRendererDispatcher blockRendererDispatcher, MatrixStack matrixStackIn, IRenderTypeBuffer bufferTypeIn, int combinedLightIn, int combinedOverlayIn)
    {
        type = InkBlockUtils.getInkType(te.getBlockState());
        BlockState blockStateIn = te.getBlockState();
        BlockRenderType blockrendertype = te.getSavedState().getRenderShape();
        if (blockrendertype.equals(BlockRenderType.MODEL))
        {
            blockStateIn = te.getSavedState();
        }

        IBakedModel ibakedmodel = blockRendererDispatcher.getBlockModel(blockStateIn);


        int i = ColorUtils.getInkColor(te);
        float f = (float) (i >> 16 & 255) / 255.0F;
        float f1 = (float) (i >> 8 & 255) / 255.0F;
        float f2 = (float) (i & 255) / 255.0F;

        //f = 0;
        //f1 = 1;
        //f2 = 1;

        renderModel(matrixStackIn.last(), bufferTypeIn, blockStateIn, ibakedmodel, f, f1, f2, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE, te);

    }

    private static void renderModel(MatrixStack.Entry matrixEntry, IRenderTypeBuffer buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData, InkedBlockTileEntity te)
    {
        Random random = new Random();
        long i = 42L;



        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            if(canRenderSide(te, direction))
                renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, direction, random, modelData), combinedLightIn, combinedOverlayIn, te);
        }

        random.setSeed(42L);
        renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, null, random, modelData), combinedLightIn, combinedOverlayIn, te);
    }

    private static boolean canRenderSide(InkedBlockTileEntity te, Direction direction)
    {
        if(te.getLevel() == null)
            return true;

        return !te.getLevel().getBlockState(te.getBlockPos().relative(direction)).isFaceSturdy(te.getLevel(), te.getBlockPos().relative(direction), direction.getOpposite());
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IRenderTypeBuffer buffer, BlockState state, float red, float green, float blue, List<BakedQuad> quads, int combinedLightIn, int combinedOverlayIn, InkedBlockTileEntity te)
    {
        IVertexBuilder builder =  type.equals(InkBlockUtils.InkType.GLOWING) ? buffer.getBuffer(RenderType.translucent()) : buffer.getBuffer(RenderTypeLookup.getRenderType(state, false));

        for (BakedQuad bakedquad : quads)
        {
            float f = MathHelper.clamp(red, 0.0F, 1.0F);
            float f1 = MathHelper.clamp(green, 0.0F, 1.0F);
            float f2 = MathHelper.clamp(blue, 0.0F, 1.0F);

            if(type.equals(InkBlockUtils.InkType.CLEAR))
                builder.putBulkData(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
            else
            {
                putBulkData(builder, Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURE), matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
                if (type.equals(InkBlockUtils.InkType.GLOWING))
                    putBulkData(builder, Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURE_GLOWING), matrixEntry, bakedquad, 1, 1, 1, combinedLightIn, combinedOverlayIn);
            }

            if(Minecraft.getInstance().options.renderDebug && te.getColor() == te.getPermanentColor())
                putBulkData(builder, Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURE_PERMANENT), matrixEntry, bakedquad, 1, 1, 1, combinedLightIn, combinedOverlayIn);
        }

    }

    private static void putBulkData(IVertexBuilder builder, TextureAtlasSprite sprite, MatrixStack.Entry matrixEntry, BakedQuad quad, float r, float g, float b, int combinedLight, int combinedOverlay)
    {
        int[] aint = quad.getVertices();
        Vector3i vector3i = quad.getDirection().getNormal();
        Vector3f vector3f = new Vector3f((float)vector3i.getX(), (float)vector3i.getY(), (float)vector3i.getZ());
        Matrix4f matrix4f = matrixEntry.pose();
        vector3f.transform(matrixEntry.normal());
        int j = aint.length / 8;

        try (MemoryStack memorystack = MemoryStack.stackPush())
        {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getVertexSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();
            VertexData[] vertexArray = new VertexData[j];

            for(int k = 0; k < j; ++k)
            {
                intbuffer.clear();
                intbuffer.put(aint, k * 8, 8);
                float vertexX = bytebuffer.getFloat(0);
                float vertexY = bytebuffer.getFloat(4);
                float vertexZ = bytebuffer.getFloat(8);

                int l = builder.applyBakedLighting(combinedLight, bytebuffer);
                Vector4f vector4f = new Vector4f(vertexX, vertexY, vertexZ, 1.0F);
                vertexArray[k] = new VertexData(vector4f, r, g, b, 1.0F, l, vector3f.x(), vector3f.y(), vector3f.z());
            }

            if (vertexArray.length <= 0)
                return;

            boolean matchesX = true;
            boolean matchesY = true;
            Direction.Axis axis;

            for(int i = 0; i < vertexArray.length-1; i++)
            {
                if(matchesX && vertexArray[i].pos.x() != vertexArray[i+1].pos.x())
                    matchesX = false;
                if(matchesY && vertexArray[i].pos.y() != vertexArray[i+1].pos.y())
                    matchesY = false;
            }

            if(matchesX)
                axis = Direction.Axis.X;
            else if(matchesY)
                axis = Direction.Axis.Y;
            else axis = Direction.Axis.Z;


            for (int k = 0; k < j; ++k)
            {
                VertexData vertex = vertexArray[k];

                float texU = sprite.getU0() + (axis.equals(Direction.Axis.X) ? vertex.pos.z() : vertex.pos.x())*(sprite.getU1()-sprite.getU0());
                float texV = sprite.getV0() + (axis.equals(Direction.Axis.Y) ? vertex.pos.z() : vertex.pos.y())*(sprite.getV1()-sprite.getV0());
                vertex.pos.transform(matrix4f);
                builder.applyBakedNormals(vertex.normal, bytebuffer, matrixEntry.normal());
                builder.vertex(vertex.pos.x(), vertex.pos.y(), vertex.pos.z(), vertex.rgba.x(), vertex.rgba.y(), vertex.rgba.z(), vertex.rgba.w(), texU, texV, combinedOverlay, vertex.lightmapUV, vertex.normal.x(), vertex.normal.y(), vertex.normal.z());
            }
        }

    }

    private static final class VertexData
    {
        final Vector4f pos;
        final Vector4f rgba;
        final int lightmapUV;
        final Vector3f normal;

        VertexData(Vector4f pos, float red, float green, float blue, float alpha, int lightmapUV, float normalX, float normalY, float normalZ)
        {
            this.pos = pos;
            rgba = new Vector4f(red, green, blue, alpha);
            this.lightmapUV = lightmapUV;
            normal = new Vector3f(normalX, normalY, normalZ);
        }
    }

    @Override
    public void render(InkedBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        renderBlock(tileEntityIn, blockRenderer, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        //blockRenderer.renderBlock(SplatcraftBlocks.sardiniumBlock.defaultBlockState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        //blockRenderer.renderBlock(tileEntityIn.getSavedState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }


}
