package com.cibernet.splatcraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class ClientUtils
{
	public static PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
}
