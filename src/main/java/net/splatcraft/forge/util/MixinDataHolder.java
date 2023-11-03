package net.splatcraft.forge.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;
import net.splatcraft.forge.mixin.BlockRenderMixin;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


//Here to prevent IllegalClassLoadError
public class MixinDataHolder
{
	@OnlyIn(Dist.CLIENT)
	public static class BlockRenderer
	{
		public static final TextureAtlasSprite INKED_BLOCK_SPRITE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(Splatcraft.MODID, "blocks/inked_block"));
		public static final TextureAtlasSprite GLITTER_SPRITE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(Splatcraft.MODID, "blocks/glitter"));
		public static final TextureAtlasSprite PERMANENT_INK_SPRITE = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(Splatcraft.MODID, "blocks/permanent_ink_overlay"));


		public static boolean splatcraft$renderInkedBlock(RenderChunkRegion region, BlockPos pos, VertexConsumer consumer, PoseStack.Pose pose, BakedQuad quad, float[] f0, int[] f1, int f2, boolean f3)
		{
			WorldInk worldInk = WorldInkCapability.get(((BlockRenderMixin.ChunkRegionAccessor)region).getLevel(), pos);

			if(!worldInk.isInked(pos))
				return false;

			WorldInk.Entry ink = worldInk.getInk(pos);

			float[] rgb = ColorUtils.hexToRGB(ink.color());
			TextureAtlasSprite sprite = null;

			if(ink.type() != InkBlockUtils.InkType.CLEAR)
				sprite = MixinDataHolder.BlockRenderer.INKED_BLOCK_SPRITE;

			splatcraft$putBulkData(sprite, consumer, pose, quad, f0, rgb[0], rgb[1], rgb[2], f1, f2, f3);
			if(ink.type() == InkBlockUtils.InkType.GLOWING)
			{
				splatcraft$putBulkData(MixinDataHolder.BlockRenderer.GLITTER_SPRITE, consumer, pose, quad, f0, 1, 1, 1, f1, f2, f3);
			}

			if(Minecraft.getInstance().options.renderDebug && worldInk.hasPermanentInk(pos) && ink.color() == worldInk.getPermanentInk(pos).color())
				splatcraft$putBulkData(MixinDataHolder.BlockRenderer.PERMANENT_INK_SPRITE, consumer, pose, quad, f0, 1, 1, 1, f1, f2, f3);

			return true;
		}

		static void splatcraft$putBulkData(TextureAtlasSprite sprite, VertexConsumer consumer, PoseStack.Pose pose, BakedQuad bakedQuad, float[] p_85998_, float r, float g, float b, int[] p_86002_, int p_86003_, boolean p_86004_)
		{
			float[] afloat = new float[]{p_85998_[0], p_85998_[1], p_85998_[2], p_85998_[3]};
			int[] aint = new int[]{p_86002_[0], p_86002_[1], p_86002_[2], p_86002_[3]};
			int[] aint1 = bakedQuad.getVertices();
			Vec3i vec3i = bakedQuad.getDirection().getNormal();
			Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			Matrix4f matrix4f = pose.pose();
			vector3f.transform(pose.normal());
			int i = 8;
			int j = aint1.length / 8;
			MemoryStack memorystack = MemoryStack.stackPush();

			try {
				ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
				IntBuffer intbuffer = bytebuffer.asIntBuffer();

				for(int k = 0; k < j; ++k) {
					intbuffer.clear();
					intbuffer.put(aint1, k * 8, 8);
					float f = bytebuffer.getFloat(0);
					float f1 = bytebuffer.getFloat(4);
					float f2 = bytebuffer.getFloat(8);
					float f3;
					float f4;
					float f5;
					if (p_86004_) {
						float f6 = (float)(bytebuffer.get(12) & 255) / 255.0F;
						float f7 = (float)(bytebuffer.get(13) & 255) / 255.0F;
						float f8 = (float)(bytebuffer.get(14) & 255) / 255.0F;
						f3 = f6 * afloat[k] * r;
						f4 = f7 * afloat[k] * g;
						f5 = f8 * afloat[k] * b;
					} else {
						f3 = afloat[k] * r;
						f4 = afloat[k] * g;
						f5 = afloat[k] * b;
					}

					int l = consumer.applyBakedLighting(p_86002_[k], bytebuffer);
					float f9 = bytebuffer.getFloat(16);
					float f10 = bytebuffer.getFloat(20);
					Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);

					Direction.Axis axis = bakedQuad.getDirection().getAxis(); //Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(Splatcraft.MODID, "blocks/inked_block"));

					float texU = sprite == null ? f9 : sprite.getU0() + (axis.equals(Direction.Axis.X) ? vector4f.z() : vector4f.x())*(sprite.getU1()-sprite.getU0());
					float texV = sprite == null ? f10 : sprite.getV0() + (axis.equals(Direction.Axis.Y) ? vector4f.z() : vector4f.y())*(sprite.getV1()-sprite.getV0());

					vector4f.transform(matrix4f);
					consumer.applyBakedNormals(vector3f, bytebuffer, pose.normal());
					consumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), f3, f4, f5, 1.0F, texU, texV, p_86003_, l, vector3f.x(), vector3f.y(), vector3f.z());
				}
			} catch (Throwable throwable1) {
				if (memorystack != null) {
					try {
						memorystack.close();
					} catch (Throwable throwable) {
						throwable1.addSuppressed(throwable);
					}
				}

				throw throwable1;
			}

			if (memorystack != null) {
				memorystack.close();
			}

		}
	}
}
