package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.items.StagePadItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestUpdateStageSpawnPadsPacket;
import net.splatcraft.forge.network.c2s.RequestWarpDataPacket;
import net.splatcraft.forge.network.c2s.SuperJumpToStagePacket;

import java.util.*;

public class StageSelectionScreen extends AbstractStagePadScreen
{
	static final TreeMap<Stage, Pair<MenuButton, SuperJumpMenuButton>> stages = new TreeMap<>();

	MenuTextBox searchBar;
	MenuButton createStageButton;
	MenuButton toggleSearchBarButton;

	private double scroll = 0;

	public static StageSelectionScreen instance;

	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_select.png");

	public StageSelectionScreen(Component title)
	{
		super(title, StagePadItem.OPEN_MAIN_MENU);

		instance = this;

		createStageButton = addButton(new MenuButton(10, 0, 178, 12,
				goToScreen(() -> new StageCreationScreen(title, this, "", null, null)), Button.NO_TOOLTIP,
				drawText(new TranslatableComponent("gui.stage_pad.button.create_stage"), false), MenuButton.ButtonColor.LIME));
		stages.clear();

		toggleSearchBarButton = addButton(new ToggleMenuButton(176, 12, 24, 12, (b) ->
		{
			searchBar.visible = !searchBar.visible;
			searchBar.setFocus(searchBar.visible);
			if(!searchBar.visible)
				searchBar.setValue("");
		}, showText(new TranslatableComponent("gui.stage_pad.button.search_stage")), drawToggleIcon(WIDGETS, 0, 0, 232, 12, 12, 12, false), MenuButton.ButtonColor.PURPLE, false));

		addTextBox(font ->
		{
			searchBar = new MenuTextBox(font, 11, 13, 175, 10, new TranslatableComponent("gui.stage_pad.textbox.search_stage"), false);
			searchBar.setFocus(true);
			searchBar.visible = false;
			return searchBar;
		});
	}

	@Override
	protected void init()
	{
		super.init();

		if (getMinecraft().level == null)
			return;

		ArrayList<Stage> stages = Stage.getAllStages(getMinecraft().level);

		if(!stages.equals(new ArrayList<>(StageSelectionScreen.stages.keySet())))
		{
			SplatcraftPacketHandler.sendToServer(new RequestWarpDataPacket());
			stages.forEach(this::addStageButton);
		}
	}

	public void addStageButton(Stage stage)
	{
		MenuButton stageButton = new MenuButton(10, 0, 166, goToScreen(() -> new StageSettingsScreen(getTitle(), stage.id, this)), Button.NO_TOOLTIP, drawText(false), MenuButton.ButtonColor.GREEN)
		{
			@Override
			public Component getMessage() {
				return Stage.getStage(Minecraft.getInstance().level, stage.id).getStageName();
			}
		};
		SuperJumpMenuButton jumpButton = new SuperJumpMenuButton(176, 0, 12,
				(button, poseStack, mx, my) -> renderTooltip(poseStack, List.of(((SuperJumpMenuButton)button).state.tooltipText), Optional.empty(), mx, my),
				(poseStack, button) -> drawIcon(WIDGETS, 0, 0, 244, ((SuperJumpMenuButton)button).state == SuperJumpMenuButton.ButtonState.REQUIRES_UPDATE ? 12 : 0, 12, 12).apply(poseStack, button), stage);

		jumpButton.active = false;

		if(stages.containsKey(stage))
		{
			Pair<MenuButton, SuperJumpMenuButton> pair = stages.get(stage);
			buttons.remove(pair.getFirst());
			removeWidget(pair.getFirst());
			buttons.remove(pair.getSecond());
			removeWidget(pair.getSecond());
		}

		stages.put(stage, new Pair<>(stageButton, jumpButton));
		addButton(stageButton);
		addButton(jumpButton);
	}

	public static void updateValidSuperJumpsList(List<String> validStages, List<String> outOfReachStages, List<String> needsUpdateStages)
	{
		stages.values().stream().map(Pair::getSecond).forEach(jumpButton ->
		{
			SuperJumpMenuButton.loading = false;
			jumpButton.state = validStages.contains(jumpButton.stage.id) ? SuperJumpMenuButton.ButtonState.VALID :
					needsUpdateStages.contains(jumpButton.stage.id) ? SuperJumpMenuButton.ButtonState.REQUIRES_UPDATE :
					outOfReachStages.contains(jumpButton.stage.id) ? SuperJumpMenuButton.ButtonState.OUT_OF_RANGE :
							SuperJumpMenuButton.ButtonState.NO_SPAWN_PADS;
		});
	}


