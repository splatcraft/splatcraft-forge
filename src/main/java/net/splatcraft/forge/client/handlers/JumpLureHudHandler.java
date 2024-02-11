package net.splatcraft.forge.client.handlers;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.client.gui.SuperJumpSelectorScreen;
import net.splatcraft.forge.items.JumpLureItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.UseJumpLurePacket;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class JumpLureHudHandler
{

	private static SuperJumpTargets targets;
	private static double scrollDelta = 0;

	private static final SuperJumpSelectorScreen selectorGui = new SuperJumpSelectorScreen();

	@SubscribeEvent
	public static void renderGui(RenderGameOverlayEvent.Pre event)
	{
		LocalPlayer player = Minecraft.getInstance().player;

		if(!event.getType().equals(RenderGameOverlayEvent.ElementType.LAYER) || player == null ||
		!(player.getUseItem().getItem() instanceof JumpLureItem) || targets == null)
			return;

		selectorGui.render(event.getMatrixStack(), event.getPartialTicks(), targets, scrollDelta);
	}


	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollEvent event)
	{
		LocalPlayer player = Minecraft.getInstance().player;
		if(player == null)
			return;

		if(player.getUseItem().getItem() instanceof JumpLureItem)
		{
			scrollDelta -= event.getScrollDelta();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onKeypadInput(TickEvent.ClientTickEvent event)
	{
		LocalPlayer player = Minecraft.getInstance().player;
		if(event.phase != TickEvent.Phase.START || player == null)
			return;

		if(player.getUseItem().getItem() instanceof JumpLureItem)
		{
			if(Minecraft.getInstance().options.keyAttack.consumeClick())
				scrollDelta = 0;

			int totalOptions = targets.playerTargetUuids.size() + (targets.canTargetSpawn ? 2 : 1);

			for(int i = 0; i < 9; i++)
			{
				KeyMapping key = Minecraft.getInstance().options.keyHotbarSlots[i];

				if(key.consumeClick())
				{
					scrollDelta += Math.signum(i - 4) * Math.min(totalOptions, Math.abs(i - 4));
					return;
				}
			}
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
		public final ArrayList<UUID> playerTargetUuids;
		public final boolean canTargetSpawn;
		public final int color;


		public SuperJumpTargets(ArrayList<UUID> playerTargetUuids, boolean canTargetSpawn, int color) {
			this.playerTargetUuids = playerTargetUuids;
			this.canTargetSpawn = canTargetSpawn;
			this.color = color;
		}
	}
}
