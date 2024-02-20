package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import org.jetbrains.annotations.Nullable;

public class StageCreationScreen extends AbstractStagePadScreen
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_create.png");

	protected String stageId = "";

	BlockPos corner1;
	BlockPos corner2;
	String defaultName;

	protected StageCreationScreen(Component label, @Nullable Screen parent, String stageName, @Nullable BlockPos cornerA, @Nullable BlockPos cornerB)
	{
		super(label);

		addButton(new MenuButton(51, 107, 50, 12, new TranslatableComponent("gui.stage_pad.button.cancel"), true, (b) -> getMinecraft().setScreen(parent),
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.RED));
		addButton(new MenuButton(107, 107, 50, 12, new TranslatableComponent("gui.stage_pad.button.create"), true, (b) -> {},
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.LIME));
		addButton(new MenuButton(167, 70, 30, 12, new TranslatableComponent("gui.stage_pad.button.set_corner"), true, (b) -> clickSetCornerButton(b, true),
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.GREEN));
		addButton(new MenuButton(167, 88, 30, 12, new TranslatableComponent("gui.stage_pad.button.set_corner"), true, (b) -> clickSetCornerButton(b, false),
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.GREEN));


		this.corner1 = cornerA;
		this.corner2 = cornerB;
		this.defaultName = stageName;
	}

	protected void clickSetCornerButton(Button button, boolean isCorner1)
	{

	}

	@Override
	public void handleWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
	{
		String newId = stageId;

		if(getMinecraft().getSingleplayerServer() != null)
			for(int i = 1; SaveInfoCapability.get(getMinecraft().getSingleplayerServer()).getStages().containsKey(newId); i++)
				newId = stageId + "_" + i;

		stageId = newId;
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

		Component label = new TranslatableComponent("gui.stage_pad.label.create_stage");
		font.draw(poseStack, label, x + 105 - (float) font.width(label) / 2, y + 14, 0xFFFFFF);
		font.draw(poseStack, new TranslatableComponent("gui.stage_pad.label.set_stage_name"), x + 14, y + 28, 0xFFFFFF);
		font.draw(poseStack, new TranslatableComponent("gui.stage_pad.label.stage_id", stageId), x + 14, y + 55, 0x808080);

		label = new TranslatableComponent("gui.stage_pad.label.corner_1");
		font.draw(poseStack, label, x + 60 - font.width(label), y + 72, 0xFFFFFF);
		label = new TranslatableComponent("gui.stage_pad.label.corner_2");
		font.draw(poseStack, label, x + 60 - font.width(label), y + 90, 0xFFFFFF);

	}
}