	@Override
	public void handleWidgets(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		List<Pair<MenuButton, SuperJumpMenuButton>> stageButtons = stages.keySet().stream().sorted(Comparator.comparing(s -> s.getStageName().getString()))
				.filter(s -> s.getStageName().getString().toLowerCase().contains(searchBar.getValue().toLowerCase()))
				.map(stages::get).toList();

		buttonListSize = stageButtons.size() + 1;

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		buttons.forEach(b -> b.visible = false);

		for(int i = 0; i < Math.min(8, stageButtons.size() + 1); i++)
		{
			MenuButton stageButton;
			SuperJumpMenuButton jumpButton = null;

			int index = (int) (scroll * Math.max(0, stageButtons.size() - 7) + i);

			if(index == 0)
				stageButton = createStageButton;
			else
			{
				index--;
				if(index >= stageButtons.size()) break;
				stageButton = stageButtons.get(index).getFirst();
				jumpButton = stageButtons.get(index).getSecond();
			}

			stageButton.relativeY = (i + 2) * 12;
			stageButton.visible = true;

			if(jumpButton != null)
			{
				jumpButton.relativeY = (i + 2) * 12;
				jumpButton.active = jumpButton.getState().valid;
				jumpButton.visible = true;
			}
		}

		toggleSearchBarButton.visible = true;
	}

	@Override
	public boolean canClickButtons() {
		return !scrollBarHeld;
	}


	private boolean scrollBarHeld = false;
	int buttonListSize = 0;
	String prevSearchBarText = "";

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int clickButton)
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		if(buttonListSize > 7 && isMouseOver(mouseX, mouseY, x + 190, y + 24, x + 200, y + 120))
			scrollBarHeld = true;

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
		if (stages.size() > 7)
			scroll = Mth.clamp(scroll - Math.signum(amount) / (buttonListSize - 7), 0.0f, 1.0f);

		return true;
	}

	@Override
	public void renderBackground(PoseStack poseStack)
	{
		if(!searchBar.getValue().equals(prevSearchBarText))
		{
			scroll = 0;
			prevSearchBarText = searchBar.getValue();
		}

		super.renderBackground(poseStack);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, TEXTURES);

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

		if(searchBar.visible)
			blit(poseStack, x + 10, y + 12, 0, 244, 178, 12);
		else
		{
			Component label = new TranslatableComponent("gui.stage_pad.label.stage_select");
			font.draw(poseStack, label, x + 105 - (float) font.width(label) / 2, y + 14, 0xFFFFFF);
		}

		RenderSystem.setShaderTexture(0, WIDGETS);
		blit(poseStack, x + 188, y + 24 + (int) (scroll * 81), 196 + (buttonListSize > 7 ? (scrollBarHeld ? 2 : 1) * 12 : 0), 0, 12, 15);
	}


	public static class ToggleMenuButton extends MenuButton
	{
		boolean toggle;
		boolean renderBackground = true;
		public ToggleMenuButton(int x, int y, int width, OnPress onPress, OnTooltip onTooltip, PostDraw draw, ButtonColor color, boolean defaultState)
		{
			super(x, y, width, onPress, onTooltip, draw, color);
			toggle = defaultState;
		}

		public ToggleMenuButton(int x, int y, int width, int height, OnPress onPress, OnTooltip onTooltip, PostDraw draw, ButtonColor color, boolean defaultState)
		{
			super(x, y, width, height, onPress, onTooltip, draw, color);
			toggle = defaultState;
		}

		@Override
		public void onPress()
		{
			toggle = !toggle;
			super.onPress();
		}

		public ToggleMenuButton setRenderBackground(boolean v)
		{
			renderBackground = v;
			return this;
		}

		public void renderButton(PoseStack poseStack) {
			if(!visible)
				return;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();

			if(renderBackground)
			{
				this.blit(poseStack, this.x, this.y, 0, getColor().ordinal() * 36, this.width / 2, this.height);
				this.blit(poseStack, this.x + this.width / 2, this.y, 180 - this.width / 2, getColor().ordinal() * 36, this.width / 2, this.height);
			}

			if(active)
			{
				int i = this.getYImage(this.isHoveredOrFocused());
				this.blit(poseStack, this.x + (toggle ? width / 2 : 0), this.y, 0, getColor().ordinal() * 36 + i * 12, this.width / 4, this.height);
				this.blit(poseStack, this.x + (toggle ? width / 2 : 0) + this.width / 4, this.y, 180 - this.width / 4, getColor().ordinal() * 36 + i * 12, this.width / 2, this.height);
			}

			draw.apply(poseStack, this);

		}
	}

	public static class HiddenButton extends MenuButton
	{

		public HiddenButton(int x, int y, int width, int height, OnPress onPress, OnTooltip onTooltip, PostDraw draw) {
			super(x, y, width, height, onPress, onTooltip, draw, ButtonColor.GREEN);
		}

		@Override
		public void renderButton(PoseStack poseStack)
		{

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();

			draw.apply(poseStack, this);
		}
	}

	public static class SuperJumpMenuButton extends MenuButton
	{
		final Stage stage;
		private ButtonState state = ButtonState.REQUESTING;

		public static boolean loading = false;

		public SuperJumpMenuButton(int x, int y, int width, OnTooltip onTooltip, PostDraw draw, Stage stage)
		{
			super(x, y, width, (b) ->
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
}
