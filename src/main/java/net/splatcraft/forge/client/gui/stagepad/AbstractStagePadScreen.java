package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.items.StagePadItem;
import org.jetbrains.annotations.Nullable;

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
	protected final ArrayList<MenuTextBox> textFields = new ArrayList<>();
	protected final ArrayList<MenuTextBox.Factory> textFieldFactories = new ArrayList<>();

	public final StagePadItem.UseAction OPEN_SELF = (level, player, hand, stack, pos) -> getMinecraft().setScreen(this);

	protected StagePadItem.UseAction useAction;

	protected AbstractStagePadScreen(Component label, @Nullable StagePadItem.UseAction useAction)
	{
		super(label);

		this.useAction = useAction == null ? OPEN_SELF : useAction;
		minecraft = Minecraft.getInstance();
	}

	protected AbstractStagePadScreen(Component label)
	{
		this(label, null);
	}

	public <B extends MenuButton> B addButton(B button)
	{
		buttons.add(button);
		addWidget(button);
		return button;
	}

	public void addTextBox(MenuTextBox.Factory factory)
	{
		textFieldFactories.add(factory);
	}

	@Override
	protected void init()
	{
		super.init();

		StagePadItem.clientUseAction = useAction;

		if(textFields.isEmpty())
			textFieldFactories.forEach(factory -> textFields.add(factory.newInstance(font)));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int mouseX, int mouseY, int key)
	{
		for (EditBox textField : textFields) {
			if(textField.isFocused())
				textField.keyPressed(mouseX, mouseY, key);
		}
		return super.keyPressed(mouseX, mouseY, key);
	}

	@Override
	public boolean charTyped(char c, int p_94684_)
	{
		for (EditBox textField : textFields)
			if(textField.isFocused())
				textField.charTyped(c, p_94684_);
		return super.charTyped(c, p_94684_);
	}

	@Override
	public void tick() {
		super.tick();
		for (EditBox textField : textFields)
			textField.tick();
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
		for (MenuTextBox button : textFields) {
			button.x = button.relativeX + x;
			button.y = button.relativeY + y;
		}

		handleWidgets(poseStack, mouseX, mouseY, partialTicks);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		renderTooltips(poseStack, mouseX, mouseY);


		buttons.forEach(b -> b.renderButton(poseStack));
		textFields.forEach(t -> t.render(poseStack, mouseX, mouseY, partialTicks));
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

	public Button.OnTooltip showCopyPos(BlockPosGetter pos)
	{
		return (button, poseStack, x, y) ->
		{
			if(pos.get() != null)
				renderTooltip(poseStack, Arrays.asList(new TranslatableComponent("gui.stage_pad.button.position", pos.get().getX(), pos.get().getY(), pos.get().getZ()), new TranslatableComponent("gui.stage_pad.button.copy_position").withStyle(ChatFormatting.YELLOW)),
						Optional.empty(), x, y);
		};
	}

	public Button.OnPress copyPos(BlockPosGetter pos)
	{
		return (button) ->
		{
			if(pos.get() != null)
				getMinecraft().keyboardHandler.setClipboard(pos.get().getX() + " " + pos.get().getY() + " " + pos.get().getZ());
		};
	}

	public interface BlockPosGetter
	{
		BlockPos get();
	}

	public MenuButton.PostDraw drawIcon(ResourceLocation location, int xOff, int yOff, int texX, int texY, int texWidth, int texHeight)
	{
		return (poseStack, button) ->
		{
			float color = button.active ? 1 : 0.5f;
			RenderSystem.setShaderColor(color, color, color, button.getAlpha());
			RenderSystem.setShaderTexture(0, location);
			this.blit(poseStack, button.x + xOff, button.y + yOff, texX, texY, texWidth, texHeight);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		};
	}
	public MenuButton.PostDraw drawToggleIcon(ResourceLocation location, int xOff, int yOff, int texX, int texY, int texWidth, int texHeight, boolean offsetTx)
	{
		return (poseStack, button) ->
		{
			if(!(button instanceof StageSelectionScreen.ToggleMenuButton t))
			{
				drawIcon(location, xOff, yOff, texX, texY, texWidth, texHeight).apply(poseStack, button);
				return;
			}

			float color = button.active ? 1 : 0.5f;
			RenderSystem.setShaderColor(color, color, color, button.getAlpha());
			RenderSystem.setShaderTexture(0, location);
			this.blit(poseStack, button.x + xOff + (t.toggle ? button.getWidth() / 2 : 0), button.y + yOff, texX + (offsetTx & t.toggle ? texWidth : 0), texY, texWidth, texHeight);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		};
	}
	public MenuButton.PostDraw drawText(boolean centeredText)
	{
		return (poseStack, button) -> drawText(button.getMessage(), centeredText).apply(poseStack, button);
	}
	public MenuButton.PostDraw drawText(Component label, boolean centeredText)
	{
		return (poseStack, button) ->
		{

			int j = button.getFGColor();
			drawString(poseStack, font, label, button.x + (centeredText ? (button.getWidth() - font.width(label)) / 2 : 3), button.y + (button.getHeight() - 8) / 2, j | Mth.ceil(button.getAlpha() * 255.0F) << 24);
		};
	}

	public Button.OnPress goToScreen(ScreenFactory screen)
	{
		return (b) -> getMinecraft().setScreen(screen.create());
	}

	public interface ScreenFactory
	{
		Screen create();
	}

	public boolean canClickButtons()
	{
		return true;
	}
	public abstract void handleWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

	protected void addOptionsTabs(Component label, String stageId, Screen mainMenu)
	{
		addButton(new MenuButton(10, 12, 14, 12, goToScreen(() -> mainMenu), Button.NO_TOOLTIP, drawIcon(WIDGETS, 1, 0, 244, 24, 12, 12), MenuButton.ButtonColor.GREEN));
		addButton(new MenuButton(24, 12, 44, 12, goToScreen(() -> new StageSettingsScreen(label, stageId, mainMenu)), Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.tab.settings"), true), MenuButton.ButtonColor.PURPLE));
		addButton(new MenuButton(68, 12, 44, 12, goToScreen(() -> new StageRulesScreen(label, stageId, mainMenu)), Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.tab.rules"), true), MenuButton.ButtonColor.PURPLE));
		addButton(new MenuButton(112, 12, 44, 12, goToScreen(() -> new StageSettingsScreen(label, stageId, mainMenu)), Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.tab.teams"), true), MenuButton.ButtonColor.PURPLE));
		addButton(new MenuButton(156, 12, 44, 12, goToScreen(() -> new StageActionsScreen(label, stageId, mainMenu)), Button.NO_TOOLTIP, drawText(new TranslatableComponent("gui.stage_pad.tab.actions"), true), MenuButton.ButtonColor.PURPLE));
	}
}
