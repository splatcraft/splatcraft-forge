package net.splatcraft.forge.mixin.itemrendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.client.handlers.RendererHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin
{
	private ItemFrame entity;
	private float deltaTick;

	@Inject(method = "render*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
	public void capture(ItemFrame frame, float entityYaw, float deltaTick, PoseStack poseStack, MultiBufferSource source, int light, CallbackInfo ci)
	{
		this.entity = frame;
		this.deltaTick = deltaTick;
	}

	@Redirect(method = "render*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
	public void fireRenderItemFrameItem(ItemRenderer renderer, ItemStack stack, ItemTransforms.TransformType transformType, int light, int overlay, PoseStack poseStack, MultiBufferSource source, int id)
	{
		if(!RendererHandler.renderSubWeapon(stack, transformType, poseStack, source, light, deltaTick))
			renderer.renderStatic(stack, transformType, light, overlay, poseStack, source, entity.getId());
	}
}
