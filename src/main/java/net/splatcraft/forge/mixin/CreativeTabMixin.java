package net.splatcraft.forge.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeTabMixin implements AbstractContainerAccessor<CreativeModeInventoryScreen.ItemPickerMenu> {
	@Shadow private static int selectedTab;

	@Shadow private EditBox searchBox;

	@Shadow private float scrollOffs;

	@Inject(cancellable = true, method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;fillItemList(Lnet/minecraft/core/NonNullList;)V", shift = At.Shift.AFTER))
	private void applySearchResults(CallbackInfo ci)
	{
		CreativeModeTab tab = CreativeModeTab.TABS[selectedTab];
		String searchValue = searchBox.getValue().toLowerCase();
		if(tab == SplatcraftItemGroups.GROUP_COLORS && !searchValue.isEmpty())
		{
			CreativeModeInventoryScreen.ItemPickerMenu menu = this.getMenu();

			String invertedStr = ChatFormatting.stripFormatting(new TranslatableComponent("ink_color.invert", "%s").getString()).toLowerCase(Locale.ROOT);
			boolean inverted = false;

			if(!invertedStr.isEmpty())
			{
				int argIndex = invertedStr.indexOf("%s");
				String invertedStrSuffix = invertedStr.substring(argIndex + "%s".length());

				inverted = searchValue.startsWith(invertedStr.substring(0, argIndex)) && searchValue.endsWith(invertedStrSuffix);

				if (inverted)
					searchValue = searchValue.substring(argIndex, searchValue.length() - invertedStrSuffix.length());
			}

			if(!inverted && (searchValue.startsWith("!") || searchValue.startsWith("-")))
			{
				searchValue = searchValue.substring(1);
				inverted = true;
			}

			if(searchValue.indexOf('#') == 0)
			{
				try
				{
					searchValue = searchValue.substring(1);
					int color = searchValue.isEmpty() ? 0 : Mth.clamp(Integer.parseInt(searchValue, 16), 0, 0xFFFFFF);

					menu.items.clear();
					for(Item item : SplatcraftItemGroups.colorTabItems)
						menu.items.add(ColorUtils.setColorLocked(ColorUtils.setInverted(ColorUtils.setInkColor(new ItemStack(item), color), inverted), true));

					splatcraft$endSearchResults(ci, menu);
				} catch (NumberFormatException ignored){}
			}
			else
			{
				List<Integer> colors = new ArrayList<>();
				boolean findExact = !searchValue.isEmpty() && searchValue.indexOf('.') == searchValue.length()-1;
				if(findExact)
					searchValue = searchValue.substring(0, searchValue.length()-1);

				for(InkColor color : SplatcraftInkColors.REGISTRY.get())
				{
					String name = ChatFormatting.stripFormatting(color.getLocalizedName().getString()).toLowerCase(Locale.ROOT);

					if(findExact ? (color.getRegistryName().toString().equals(searchValue) || name.equals(searchValue)) :
					(color.getRegistryName().toString().contains(searchValue) || name.contains(searchValue)))
						colors.add(color.getColor());
				}

				if(colors.isEmpty())
				{
					try {
						colors.add(Mth.clamp(Integer.parseInt(searchValue), 0, 0xFFFFFF));
					} catch (NumberFormatException ignored) {}
				}

				if(!colors.isEmpty())
				{
					menu.items.clear();
					for(Item item : SplatcraftItemGroups.colorTabItems)
						for(int color : colors)
							menu.items.add(ColorUtils.setColorLocked(ColorUtils.setInverted(ColorUtils.setInkColor(new ItemStack(item), color), inverted), true));

					splatcraft$endSearchResults(ci, menu);
				}
			}
		}
	}

	@Unique
	private static final Component splatcraft$label = new TranslatableComponent("itemGroup.splatcraft_colors.label");
	@ModifyArg(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"), index = 1)
	private Component injectTabLabel(Component original)
	{
		return CreativeModeTab.TABS[selectedTab] == SplatcraftItemGroups.GROUP_COLORS ? splatcraft$label : original;
	}


	@Unique
	private void splatcraft$endSearchResults(CallbackInfo ci, CreativeModeInventoryScreen.ItemPickerMenu menu)
	{
		scrollOffs = 0;
		menu.scrollTo(0);
		ci.cancel();
	}
}
