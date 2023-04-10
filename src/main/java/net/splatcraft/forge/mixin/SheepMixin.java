package net.splatcraft.forge.mixin;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Sheep.class)
public class SheepMixin
{
	@Redirect(method = "onSheared", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
	public <E> boolean getWool(List instance, E e)
	{
		if(InkOverlayCapability.hasCapability((Sheep)(Object)this))
		{
			int color = InkOverlayCapability.get((Sheep)(Object)this).getWoolColor();
			if(color > -1)
				return instance.add(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool.get()), color), true));
		}
		return instance.add(e);
	}


	@Redirect(method = "shear", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"))
	public ItemEntity spawnAtLocation(Sheep instance, ItemLike iItemProvider, int i)
	{
		if(InkOverlayCapability.hasCapability(instance))
		{
			InkOverlayInfo info = InkOverlayCapability.get(instance);
			if(info.getWoolColor() > -1)
				return instance.spawnAtLocation(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool.get()), info.getWoolColor()), true), i);
		}

		return instance.spawnAtLocation(iItemProvider, i);
	}
}
