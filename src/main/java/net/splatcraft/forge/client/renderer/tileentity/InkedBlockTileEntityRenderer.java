package net.splatcraft.forge.client.renderer.tileentity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.mixin.BlockRenderMixin;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class InkedBlockTileEntityRenderer implements BlockEntityRenderer<InkedBlockTileEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Splatcraft.MODID, "blocks/inked_block");
    public static final ArrayList<ResourceLocation> TEXTURES = new ArrayList<>();
    public static final ResourceLocation TEXTURE_GLOWING = new ResourceLocation(Splatcraft.MODID, "blocks/glitter");
    public static final ArrayList<ResourceLocation> TEXTURES_GLOWING = new ArrayList<>();
    public static final ResourceLocation TEXTURE_PERMANENT = new ResourceLocation(Splatcraft.MODID, "blocks/permanent_ink_overlay");

    public static final Random random = new Random();

    public InkedBlockTileEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

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

    private static void renderBlock(InkedBlockTileEntity te, BlockRenderDispatcher blockRendererDispatcher, PoseStack matrixStackIn, MultiBufferSource bufferTypeIn, int combinedLightIn, int combinedOverlayIn)
    {
        type = InkBlockUtils.getInkType(te.getBlockState());
        BlockState blockStateIn = te.getBlockState();
        RenderShape blockrendertype = te.getSavedState().getRenderShape();
        if (blockrendertype.equals(RenderShape.MODEL))
        {
            blockStateIn = te.getSavedState();
        }

        BakedModel ibakedmodel = blockRendererDispatcher.getBlockModel(blockStateIn);


        int color = ColorUtils.getInkColor(te);
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        //TileEntityMerger.ICallbackWrapper<? extends InkedBlockTileEntity> icallbackwrapper = TileEntityMerger.ICallback::acceptNone;
        //combinedLightIn = icallbackwrapper.apply(new DualBrightnessCallback<>()).applyAsInt(combinedLightIn);

        renderModel(matrixStackIn.last(), bufferTypeIn, blockStateIn, ibakedmodel, f, f1, f2, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE, te);

    }

    private static void renderModel(PoseStack.Pose matrixEntry, MultiBufferSource buffer, @Nullable BlockState state, BakedModel modelIn, float red, float green, float blue, int combinedLightIn, int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData, InkedBlockTileEntity te) {
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            if (ClientUtils.shouldRenderSide(te, direction))
                renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, direction, random, modelData), combinedLightIn, combinedOverlayIn, te);
        }

        random.setSeed(42L);
        if (ClientUtils.shouldRenderSide(te, null))
            renderModelBrightnessColorQuads(matrixEntry, buffer, state, red, green, blue, modelIn.getQuads(state, null, random, modelData), combinedLightIn, combinedOverlayIn, te);
    }

    private static void renderModelBrightnessColorQuads(PoseStack.Pose matrixEntry, MultiBufferSource buffer, BlockState state, float red, float green, float blue, List<BakedQuad> quads, int combinedLightIn, int combinedOverlayIn, InkedBlockTileEntity te) {
        VertexConsumer builder = type.equals(InkBlockUtils.InkType.GLOWING) ? buffer.getBuffer(RenderType.translucent()) : buffer.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        BitSet bitset = new BitSet(3);
        float[] afloat = new float[Direction.values().length * 2];

        float f = Mth.clamp(red, 0.0F, 1.0F);
        float f1 = Mth.clamp(green, 0.0F, 1.0F);
        float f2 = Mth.clamp(blue, 0.0F, 1.0F);

        int[] combinedLights = new int[]{combinedLightIn};
        float[] brightness = new float[]{1};

        for (BakedQuad bakedquad : quads) {
            if (te.getLevel() != null && Minecraft.useAmbientOcclusion()) {
                ModelBlockRenderer.AmbientOcclusionFace ambientOcclusionFace =
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

                putBulkData(builder, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TEXTURES.get(random.nextInt(TEXTURES.size()))), matrixEntry, bakedquad, f, f1, f2, brightness, combinedLights, combinedOverlayIn);
                if (type.equals(InkBlockUtils.InkType.GLOWING))
                    putBulkData(builder, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TEXTURES_GLOWING.get(random.nextInt(TEXTURES_GLOWING.size()))), matrixEntry, bakedquad, 1, 1, 1, brightness, combinedLights, combinedOverlayIn);
            }

            if(Minecraft.getInstance().options.renderDebug && te.getColor() == te.getPermanentColor())
                putBulkData(builder, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TEXTURE_PERMANENT), matrixEntry, bakedquad, 1, 1, 1, brightness, combinedLights, combinedOverlayIn);
        }

    }

    private static void putBulkData(VertexConsumer builder, TextureAtlasSprite sprite, PoseStack.Pose matrixEntry, BakedQuad quad, float r, float g, float b, float[] brightness, int[] combinedLights, int combinedOverlay)
    {
        int[] aint = quad.getVertices();
        Vec3i vector3i = quad.getDirection().getNormal();
        Vector3f vector3f = new Vector3f((float)vector3i.getX(), (float)vector3i.getY(), (float)vector3i.getZ());
        Matrix4f matrix4f = matrixEntry.pose();
        vector3f.transform(matrixEntry.normal());
        int j = aint.length / 8;

        try (MemoryStack memorystack = MemoryStack.stackPush())
        {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
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
    public void render(InkedBlockTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        renderBlock(tileEntityIn, blockRenderer, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        //blockRenderer.renderBlock(SplatcraftBlocks.sardiniumBlock.defaultBlockState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        //blockRenderer.renderBlock(tileEntityIn.getSavedState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
