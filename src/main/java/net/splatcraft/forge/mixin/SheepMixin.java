package net.splatcraft.forge.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.data.capabilities.inkoverlay.IInkOverlayInfo;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SheepEntity.class)
public class SheepMixin
{
	@Redirect(method = "onSheared", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
	public <E> boolean getWool(List instance, E e)
	{
		PlayerInfo

		if(InkOverlayCapability.hasCapability((SheepEntity)(Object)this))
		{
			int color = InkOverlayCapability.get((SheepEntity)(Object)this).getWoolColor();
			if(color > -1)
				return instance.add(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool), color));
		}
		return instance.add(e);
	}


	@Redirect(method = "shear", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;spawnAtLocation(Lnet/minecraft/util/IItemProvider;I)Lnet/minecraft/entity/item/ItemEntity;"))
	public ItemEntity spawnAtLocation(SheepEntity instance, IItemProvider iItemProvider, int i)
	{
		if(InkOverlayCapability.hasCapability(instance))
		{
			IInkOverlayInfo info = InkOverlayCapability.get(instance);
			if(info.getWoolColor() > -1)
				return instance.spawnAtLocation(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool), info.getWoolColor()), i);
		}

		return instance.spawnAtLocation(iItemProvider, i);
	}

	@Mixin(SheepWoolLayer.class)
	@OnlyIn(Dist.CLIENT)
	public static abstract class WoolLayer extends LayerRenderer<SheepEntity, SheepModel<SheepEntity>>
	{

		public WoolLayer() {
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

}
