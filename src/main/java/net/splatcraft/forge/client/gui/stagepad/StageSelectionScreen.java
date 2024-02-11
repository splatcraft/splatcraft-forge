package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestUpdateStageSpawnPadsPacket;
import net.splatcraft.forge.network.c2s.SuperJumpToStagePacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StageSelectionScreen extends Screen
{
	protected int imageWidth = 210;
	protected int imageHeight = 130;

	final ArrayList<MenuButton> buttons = new ArrayList<>();
	final ArrayList<MenuButton> stageButtons = new ArrayList<>();
	final ArrayList<SuperJumpMenuButton> jumpButtons = new ArrayList<>();


	private double scroll = 0;

	static StageSelectionScreen instance;

	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_select.png");
	private static final ResourceLocation WIDGETS = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/widgets.png");
	public StageSelectionScreen(Component title)
	{
		super(title);

		minecraft = Minecraft.getInstance();
		instance = this;

		MenuButton createStageButton = new MenuButton(10, 0, 180, new TranslatableComponent("gui.stage_pad.button.create"), (b) -> {}, Button.NO_TOOLTIP, (ps, b) -> {},ButtonColor.LIME);
		stageButtons.add(createStageButton);
		buttons.add(createStageButton);

		if (minecraft.level != null)
		{
			ArrayList<Stage> stages = Stage.getAllStages(minecraft.level);
			for(Stage stage : stages)
				addStageButton(stage);
		}

	}

	public void addStageButton(Stage stage)
	{
		MenuButton stageButton = new MenuButton(10, 0, 168, stage.getStageName(), (b) -> {}, Button.NO_TOOLTIP, (ps, b) -> {}, ButtonColor.GREEN);
		SuperJumpMenuButton jumpButton = new SuperJumpMenuButton(178, 0, 12,
				(button, poseStack, mx, my) -> renderTooltip(poseStack, List.of(((SuperJumpMenuButton)button).state.tooltipText), Optional.empty(), mx, my),
				(poseStack, button) -> drawIcon(WIDGETS, 0, 0, 244, ((SuperJumpMenuButton)button).state == SuperJumpMenuButton.ButtonState.REQUIRES_UPDATE ? 12 : 0, 12, 12).apply(poseStack, button), stage);

		jumpButton.active = false;

		stageButtons.add(stageButton);
		jumpButtons.add(jumpButton);
		buttons.add(stageButton);
		buttons.add(jumpButton);
	}

	public static void updateValidSuperJumpsList(List<String> validStages, List<String> outOfReachStages, List<String> needsUpdateStages)
	{
		for (SuperJumpMenuButton jumpButton : instance.jumpButtons)
		{
			SuperJumpMenuButton.loading = false;
			jumpButton.state = validStages.contains(jumpButton.stage.id) ? SuperJumpMenuButton.ButtonState.VALID :
					needsUpdateStages.contains(jumpButton.stage.id) ? SuperJumpMenuButton.ButtonState.REQUIRES_UPDATE :
					outOfReachStages.contains(jumpButton.stage.id) ? SuperJumpMenuButton.ButtonState.OUT_OF_RANGE :
							SuperJumpMenuButton.ButtonState.NO_SPAWN_PADS;
		}
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{

		MenuButton hoveredButton = null;

		for(MenuButton button : buttons)
		{
			button.setHovered(false);
			if(isMouseOver(mouseX, mouseY, button))
				hoveredButton = button;
		}

		if(hoveredButton != null)
			hoveredButton.setHovered(true);

		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltips(matrixStack, mouseX, mouseY);

	}
	public boolean isMouseOver(double mouseX, double mouseY, MenuButton button)
	{
		return isMouseOver(mouseX, mouseY, button.x, button.y, button.x + button.getWidth(), button.y + button.getHeight());
	}
	public boolean isMouseOver(double mouseX, double mouseY, double x1, double y1, double x2, double y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}

	private boolean scrollBarHeld = false;

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int clickButton)
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		if(stageButtons.size() > 8 && isMouseOver(mouseX, mouseY, x + 190, y + 24, x + 200, y + 120))
			scrollBarHeld = true;
		if(!scrollBarHeld)
			for (MenuButton button : buttons)
				button.mouseClicked(mouseX, mouseY, clickButton);
		return super.mouseClicked(mouseX, mouseY, clickButton);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int clickButton)
	{
		scrollBarHeld = false;
		return super.mouseReleased(mouseX, mouseY, clickButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double p_94702_, double p_94703_)
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		if(scrollBarHeld)
			scroll = Mth.clamp((mouseY - (y + 24)) / 96f, 0, 1);

		return super.mouseDragged(mouseX, mouseY, mouseButton, p_94702_, p_94703_);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (stageButtons.size() > 8)
			scroll = Mth.clamp(scroll - Math.signum(amount) / (stageButtons.size() - 8), 0.0f, 1.0f);

		return true;
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

		RenderSystem.setShaderTexture(0, WIDGETS);
		blit(poseStack, x + 190, y + 24 + (int) (scroll * 81), 202 + (stageButtons.size() > 8 ? (scrollBarHeld ? 2 : 1) * 10 : 0), 0, 10, 15);

		Component label = new TranslatableComponent("gui.stage_pad.label.stage_select");
		font.draw(poseStack, label, x + 105 - (float) font.width(label) / 2, y + 14, 0xFFFFFF);

		for(int i = 0; i < Math.min(8, stageButtons.size()); i++)
		{
			int index = (int) (scroll * (stageButtons.size() - 8) + i);
			if(index >= stageButtons.size()) break;

			MenuButton stageButton = stageButtons.get(index);
			stageButton.x = x + 10;
			stageButton.y = y + (i + 2) * 12;
			stageButton.renderButton(poseStack);

			if(index > 0 && index - 1 < jumpButtons.size())
			{
				SuperJumpMenuButton jumpButton = jumpButtons.get(index - 1);
				jumpButton.x = x + 178;
				jumpButton.y = y + (i + 2) * 12;
				jumpButton.active = jumpButton.getState().valid;

				jumpButton.renderButton(poseStack);
			}
		}
	}

	private void renderTooltips(PoseStack poseStack, int mouseX, int mouseY)
	{
		for(MenuButton button : buttons)
			if(button.isHovered())
				button.renderToolTip(poseStack, mouseX, mouseY);
	}

	public static class MenuButton extends Button
	{
		ButtonColor color;
		final PostDraw draw;

		public MenuButton(int x, int y, int width, Component text, OnPress onPress, OnTooltip onTooltip, PostDraw draw, ButtonColor color)
		{
			this(x, y, width, 12, text, onPress, onTooltip, draw, color);
		}
		public MenuButton(int x, int y, int width, int height, Component text, OnPress onPress, OnTooltip onTooltip, PostDraw draw,  ButtonColor color)
		{
			super(x, y, width, height, text, onPress, onTooltip);
			this.color = color;
			this.draw = draw;
		}

		public void renderButton(PoseStack poseStack) {
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			int i = this.getYImage(this.isHoveredOrFocused());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.blit(poseStack, this.x, this.y, 0, getColor().ordinal() * 36 + i * 12, this.width / 2, this.height);
			this.blit(poseStack, this.x + this.width / 2, this.y, 180 - this.width / 2, getColor().ordinal() * 36 + i * 12, this.width / 2, this.height);
			int j = getFGColor();
			drawString(poseStack, font, this.getMessage(), this.x + 3, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);

			draw.apply(poseStack, this);

		}

		@Override
		protected int getYImage(boolean hovered)
		{
			return active ? (hovered ? 2 : 1) : 0;
		}

		public float getAlpha()
		{
			return alpha;
		}

		@Override
		public int getFGColor() {
			return 0xFFFFFF;
		}

		public boolean isHovered()
		{
			return isHovered;
		}

		public void setHovered(boolean hovered)
		{
			isHovered = hovered;
		}

		public ButtonColor getColor() {
			return color;
		}

		public void setColor(ButtonColor color) {
			this.color = color;
		}

		public interface PostDraw
		{
			void apply(PoseStack poseStack, MenuButton button);
		}
	}

	public static class SuperJumpMenuButton extends MenuButton
	{
		final Stage stage;
		private ButtonState state = ButtonState.REQUESTING;

		public static boolean loading = false;

		public SuperJumpMenuButton(int x, int y, int width, OnTooltip onTooltip, PostDraw draw, Stage stage)
		{
			super(x, y, width, TextComponent.EMPTY, (b) ->
			{
				SuperJumpMenuButton jumpButton = ((SuperJumpMenuButton)b);

				switch (jumpButton.getState())
				{
					case REQUIRES_UPDATE ->
					{
						jumpButton.state = ButtonState.REQUESTING;
						loading = true;
						SplatcraftPacketHandler.sendToServer(new RequestUpdateStageSpawnPadsPacket(jumpButton.stage));
					}
					default ->
					{
						SplatcraftPacketHandler.sendToServer(new SuperJumpToStagePacket(stage.id));
						Minecraft.getInstance().setScreen(null);
					}
				}

			}, onTooltip, draw, ButtonColor.CYAN);

			this.stage = stage;
		}

		public ButtonState getState()
		{
			return loading ? ButtonState.REQUESTING : state;
		}

		@Override
		public ButtonColor getColor() {
			return state.color;
		}

		@Override
		public void renderToolTip(PoseStack p_93736_, int p_93737_, int p_93738_)
		{
			super.renderToolTip(p_93736_, p_93737_, p_93738_);
		}

		public enum ButtonState
		{
			REQUESTING(false, ButtonColor.YELLOW, new TranslatableComponent("gui.stage_pad.button.superjump_to.requesting")),
			OUT_OF_RANGE(false, ButtonColor.RED, new TranslatableComponent("gui.stage_pad.button.superjump_to.out_of_range").withStyle(ChatFormatting.RED)),
			NO_SPAWN_PADS(false, ButtonColor.RED,  new TranslatableComponent("gui.stage_pad.button.superjump_to.no_pads_found").withStyle(ChatFormatting.RED)),
			VALID(true, ButtonColor.CYAN,  new TranslatableComponent("gui.stage_pad.button.superjump_to")),
			REQUIRES_UPDATE(true, ButtonColor.YELLOW, new TranslatableComponent("gui.stage_pad.button.superjump_to.requires_update").withStyle(ChatFormatting.YELLOW))
			;


			final boolean valid;
			final Component tooltipText;
			final ButtonColor color;

			ButtonState(boolean valid, ButtonColor color, Component tooltipText) {
				this.valid = valid;
				this.tooltipText = tooltipText;
				this.color = color;
			}
		}
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
			this.blit(poseStack, button.x + xOff, button.y + yOff, texX, texY, texWidth, texHeight);
		};
	}


	public enum ButtonColor
	{
		GREEN,
		PURPLE,
		LIME,
		CYAN,
		RED,
		YELLOW
	}
}
