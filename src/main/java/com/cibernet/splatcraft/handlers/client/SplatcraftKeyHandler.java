package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.capabilities.IPlayerInfo;
import com.cibernet.splatcraft.capabilities.PlayerInfoCapability;
import com.cibernet.splatcraft.network.PlayerSetSquidServerPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SplatcraftKeyHandler
{
	private static final HashMap<KeyBinding, Integer> pressState = new HashMap<>();
	
	public static KeyBinding squidKey;
	
	public static void registerKeys()
	{
		squidKey = new KeyBinding("key.squidForm", GLFW.GLFW_KEY_Z, "key.categories.splatcraft");
		pressState.put(squidKey, 0);
		ClientRegistry.registerKeyBinding(squidKey);
	}
	
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event)
	{
		if(squidKey.isKeyDown())
			pressState.put(squidKey, Math.min(pressState.get(squidKey)+1, 2));
		else pressState.put(squidKey, 0);
		
		if(pressState.get(squidKey) == 1)
			onSquidKeyPress();
	}
	
	public static void onSquidKeyPress()
	{
		PlayerEntity player = Minecraft.getInstance().player;
		IPlayerInfo capability = PlayerInfoCapability.get(player);
		SplatcraftPacketHandler.sendToServer(new PlayerSetSquidServerPacket(player));
		capability.setIsSquid(!capability.isSquid());
	}
}
