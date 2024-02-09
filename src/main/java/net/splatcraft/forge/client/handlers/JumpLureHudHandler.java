package net.splatcraft.forge.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.items.JumpLureItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.UseJumpLurePacket;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class JumpLureHudHandler
{

	private static SuperJumpTargets targets;
	private static double scrollDelta = 0;
	@SubscribeEvent
	public static void renderGui(RenderGameOverlayEvent.Pre event)
	{
		LocalPlayer player = Minecraft.getInstance().player;
		if(player == null)
			return;

		if(!event.getType().equals(RenderGameOverlayEvent.ElementType.LAYER) || targets == null)
			return;

		ArrayList<UUID> playerUuids = new ArrayList<>(targets.playerTargetUuids);
		playerUuids.removeIf(uuid -> !player.connection.getOnlinePlayerIds().contains(uuid));

		int entryCount = playerUuids.size() + (targets.canTargetSpawn ? 2 : 1);
		int index = Math.floorMod((int)scrollDelta, entryCount);


		int j = 0;
		for(int i = index; i < entryCount; i++)
		{
			if(i == 0)
				Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), "cancel", 10, 10 * j, 0xFFFFFF);
			else if(targets.canTargetSpawn && i == 1)
				Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), "go to spawn", 10, 10 * j, 0xFFFFFF);
			else
			{
				Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), player.connection.getPlayerInfo(playerUuids.get(i - (targets.canTargetSpawn ? 2 : 1))).getProfile().getName(),
						10, 10 * j, 0xFFFFFF);
			}


			j++;
		}
	}

	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollEvent event)
	{
		LocalPlayer player = Minecraft.getInstance().player;
		if(player == null)
			return;

		if(player.getUseItem().getItem() instanceof JumpLureItem)
		{
			scrollDelta += event.getScrollDelta();
			event.setCanceled(true);
		}
	}

	public static void updateTargetData(@Nullable SuperJumpTargets targets)
	{
		JumpLureHudHandler.targets = targets;
	}

	public static void releaseLure()
	{
		LocalPlayer player = Minecraft.getInstance().player;

		if(player == null || targets == null) return;

		ArrayList<UUID> playerUuids = new ArrayList<>(targets.playerTargetUuids);
		playerUuids.removeIf(uuid -> !player.connection.getOnlinePlayerIds().contains(uuid));

		int entryCount = playerUuids.size() + (targets.canTargetSpawn ? 2 : 1);
		int index = Math.floorMod((int)scrollDelta, entryCount);

		SuperJumpTargets targets = JumpLureHudHandler.targets;
		updateTargetData(null);

		if(index == 0) return;
		UUID target = (targets.canTargetSpawn && index == 1) ? null : playerUuids.get(index - (targets.canTargetSpawn ? 2 : 1));

		SplatcraftPacketHandler.sendToServer(new UseJumpLurePacket(targets.color, target));

	}

	public static class SuperJumpTargets
	{
		final ArrayList<UUID> playerTargetUuids;
		final boolean canTargetSpawn;
		final int color;


		public SuperJumpTargets(ArrayList<UUID> playerTargetUuids, boolean canTargetSpawn, int color) {
			this.playerTargetUuids = playerTargetUuids;
			this.canTargetSpawn = canTargetSpawn;
			this.color = color;
		}
	}
}
