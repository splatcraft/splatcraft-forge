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
		if(InkOverlayCapability.hasCapability((SheepEntity)(Object)this))
		{
			int color = InkOverlayCapability.get((SheepEntity)(Object)this).getWoolColor();
			if(color > -1)
				return instance.add(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool), color), true));
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
				return instance.spawnAtLocation(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool), info.getWoolColor()), true), i);
		}

		return instance.spawnAtLocation(iItemProvider, i);
	}
}
