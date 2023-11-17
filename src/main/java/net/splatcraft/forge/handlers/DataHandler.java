package net.splatcraft.forge.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.items.weapons.settings.*;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateWeaponSettingsPacket;
import net.splatcraft.forge.registries.SplatcraftInkColors;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber
public class DataHandler
{
	public static final WeaponStatsListener WEAPON_STATS_LISTENER = new WeaponStatsListener();
	public static final InkColorTagsListener INK_COLOR_TAGS_LISTENER = new InkColorTagsListener();
	@SubscribeEvent
	public static void addReloadListener(AddReloadListenerEvent event)
	{
		event.addListener(WEAPON_STATS_LISTENER);
		event.addListener(INK_COLOR_TAGS_LISTENER);
	}

	@SubscribeEvent
	public static void onDataSync(OnDatapackSyncEvent event)
	{

		SplatcraftPacketHandler.sendToAll(new UpdateWeaponSettingsPacket());
	}

	public static class InkColorTag
	{
		private final List<Integer> list;

		public InkColorTag(List<Integer> list) {
			this.list = list;
		}

		public void clear()
		{
			list.clear();
		}

		public void addAll(Collection<Integer> values)
		{
			list.addAll(values);
		}

		public int getRandom(Random random)
		{
			return list.isEmpty() ? SplatcraftInkColors.undyed.getColor() : list.get(random.nextInt(list.size()));
		}

		public List<Integer> getAll()
		{
			return new ArrayList<>(list);
		}
	}

	public static class InkColorTagsListener extends SimpleJsonResourceReloadListener
	{
		private static final HashMap<ResourceLocation, InkColorTag> REGISTRY = new HashMap<>();

		public static final InkColorTag STARTER_COLORS = registerTag(new ResourceLocation(Splatcraft.MODID, "starter_colors"));

		private static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();
		private static final String folder = "tags/ink_colors";

		public InkColorTagsListener() {
			super(GSON_INSTANCE, folder);
		}

		public static InkColorTag registerTag(ResourceLocation name)
		{
			InkColorTag result = new InkColorTag(new ArrayList<>());

			REGISTRY.put(name, result);

			return result;
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
		{
			REGISTRY.forEach((key, tag) ->
			{
				if(resourceList.containsKey(key))
				{
					JsonObject json = resourceList.get(key).getAsJsonObject();

					if(GsonHelper.getAsBoolean(json, "replace", false))
						tag.clear();

					tag.addAll(StreamSupport.stream(GsonHelper.getAsJsonArray(json, "values").spliterator(), false).map(jsonElement ->
					{
						if(GsonHelper.isNumberValue(jsonElement))
							return jsonElement.getAsInt();
						else
						{
							String str = jsonElement.getAsString();
							if(str.indexOf('#') == 0)
								return Integer.parseInt(str);
							else
							{
								ResourceLocation loc = new ResourceLocation(str);
								if(SplatcraftInkColors.REGISTRY.get().containsKey(loc))
									return SplatcraftInkColors.REGISTRY.get().getValue(loc).getColor();
							}
							return -1;
						}
					}).filter(i -> i >= 0 && i <= 0xFFFFFF).toList());
				}
			});

		}
	}

	public static class WeaponStatsListener extends SimpleJsonResourceReloadListener
	{
		public static final HashMap<String, Class<? extends AbstractWeaponSettings<?, ?>>> SETTING_TYPES = new HashMap<>()
		{{
			put(Splatcraft.MODID+":shooter", ShooterWeaponSettings.class);
			put(Splatcraft.MODID+":main", WeaponSettings.class);
			put(Splatcraft.MODID+":blaster", BlasterWeaponSettings.class);
			put(Splatcraft.MODID+":roller", RollerWeaponSettings.class);
			put(Splatcraft.MODID+":charger", ChargerWeaponSettings.class);
			put(Splatcraft.MODID+":slosher", SlosherWeaponSettings.class);
			put(Splatcraft.MODID+":sub_weapon", SubWeaponSettings.class);
		}}; //TODO make better registry probably
		public static final  HashMap<ResourceLocation, AbstractWeaponSettings<?, ?>> SETTINGS = new HashMap<>();

		private static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();
		private static final String folder = "weapon_settings";

		public WeaponStatsListener() {
			super(GSON_INSTANCE, folder);
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
		{
			SETTINGS.clear();

			resourceList.forEach((key, element) ->
			{
				JsonObject json = element.getAsJsonObject();
				try
				{
					String type = GsonHelper.getAsString(json, "type");
					if(!SETTING_TYPES.containsKey(type))
						return;

					AbstractWeaponSettings<?, ?> settings = SETTING_TYPES.get(type).getConstructor(String.class).newInstance(key.toString());
					settings.getCodec().parse(JsonOps.INSTANCE, json).resultOrPartial(msg -> System.out.println("Failed to load weapon settings for " + key + ": " + msg)).ifPresent(
							settings::castAndDeserialize
					);

					settings.registerStatTooltips();
					SETTINGS.put(key, settings);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				         NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
