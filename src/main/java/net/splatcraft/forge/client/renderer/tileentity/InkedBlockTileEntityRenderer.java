package net.splatcraft.forge.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.mixin.BlockRenderMixin;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class InkedBlockTileEntityRenderer extends TileEntityRenderer<InkedBlockTileEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "blocks/inked_block");
    public static final ArrayList<ResourceLocation> TEXTURES = new ArrayList<>();
    public static final ResourceLocation TEXTURE_GLOWING = new ResourceLocation(Splatcraft.MODID, "blocks/glitter");
    public static final ArrayList<ResourceLocation> TEXTURES_GLOWING = new ArrayList<>();
    public static final ResourceLocation TEXTURE_PERMANENT = new ResourceLocation(Splatcraft.MODID, "blocks/permanent_ink_overlay");

    public static final Random random = new Random();

    static {
        int i = 1;
        TEXTURES.add(TEXTURE);
        while (Minecraft.getInstance().getResourceManager().hasResource(new ResourceLocation(Splatcraft.MODID, "textures/blocks/inked_block" + i + ".png")))
            TEXTURES.add(new ResourceLocation(Splatcraft.MODID, "blocks/inked_block" + (i++)));
        i = 1;
        TEXTURES_GLOWING.add(TEXTURE_GLOWING);
        while (Minecraft.getInstance().getResourceManager().hasResource(new ResourceLocation(Splatcraft.MODID, "textures/blocks/glitter" + i + ".png")))
            TEXTURES_GLOWING.add(new ResourceLocation(Splatcraft.MODID, "blocks/glitter" + (i++)));
    }

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


        int color = ColorUtils.getInkColor(te);
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        //TileEntityMerger.ICallbackWrapper<? extends InkedBlockTileEntity> icallbackwrapper = TileEntityMerger.ICallback::acceptNone;
        //combinedLightIn = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(combinedLightIn);

        renderModel(matrixStackIn.last(), bufferTypeIn, blockStateIn, ibakedmodel, f, f1, f2, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE, te);

    }

    private static void renderModel(MatrixStack.Entry matrixEntry, IRenderTypeBuffer buffer, @Nullable BlockState state, IBakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData, InkedBlockTileEntity te) {
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            if (shouldRenderSide(te, direction))
                renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, direction, random, modelData), combinedLightIn, combinedOverlayIn, te);
        }

        random.setSeed(42L);
        if (shouldRenderSide(te, null))
            renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, null, random, modelData), combinedLightIn, combinedOverlayIn, te);
    }

    private static boolean shouldRenderSide(InkedBlockTileEntity te, Direction direction) {
        if (te.getLevel() == null)
            return false;

        BlockPos tePos = te.getBlockPos();

        Vector3f lookVec = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
        Vector3d blockVec = Vector3d.atBottomCenterOf(tePos).add(lookVec.x(), 0.0, lookVec.z());

        Vector3d directionVec3d = blockVec.subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()).normalize();
        Vector3f directionVec = new Vector3f((float) directionVec3d.x, (float) directionVec3d.y, (float) directionVec3d.z);
        if (lookVec.dot(directionVec) > 0) {
            if (direction == null) return true;
            BlockState relative = te.getLevel().getBlockState(tePos.relative(direction));
            return !relative.getMaterial().isSolidBlocking() || !relative.isCollisionShapeFullBlock(te.getLevel(), tePos.relative(direction));
        }

        return false;
    }

    private static void renderModelBrightnessColorQuads(MatrixStack.Entry matrixEntry, IRenderTypeBuffer buffer, BlockState state, float red, float green, float blue, List<BakedQuad> quads, int combinedLightIn, int combinedOverlayIn, InkedBlockTileEntity te) {
        IVertexBuilder builder = type.equals(InkBlockUtils.InkType.GLOWING) ? buffer.getBuffer(RenderType.translucent()) : buffer.getBuffer(RenderTypeLookup.getRenderType(state, false));
        BitSet bitset = new BitSet(3);
        float[] afloat = new float[Direction.values().length * 2];

        float f = MathHelper.clamp(red, 0.0F, 1.0F);
        float f1 = MathHelper.clamp(green, 0.0F, 1.0F);
        float f2 = MathHelper.clamp(blue, 0.0F, 1.0F);

        int[] combinedLights = new int[]{combinedLightIn};
        float[] brightness = new float[]{1};

        for (BakedQuad bakedquad : quads) {
            if (te.getLevel() != null && Minecraft.useAmbientOcclusion()) {
                BlockModelRenderer.AmbientOcclusionFace ambientOcclusionFace =
                        Minecraft.getInstance().getBlockRenderer().getModelRenderer().new AmbientOcclusionFace();

                ((BlockRenderMixin.ModelRenderer) Minecraft.getInstance().getBlockRenderer().getModelRenderer())
                        .invokeCalculateShape(te.getLevel(), te.getSavedState(), te.getBlockPos(), bakedquad.getVertices(), bakedquad.getDirection(), afloat, bitset);
                ambientOcclusionFace.calculate(te.getLevel(), te.getSavedState(), te.getBlockPos(), bakedquad.getDirection(), afloat, bitset, bakedquad.isShade());

                int[] aoLights = ((BlockRenderMixin.AOFace)ambientOcclusionFace).getLightmap();
                float[] aoBrightness = ((BlockRenderMixin.AOFace)ambientOcclusionFace).getBrightness();

                combinedLights = new int[]{aoLights[0], aoLights[1], aoLights[2], aoLights[3]};
                brightness = new float[]{aoBrightness[0], aoBrightness[1], aoBrightness[2], aoBrightness[3]};
            }

            if (type.equals(InkBlockUtils.InkType.CLEAR))
                builder.putBulkData(matrixEntry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
            else {
                BlockPos pos = te.getBlockPos();
                random.setSeed(Long.parseLong((Math.signum(pos.getX()) > 0 ? "1" : "0") + (Math.signum(pos.getY()) > 0 ? "1" : "0") + (Math.signum(pos.getZ()) > 0 ? "1" : "0")
                        + (Math.abs(pos.getX()) % Integer.MAX_VALUE) + "" + (Math.abs(pos.getY()) % Integer.MAX_VALUE) + "" + (Math.abs(pos.getZ()) % Integer.MAX_VALUE) + ""));
                random.setSeed(random.nextLong());

                putBulkData(builder, Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURES.get(random.nextInt(TEXTURES.size()))), matrixEntry, bakedquad, f, f1, f2, brightness, combinedLights, combinedOverlayIn);
                if (type.equals(InkBlockUtils.InkType.GLOWING))
                    putBulkData(builder, Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURES_GLOWING.get(random.nextInt(TEXTURES_GLOWING.size()))), matrixEntry, bakedquad, 1, 1, 1, brightness, combinedLights, combinedOverlayIn);
            }

            if(Minecraft.getInstance().options.renderDebug && te.getColor() == te.getPermanentColor())
                putBulkData(builder, Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURE_PERMANENT), matrixEntry, bakedquad, 1, 1, 1, brightness, combinedLights, combinedOverlayIn);
        }

    }

    private static void putBulkData(IVertexBuilder builder, TextureAtlasSprite sprite, MatrixStack.Entry matrixEntry, BakedQuad quad, float r, float g, float b, float[] brightness, int[] combinedLights, int combinedOverlay)
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

                float r1 = brightness[k % brightness.length] * r;
                float g1 = brightness[k % brightness.length] * g;
                float b1 = brightness[k % brightness.length] * b;

                int l = builder.applyBakedLighting(combinedLights[k % combinedLights.length], bytebuffer);
                Vector4f vector4f = new Vector4f(vertexX, vertexY, vertexZ, 1.0F);
                vertexArray[k] = new VertexData(vector4f, r1, g1, b1, 1.0F, l, vector3f.x(), vector3f.y(), vector3f.z());
            }

            if (vertexArray.length == 0)
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
