package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractStagePadScreen extends Screen
{
	protected int imageWidth = 210;
	protected int imageHeight = 130;
	protected static final ResourceLocation COMMON_TEXTURE = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/common.png");
	protected static final ResourceLocation WIDGETS = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/widgets.png");
	protected final ArrayList<MenuButton> buttons = new ArrayList<>();
	
	protected AbstractStagePadScreen(Component label) 
	{
		super(label);
		minecraft = Minecraft.getInstance();
	}

	public void addButton(MenuButton button)
	{
		buttons.add(button);
		addWidget(button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int clickButton)
	{
		if(canClickButtons())
			for (MenuButton button : buttons)
				if(button.isActive())
					button.mouseClicked(mouseX, mouseY, clickButton);
		return super.mouseClicked(mouseX, mouseY, clickButton);
	}

	private void renderTooltips(PoseStack poseStack, int mouseX, int mouseY)
	{
		for(MenuButton button : buttons)
			if(button.visible && button.isHovered())
				button.renderToolTip(poseStack, mouseX, mouseY);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {


		MenuButton hoveredButton = null;

		for (MenuButton button : buttons) {
			button.setHovered(false);
			if (isMouseOver(mouseX, mouseY, button))
				hoveredButton = button;
		}

		if(canClickButtons())
		{
			if (hoveredButton != null)
				hoveredButton.setHovered(true);
		}


		renderBackground(poseStack);

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		for (MenuButton button : buttons) {
			button.x = button.relativeX + x;
			button.y = button.relativeY + y;
		}

		handleWidgets(poseStack, mouseX, mouseY, partialTicks);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		renderTooltips(poseStack, mouseX, mouseY);


		buttons.forEach(b -> b.renderButton(poseStack));
	}

	@Override
	public void renderBackground(PoseStack poseStack)
	{
		super.renderBackground(poseStack);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, COMMON_TEXTURE);

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
	}

	public boolean isMouseOver(double mouseX, double mouseY, MenuButton button)
	{
		return isMouseOver(mouseX, mouseY, button.x, button.y, button.x + button.getWidth(), button.y + button.getHeight());
	}
	public boolean isMouseOver(double mouseX, double mouseY, double x1, double y1, double x2, double y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}


	public Button.OnTooltip showText(Component... lines)
	{
		return (button, poseStack, x, y) ->
		{
			renderTooltip(poseStack, Arrays.asList(lines), Optional.empty(), x, y);
		};
	}

	public MenuButton.PostDraw drawIcon(ResourceLocation location, int xOff, int yOff, int texX, int texY, int texWidth, int texHeight)
	{
		return (poseStack, button) ->
		{
			float color = button.active ? 1 : 0.5f;
			RenderSystem.setShaderColor(color, color, color, button.getAlpha());
			RenderSystem.setShaderTexture(0, location);
			this.blit(poseStack, button.x + xOff + (button instanceof StageSelectionScreen.ToggleMenuButton t && t.toggle ? button.getWidth() / 2 : 0), button.y + yOff, texX, texY, texWidth, texHeight);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		};
	}

	public boolean canClickButtons()
	{
		return true;
	}
	public abstract void handleWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);
}
