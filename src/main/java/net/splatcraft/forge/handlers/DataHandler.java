package net.splatcraft.forge.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ReloadCommand;
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
import net.splatcraft.forge.items.weapons.settings.AbstractWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.RollerWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.SubWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;
import net.splatcraft.forge.network.s2c.UpdateWeaponSettingsPacket;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class DataHandler
{
	public static final WeaponStatsListener LISTENER = new WeaponStatsListener();
	@SubscribeEvent
	public static void addReloadListener(AddReloadListenerEvent event)
	{
		event.addListener(LISTENER);
	}

	@SubscribeEvent
	public static void onDataSync(OnDatapackSyncEvent event)
	{

		SplatcraftPacketHandler.sendToAll(new UpdateWeaponSettingsPacket());
	}

	public static class WeaponStatsListener extends SimpleJsonResourceReloadListener
	{
		public static final HashMap<String, Class<? extends AbstractWeaponSettings<?>>> SETTING_TYPES = new HashMap<>()
		{{
			put(Splatcraft.MODID+":main", WeaponSettings.class);
			put(Splatcraft.MODID+":roller", RollerWeaponSettings.class);
			put(Splatcraft.MODID+":sub_weapon", SubWeaponSettings.class);
		}}; //TODO make better registry probably
		public static final  HashMap<ResourceLocation, AbstractWeaponSettings<?>> SETTINGS = new HashMap<>();

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
					AbstractWeaponSettings<?> settings = SETTING_TYPES.get(GsonHelper.getAsString(json, "type")).getConstructor(String.class).newInstance(key.toString());

					settings.getCodec().parse(JsonOps.INSTANCE, json).result().ifPresent(
							settings::castAndDeserialize
					);

					SETTINGS.put(key, settings);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				         NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
