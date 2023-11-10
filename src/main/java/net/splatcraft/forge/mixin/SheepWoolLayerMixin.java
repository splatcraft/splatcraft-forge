package net.splatcraft.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@OnlyIn(Dist.CLIENT)
@Mixin(SheepFurLayer.class)
public abstract class SheepWoolLayerMixin extends RenderLayer<Sheep, SheepModel<Sheep>>
{
	public SheepWoolLayerMixin(RenderLayerParent<Sheep, SheepModel<Sheep>> p_117346_) {
		super(p_117346_);
	}

	@Redirect(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Sheep;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/SheepFurLayer;coloredCutoutModelCopyLayerRender(Lnet/minecraft/client/model/EntityModel;Lnet/minecraft/client/model/EntityModel;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFFFFF)V"))
	public void render(EntityModel<LivingEntity> parentModel, EntityModel<LivingEntity> entityModel, ResourceLocation resourceLocation, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, LivingEntity entity, float v1, float v2, float v3, float v4, float v5, float v6, float r, float g, float b)
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
