package net.splatcraft.forge.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ItemEntry
		(
				Item item,
				TagKey<Item> tag
		)
{
	public static final Codec<ItemEntry> CODEC = Codec.STRING.xmap(string -> {
		if(string.indexOf('#') == 0)
			return new ItemEntry(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(string.substring(1))));
		Optional<Item> item = Registry.ITEM.getOptional(new ResourceLocation(string));

		return item.map(ItemEntry::new).orElse(ItemEntry.NONE);
	}, itemEntry -> {
		if(itemEntry.tag != null)
			return '#' + itemEntry.tag.location().toString();
		else if(itemEntry.item != null)
			return itemEntry.item.getRegistryName().toString();
		return "";
	}).stable();
	public static final ItemEntry NONE = new ItemEntry((Item)null);


	public ItemEntry(TagKey<Item> tag)
	{
		this(null, tag);
	}
	public ItemEntry(Item item)
	{
		this(item, null);
	}

	public boolean matches(ItemStack stack)
	{
		return tag == null ? (item != null && stack.is(item)) : stack.is(tag);
	}
}
