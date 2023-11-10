package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.client.handlers.RendererHandler;
import net.splatcraft.forge.registries.SplatcraftItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin
{
	@WrapOperation(method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
	public void onRenderGuiItem(ItemRenderer renderer, ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHanded, PoseStack poseStack, MultiBufferSource source, int light, int overlay, BakedModel modelIn, Operation<Void> original)
	{
		if (stack.getItem().equals(SplatcraftItems.powerEgg.get())) {
			BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(stack.getItem().getRegistryName() + "#inventory"));
			RendererHandler.renderItem(stack, transformType, true, poseStack, source, light, overlay, model);
		} else {
			original.call(renderer, stack, transformType, leftHanded, poseStack, source, light, overlay, modelIn);
		}
	}
}
