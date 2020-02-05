package com.cibernet.splatcraft.handlers;

import javafx.scene.input.KeyCode;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SplatCraftKeyHandler
{
	public static final SplatCraftKeyHandler instance = new SplatCraftKeyHandler();
	
	public static KeyBinding squidKey;
	
	public void registerKeys()
	{
		squidKey = new KeyBinding("key.squidForm", KeyCode.Z.ordinal(), "key.categories.splatcraft");
		ClientRegistry.registerKeyBinding(squidKey);
	}
}
