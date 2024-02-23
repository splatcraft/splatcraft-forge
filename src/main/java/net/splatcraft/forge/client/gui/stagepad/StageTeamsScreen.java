package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;

public class StageTeamsScreen extends AbstractStagePadScreen
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_teams.png");
	private Stage stage;
	public StageTeamsScreen(Component label, String stageId, Screen mainMenu)
	{
		super(label);
		stage = Stage.getStage(Minecraft.getInstance().level, stageId);

		addOptionsTabs(label, stageId, mainMenu);
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

		//blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

	}

	@Override
	public void onStagesUpdate()
	{
		stage = Stage.getStage(Minecraft.getInstance().level, stage.id);
	}
}
