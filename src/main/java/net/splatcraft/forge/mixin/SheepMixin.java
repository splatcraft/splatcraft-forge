package net.splatcraft.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
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

@Mixin(Sheep.class)
public class SheepMixin
{
	@WrapOperation(method = "onSheared", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
	public boolean getWool(List<ItemStack> list, Object stack, Operation<Boolean> original)
	{
		Sheep that = (Sheep) (Object) this;
		if (InkOverlayCapability.hasCapability(that))
		{
			int color = InkOverlayCapability.get(that).getWoolColor();
			if (color > -1) {
				return original.call(list, ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool.get()), color), true));
			}
		}
		return original.call(list, stack);
	}


	@WrapOperation(method = "shear", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"))
	public ItemEntity spawnAtLocation(Sheep instance, ItemLike iItemProvider, int i, Operation<ItemEntity> original)
	{
		if(InkOverlayCapability.hasCapability(instance))
		{
			InkOverlayInfo info = InkOverlayCapability.get(instance);
			if (info.getWoolColor() > -1) {
				return original.call(instance, ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool.get()), info.getWoolColor()), true), i);
			}
		}

		return original.call(instance, iItemProvider, i);
	}
}
