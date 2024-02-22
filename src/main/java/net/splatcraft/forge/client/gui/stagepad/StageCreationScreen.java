package net.splatcraft.forge.client.gui.stagepad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.StagePadItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestStageCreatePacket;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class StageCreationScreen extends AbstractStagePadScreen
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/stage_pad/stage_create.png");

	protected String stageId = "";

	public static BlockPos corner1;
	public static BlockPos corner2;
	static String savedName = "";
	@Nullable //null when stage creation screen was closed via escape key, opens creation menu back up without setting a corner pos
	static Boolean setCorner1 = null;

	private MenuTextBox stageName;
	private final MenuButton createButton;

	private boolean pendingCreation = false;

	public StageCreationScreen(Component label, @Nullable Screen parent, String savedStageName, @Nullable BlockPos cornerA, @Nullable BlockPos cornerB)
	{
		super(label, SET_STAGE_BOUNDS);

		addButton(new MenuButton(51, 107, 50, 12, new TranslatableComponent("gui.stage_pad.button.cancel"), true, (b) -> getMinecraft().setScreen(parent),
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.RED));
		addButton(new MenuButton(167, 70, 30, 12, new TranslatableComponent("gui.stage_pad.button.set_corner"), true, (b) -> clickSetCornerButton(b, true),
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.GREEN));
		addButton(new MenuButton(167, 88, 30, 12, new TranslatableComponent("gui.stage_pad.button.set_corner"), true, (b) -> clickSetCornerButton(b, false),
				Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.GREEN));
		createButton = addButton(new MenuButton(107, 107, 50, 12, new TranslatableComponent("gui.stage_pad.button.create"), true, (b) ->
		{
			if(canCreate())
			{
				SplatcraftPacketHandler.sendToServer(new RequestStageCreatePacket(stageId, new TextComponent(this.stageName.getValue()), corner1, corner2));

				buttons.forEach(button -> button.active = false);
				this.stageName.setFocus(false);
				pendingCreation = true;
			}
		}, Button.NO_TOOLTIP, (ps, b) -> {}, MenuButton.ButtonColor.LIME));

		addTextBox(font -> {
			this.stageName = new MenuTextBox(font, 17, 40, 178, 12, new TranslatableComponent("gui.stage_pad.label.set_stage_name.textbox"), false);
			this.stageName.setValue(savedStageName);
			this.stageName.setFocus(true);
			return this.stageName;
		});


		corner1 = cornerA;
		corner2 = cornerB;
		savedName = savedStageName;
	}

	public StageCreationScreen(Component label, @Nullable Screen parent)
	{
		this(label, parent, savedName, corner1, corner2);
	}

	@Override
	protected void init() {
		super.init();
	}

	protected void clickSetCornerButton(Button button, boolean isCorner1)
	{
		setCorner1 = isCorner1;
		minecraft.setScreen(null);
	}

	public static final StagePadItem.UseAction SET_STAGE_BOUNDS = ((level, player, hand, stack, pos) ->
	{
		if(setCorner1 != null && pos != null)
		{
			if(setCorner1)
				corner1 = pos;
			else corner2 = pos;
		}

		Minecraft.getInstance().setScreen(new StageCreationScreen(stack.getDisplayName(), new StageSelectionScreen(stack.getDisplayName())));
		setCorner1 = null;
	});

	@Override
	public void handleWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
	{
		if(pendingCreation)
			return;

		if(!savedName.equals(stageName.getValue()))
		{
			savedName = stageName.getValue();
			String newId = stageName.getValue().replace(' ', '_');
			if(getMinecraft().getSingleplayerServer() != null && !newId.isEmpty())
			{
				HashMap<String, Stage> stages = SaveInfoCapability.get(getMinecraft().getSingleplayerServer()).getStages();
				for (int i = 1; stages.containsKey(newId); i++)
					newId = stageId + "_" + i;
			} else newId = "";

			stageId = newId;
		}

		createButton.active = canCreate();
	}

	private boolean canCreate() {
		return !stageId.isEmpty() && corner1 != null && corner2 != null;
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

		if(corner1 != null)
		{
			font.draw(poseStack, getShortenedInt(corner1.getX()), x + 64, y + 73, 0xFFFFFF);
			font.draw(poseStack, getShortenedInt(corner1.getY()), x + 98, y + 73, 0xFFFFFF);
			font.draw(poseStack, getShortenedInt(corner1.getZ()), x + 132, y + 73, 0xFFFFFF);

		}
		if(corner2 != null)
		{
			font.draw(poseStack, getShortenedInt(corner2.getX()), x + 64, y + 91, 0xFFFFFF);
			font.draw(poseStack, getShortenedInt(corner2.getY()), x + 98, y + 91, 0xFFFFFF);
			font.draw(poseStack, getShortenedInt(corner2.getZ()), x + 132, y + 91, 0xFFFFFF);

		}

	}

	public String getStageId() {
		return stageId;
	}

	protected String getShortenedInt(int v)
	{
		if(Integer.toString(v).length() > 5)
			for (NumberLetters nl : NumberLetters.values())
			{
				if(nl.minValue <= Math.abs(v))
					return Integer.toString((int) (v / nl.minValue)) + nl.letter;
			}

		return String.valueOf(v);
	}

	enum NumberLetters
	{
		BILLION(10, 'B'),
		MILLION(7, 'M'),
		THOUSAND(4, 'K'),
		;
		final int length;
		final double minValue;
		final char letter;


		NumberLetters(int length, char letter)
		{
			this.length = length;
			minValue = Math.pow(10, (length-1));
			this.letter = letter;
		}
	}
}
