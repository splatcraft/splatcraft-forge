package net.splatcraft.forge.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.Serializer;

import java.util.ArrayList;
import java.util.List;

public class BlueprintLootFunction implements LootItemFunction
{
	final List<String> advancementIds;

	public BlueprintLootFunction(List<String> advancements)
	{
		advancementIds = advancements;
	}

	@Override
	public LootItemFunctionType getType() {
		return null;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext lootContext)
	{
		ListTag listtag = new ListTag();

		this.advancementIds.stream().map(StringTag::valueOf).forEach(listtag::add);



		stack.getOrCreateTag().put("Advancements", listtag);
		return stack;
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlueprintLootFunction>
	{
		@Override
		public void serialize(JsonObject json, BlueprintLootFunction lootFunction, JsonSerializationContext context)
		{
			JsonArray array = new JsonArray();

			lootFunction.advancementIds.forEach(array::add);

			json.add("advancements", array);
		}
		@Override
		public BlueprintLootFunction deserialize(JsonObject json, JsonDeserializationContext context)
		{
			JsonArray array = GsonHelper.getAsJsonArray(json, "advancements");
			List<String> advancements = new ArrayList<>();

			array.forEach(element -> advancements.add(element.getAsString()));

			return new BlueprintLootFunction(advancements);
		}
	}
}
