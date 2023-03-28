package net.splatcraft.forge.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@OnlyIn(Dist.CLIENT)
@Mixin(SheepWoolLayer.class)
public abstract class SheepWoolLayerMixin extends LayerRenderer<SheepEntity, SheepModel<SheepEntity>>
{

	public SheepWoolLayerMixin() {
		super(null);
	}

	@Redirect(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/SheepWoolLayer;coloredCutoutModelCopyLayerRender(Lnet/minecraft/client/renderer/entity/model/EntityModel;Lnet/minecraft/client/renderer/entity/model/EntityModel;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V"))
	public void render(EntityModel parentModel, EntityModel entityModel, ResourceLocation resourceLocation, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, LivingEntity entity, float v1, float v2, float v3, float v4, float v5, float v6, float r, float g, float b)
	{
		float[] rgb = new float[] {r, g, b};

		if(InkOverlayCapability.hasCapability(entity))
		{
			int color = InkOverlayCapability.get(entity).getWoolColor();

			if(color >= 0)
				rgb = ColorUtils.hexToRGB(color);
		}

		coloredCutoutModelCopyLayerRender(parentModel, entityModel, resourceLocation, matrixStack, iRenderTypeBuffer, i, entity, v1, v2, v3, v4, v5, v6, rgb[0], rgb[1], rgb[2]);
	}
}
