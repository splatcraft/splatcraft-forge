package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestSetStageRulePacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StageRulesScreen extends AbstractStagePadScreen
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_rules.png");
	private Stage stage;
	private final HashMap<GameRules.Key<GameRules.BooleanValue>, RuleEntry> rules = new HashMap<>();

	public StageRulesScreen(Component label, String stageId, Screen mainMenu)
	{
		super(label);
		stage = Stage.getStage(Minecraft.getInstance().level, stageId);

		for (GameRules.Key<GameRules.BooleanValue> rule : Stage.VALID_SETTINGS.values())
		{
			rules.put(rule, new RuleEntry(
					addButton(new RuleNameLabel(rule)),
					addButton(new RuleValueButton(rule)), stage.getSetting(rule)));
		}

		addOptionsTabs(label, stageId, mainMenu);
	}

	@Override
	public void onStagesUpdate()
	{
		stage = Stage.getStage(Minecraft.getInstance().level, stage.id);
		rules.forEach((gameRule, value) -> value.value = stage.getSetting(gameRule));
	}

	@Override
	public void handleWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
	{
		int index = 0;
		for(RuleEntry rule : rules.values())
		{
			int i = index - (int)(scroll * Math.max(0, rules.size() - 8));
			MenuButton ruleLabel = rule.label;
			MenuButton ruleButton = rule.button;

			if(i >= 8 || i < 0)
			{
				ruleLabel.visible = false;
				ruleButton.visible = false;
			}
			else
			{
				ruleLabel.relativeY = (i + 2) * 12;
				ruleLabel.visible = true;
				ruleButton.relativeY = (i + 2) * 12;
				ruleButton.visible = true;
			}

			index++;
		}
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
		blit(poseStack, x + 188, y + 24 + (int) (scroll * 81), 196 + (rules.size() > 8 ? (scrollBarHeld ? 2 : 1) * 12 : 0), 0, 12, 15);
	}

	private boolean scrollBarHeld = false;
	private double scroll = 0;

	@Override
	public boolean canClickButtons() {
		return !scrollBarHeld;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int clickButton)
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		if(rules.size() > 8 && isMouseOver(mouseX, mouseY, x + 190, y + 24, x + 200, y + 120))
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
		int y = (height - imageHeight) / 2;

		if(scrollBarHeld)
			scroll = Mth.clamp((mouseY - (y + 24)) / 96f, 0, 1);

		return super.mouseDragged(mouseX, mouseY, mouseButton, p_94702_, p_94703_);
	}


	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (rules.size() >= 8)
			scroll = Mth.clamp(scroll - Math.signum(amount) / (rules.size() - 8), 0.0f, 1.0f);

		return true;
	}

	class RuleEntry
	{
		public final RuleNameLabel label;
		public final RuleValueButton button;
		public Boolean value;

		RuleEntry(RuleNameLabel label, RuleValueButton button, Boolean value)
		{
			this.label = label;
			this.button = button;
			this.value = value;
		}
	}

	class RuleNameLabel extends MenuButton
	{

		public RuleNameLabel(GameRules.Key<GameRules.BooleanValue> rule)
		{
			super(10,24, 144, 12, (b) -> {}, ((button, poseStack, mouseX, mouseY) ->
			{

				ArrayList<FormattedCharSequence> lines = new ArrayList<>(font.split(new TranslatableComponent(rule.getDescriptionId()), 150));

				lines.add(new TextComponent(rule.getId().replace(Splatcraft.MODID + ".", "")).withStyle(ChatFormatting.YELLOW).getVisualOrderText());

				String descriptionKey = rule.getDescriptionId() + ".description";

				if(I18n.exists(descriptionKey))
					lines.addAll(font.split(new TranslatableComponent(descriptionKey).withStyle(ChatFormatting.GRAY), 150));

				renderTooltip(poseStack, lines, mouseX, mouseY);
			}), (ps, b) -> {}, ButtonColor.GREEN);
			setMessage(new TranslatableComponent(rule.getDescriptionId()));
		}

		@Override
		public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
		{
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, TEXTURES);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			this.blit(poseStack, this.x, this.y, 0, 244, width, height);

			Component label = getMessage();
			FormattedCharSequence sequence = label.getVisualOrderText();

			if(font.width(label) > width)
				sequence = Language.getInstance().getVisualOrder(FormattedText.composite(Arrays.asList(font.substrByWidth(label, width - 12), FormattedText.of("...", Style.EMPTY))));

			drawString(poseStack, font, sequence, x + (3), y + (getHeight() - 8) / 2, getFGColor() | Mth.ceil(getAlpha() * 255.0F) << 24);
		}

		@Override
		public void playDownSound(SoundManager soundManager) {

		}
	}

	class RuleValueButton extends MenuButton
	{
		static RuleValueButton heldButton = null;
		final GameRules.Key<GameRules.BooleanValue> rule;

		public RuleValueButton(GameRules.Key<GameRules.BooleanValue> rule)
		{
			super(154, 24, 34, 12, (b) -> {}, (button, poseStack, mouseX, mouseY) ->
			{
				if(heldButton == null || heldButton.rule.equals(rule) /*can't ref to self before super, so this'll have to do*/)
				{
					Boolean value = rules.get(rule).value;
					showText(value == null ? new TranslatableComponent("gui.stage_pad.button.rule_value.default",
							new TranslatableComponent("gui.stage_pad.button.rule_value." + (SplatcraftGameRules.getClientsideBooleanValue(rule) ? "on" : "off"))) :
							new TranslatableComponent("gui.stage_pad.button.rule_value." + (value ? "on" : "off"))).onTooltip(button, poseStack, mouseX, mouseY);
				}
			}, (poseStack, button) -> {}, ButtonColor.LIME);

			this.rule = rule;
		}

		@Override
		public void onRelease(double mouseX, double mouseY)
		{
			super.onRelease(mouseX, mouseY);

			if(this.equals(heldButton))
			{
				SplatcraftPacketHandler.sendToServer(new RequestSetStageRulePacket(stage.id, rule.getId(), rules.get(rule).value));
				heldButton = null;
			}
		}
		@Override
		public void onClick(double mouseX, double mouseY)
		{
			super.onClick(mouseX, mouseY);

			if(heldButton == null)
				heldButton = this;

			setValue(rules.get(rule), mouseX);
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double p_94702_, double p_94703_)
		{
			if(this.equals(heldButton))
			{
				RuleEntry ruleEntry = rules.get(rule);
				Boolean prevValue = ruleEntry.value;

				setValue(ruleEntry, mouseX);

				if(!(prevValue == null && ruleEntry.value == null) && (ruleEntry.value == null || !ruleEntry.value.equals(prevValue)))
					playDownSound(Minecraft.getInstance().getSoundManager());


			}
			return super.mouseDragged(mouseX, mouseY, mouseButton, p_94702_, p_94703_);
		}

		void setValue(RuleEntry entry, double mouseX)
		{
			entry.value = new Boolean[]{false, null, true}[Mth.clamp((int)((mouseX - x) / width * 3), 0, 2)];
		}

		@Override
		public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
		{
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			int i = this.getYImage(this.isHoveredOrFocused());
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderTexture(0, TEXTURES);
			this.blit(poseStack, this.x, this.y, 144, 244, this.width, this.height);

			Boolean bool = rules.get(rule).value;
			int j = bool == null ? 1 : bool ? 2 : 0;

			int notchWidth = 12;
			RenderSystem.setShaderTexture(0, WIDGETS);
			this.blit(poseStack, this.x + (j * (notchWidth - 1)), this.y, 0, getColor().ordinal() * 36 + i * 12, notchWidth / 2, this.height);
			this.blit(poseStack, this.x + (j * (notchWidth - 1)) + notchWidth / 2, this.y, 180 - notchWidth / 2, getColor().ordinal() * 36 + i * 12, notchWidth / 2, this.height);

			drawIcon(WIDGETS, (j * width / 3), 0, 220 + j * 12, 36, 12, 12).apply(poseStack, this);

		}

		@Override
		protected int getYImage(boolean hovered) {
			return super.getYImage(heldButton == null ? hovered : equals(heldButton));
		}

		@Override
		public ButtonColor getColor()
		{
			Boolean value = rules.get(rule).value;
			return value == null ? ButtonColor.CYAN : value ? ButtonColor.LIME : ButtonColor.RED;
		}
	}
}
