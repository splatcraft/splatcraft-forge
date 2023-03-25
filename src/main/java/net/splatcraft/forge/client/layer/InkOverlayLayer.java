package net.splatcraft.forge.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.model.SquidBumperModel;
import net.splatcraft.forge.data.capabilities.inkoverlay.IInkOverlayInfo;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.util.ColorUtils;

import java.util.Arrays;
import java.util.List;

public class InkOverlayLayer<E extends LivingEntity> extends LayerRenderer<E, EntityModel<E>>
{
	private final List<RenderType> BUFFERS = Arrays.asList(
			RenderType.entitySmoothCutout(new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_" + 0 + ".png")),
			RenderType.entitySmoothCutout(new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_" + 1 + ".png")),
			RenderType.entitySmoothCutout(new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_" + 2 + ".png")),
			RenderType.entitySmoothCutout(new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_" + 3 + ".png")),
			RenderType.entitySmoothCutout(new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_" + 4 + ".png"))
	);

	public InkOverlayLayer(IEntityRenderer<E, EntityModel<E>> renderer) {
		super(renderer);
	}

	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, E entity, float v, float v1, float v2, float v3, float v4, float v5)
	{
		int overlay = -1;
		float[] rgb = ColorUtils.hexToRGB(ColorUtils.DEFAULT);

		if (InkOverlayCapability.hasCapability(entity))
		{
			IInkOverlayInfo info = InkOverlayCapability.get(entity);
			rgb = ColorUtils.hexToRGB(info.getColor());
			overlay = (int) (Math.min(info.getAmount() / (entity instanceof SquidBumperEntity ? SquidBumperEntity.maxInkHealth : entity.getMaxHealth()) * 4, 4) - 1);
		}

		if (overlay <= -1)
		{
			return;
		}

		//alex mob coming in clutch
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(BUFFERS.get(overlay));
		this.getParentModel().renderToBuffer(matrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, rgb[0], rgb[1], rgb[2], 1.0F);

		if(getParentModel() instanceof SquidBumperModel)
			((SquidBumperModel) getParentModel()).renderBumper(matrixStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, rgb[0], rgb[1], rgb[2], 1.0F);

		//renderCopyCutoutModel(getParentModel(), MODEL, new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_" + overlay + ".png"), matrixStack, bufferIn, packedLightIn, entity, v, v1, v2, v3, v4, v5, 1, 1, 1);
	}
}
