package com.cibernet.splatcraft.crafting;

import com.cibernet.splatcraft.Splatcraft;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import netscape.javascript.JSException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class InkColorManager extends JsonReloadListener
{
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private Map<ResourceLocation, InkColor> colors = ImmutableMap.of();
	private Map<Integer, InkColor> colorsByHex = ImmutableMap.of();
	private boolean someRecipesErrored;
	
	public static InkColorManager instance = new InkColorManager();
	
	
	public InkColorManager()
	{
		super(GSON, "ink_colors");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> splashList, IResourceManager iResourceManager, IProfiler iProfiler)
	{
		this.someRecipesErrored = false;
		Map<ResourceLocation, InkColor> map = Maps.newHashMap();
		Map<Integer, InkColor> hexMap = Maps.newHashMap();
		
		for(Map.Entry<ResourceLocation, JsonElement> entry : splashList.entrySet())
		{
			ResourceLocation resourcelocation = entry.getKey();
			if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			
			try {
				if (entry.getValue().isJsonObject() && !net.minecraftforge.common.crafting.CraftingHelper.processConditions(entry.getValue().getAsJsonObject(), "conditions")) {
					LOGGER.debug("Skipping loading color {} as it's conditions were not met", resourcelocation);
					continue;
				}
				InkColor color = deserializeColor(resourcelocation, JSONUtils.getJsonObject(entry.getValue(), "top element"));
				if (color == null) {
					LOGGER.info("Skipping loading color {} as it's serializer returned null", resourcelocation);
					continue;
				}
				map.put(resourcelocation, color);
				hexMap.put(color.getColor(), color);
			} catch (IllegalArgumentException | JsonParseException jsonparseexception) {
				LOGGER.error("Parsing error loading color {}", resourcelocation, jsonparseexception);
			}
		}
		
		this.colors = map;
		this.colorsByHex = hexMap;
		LOGGER.info("Loaded {} ink colors", (int)map.size());
	}
	
	/**
	 * Deserializes an ink color object from json data.
	 *
	 * @param id The identifier for the color being read.
	 * @param json The data from the json file.
	 */
	public static InkColor deserializeColor(ResourceLocation id, JsonObject json)
	{
		
		String name = JSONUtils.getString(json, "name");
		int code;
		DyeColor dye = null;
		int mapColor = MaterialColor.CLAY.colorIndex;
		
		
		code = Integer.parseInt(JSONUtils.getString(json, "hex"),16);
		
		try { dye = DyeColor.byTranslationKey(JSONUtils.getString(json, "dye"), null);}
		catch(JsonSyntaxException e) {}
		
		try { mapColor = JSONUtils.getInt(json, "map_color"); }
		catch(JsonSyntaxException e) {}
		
		return new InkColor(name, code, mapColor, dye);
	}
	
	public InkColor getColorByHex(int hex)
	{
		return colorsByHex.get(hex);
	}
	
	@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Subscriber
	{
		@SubscribeEvent
		public static void addListener(AddReloadListenerEvent event)
		{
			event.addListener(instance);
		}
	}
}
