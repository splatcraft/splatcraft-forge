package net.splatcraft.forge.mixin;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.crafting.SplatcraftRecipeTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class RecipeCategoryMixin
{
	@OnlyIn(Dist.CLIENT)
	@Inject(method = "getCategory", at = @At("HEAD"), cancellable = true)
	private static void getCategory(Recipe<?> itemstack, CallbackInfoReturnable<RecipeBookCategories> cir)
	{
		RecipeType<?> type = itemstack.getType();
		if(type == SplatcraftRecipeTypes.INK_VAT_COLOR_CRAFTING_TYPE || type == SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE
			|| type == SplatcraftRecipeTypes.WEAPON_STATION_TYPE)
		{
			cir.setReturnValue(RecipeBookCategories.UNKNOWN);
			cir.cancel();
		}
	}
}
