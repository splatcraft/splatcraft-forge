package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestClearInkPacket;
import net.splatcraft.forge.network.c2s.RequestTurfScanPacket;

import static net.splatcraft.forge.client.gui.stagepad.StageCreationScreen.getShortenedInt;

public class StageActionsScreen extends AbstractStagePadScreen
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_actions.png");
	private final Stage stage;
	private final StageSelectionScreen.ToggleMenuButton scanMode;
	public StageActionsScreen(Component label, String stageId, Screen mainMenu)
	{
		super(label);
		stage = Stage.getStage(getMinecraft().level, stageId);

		addOptionsTabs(label, stageId, mainMenu);

		scanMode = addButton(new StageSelectionScreen.ToggleMenuButton(136, 50, 24, 12, (b) -> {}, (b, ps, mx, my) ->
		{
			showText(new TranslatableComponent("gui.stage_pad.button.scan_mode", new TranslatableComponent("item.splatcraft.turf_scanner.mode." + (((StageSelectionScreen.ToggleMenuButton)b).toggle ? "1" : "0")))).onTooltip(b, ps, mx, my);
		}, drawToggleIcon(WIDGETS, 0, 0, 232, 48, 12, 12, true), MenuButton.ButtonColor.GREEN, false).setRenderBackground(false));
		addButton(new MenuButton(50, 50, 86, 12, (b) ->
		{
			SplatcraftPacketHandler.sendToServer(new RequestTurfScanPacket(stageId, !scanMode.toggle));
			getMinecraft().setScreen(null);
		}, Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.button.scan_turf"), true), MenuButton.ButtonColor.GREEN));
		addButton(new MenuButton(50, 64, 110, 12, (b) ->
		{
			SplatcraftPacketHandler.sendToServer(new RequestClearInkPacket(stageId));
			getMinecraft().setScreen(null);
		}, Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.button.clear_ink"), true), MenuButton.ButtonColor.GREEN));
		addButton(new MenuButton(50, 78, 110, 12, goToScreen(() -> mainMenu), Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.button.pair_remote"), true), MenuButton.ButtonColor.GREEN));
	}

	@Override
	public void handleWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {

	}

	@Override
	public void renderBackground(PoseStack poseStack)
	{
		super.renderBackground(poseStack);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, TEXTURES);

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

	}
}
