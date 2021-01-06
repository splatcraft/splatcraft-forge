package com.cibernet.splatcraft.client.gui;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.crafting.InkVatColorRecipe;
import com.cibernet.splatcraft.tileentities.InkVatTileEntity;
import com.cibernet.splatcraft.tileentities.container.InkVatContainer;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class InkVatScreen extends ContainerScreen<InkVatContainer>
{
	
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/inkwell_crafting.png");
	
	private static final int colorSelectionX = 12;
	private static final int colorSelectionY = 16;
	
	private static final int scrollBarX = 15;
	private static final int scrollBarY = 55;
	private boolean scrolling = false;
	
	private boolean canScroll = false;
	private float maxScroll = 0;
	
	private float scroll = 0.0f;
	
	public InkVatScreen(InkVatContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		this.ySize = 208;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = this.ySize - 98;
		
		
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		List<Integer> colorSelection = container.sortRecipeList();
		
		super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
		int sc = (int) Math.ceil(Math.max(0, (colorSelection.size()-16) * scroll));
		sc += sc%2;
		
		for (int i = sc; i < colorSelection.size() && i-sc < 16; i++)
		{
			int x = colorSelectionX + (i - sc) / 2 * 19;
			int y = colorSelectionY + ((i - sc) % 2) * 18;
			
			if(isPointInRegion(x, y, 18, 16, mouseX, mouseY))
				renderTooltip(matrixStack, ColorUtils.getFormatedColorName(colorSelection.get(i), false), mouseX, mouseY);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		
		font.drawString(matrixStack, title.getUnformattedComponentText(), (xSize/2 - font.getStringWidth(title.getUnformattedComponentText())/2), 4, 4210752);
		
		List<Integer> colors = container.sortRecipeList();
		drawAvailableColors(matrixStack, colors, colorSelectionX, colorSelectionY);
		canScroll = colors.size() > 16;
		maxScroll = (float) Math.ceil(colors.size()/2.0) - 8;
		
		drawScrollBar(matrixStack, scrollBarX, scrollBarY, 132, mouseX, mouseY);
	}
	
	protected void drawAvailableColors(MatrixStack matrixStack, List<Integer> colorSelection, int x, int y)
	{
		minecraft.getTextureManager().bindTexture(TEXTURES);
		int sc = (int) Math.ceil(Math.max(0, (colorSelection.size()-16) * scroll));
		sc += sc%2;
		for(int i = sc; i < colorSelection.size() && i-sc < 16; i++)
		{
			int color = colorSelection.get(i);
			
			
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			
			int cx = x + (i-sc)/ 2 * 19;
			int cy = y + ((i-sc) % 2) * 18;
			
			RenderSystem.color4f(r, g, b, 1);
			blit(matrixStack, cx, cy, 34, 220, 19, 18);
			RenderSystem.color4f(1, 1, 1, 1);
			
			if(container.getSelectedRecipe() == i)
				blit(matrixStack, cx, cy, 34, 238, 19, 18);
			
		}
	}
	
	protected void drawScrollBar(MatrixStack matrixStack, int x, int y, int width, int mouseX, int mouseY)
	{
		minecraft.getTextureManager().bindTexture(TEXTURES);
		if(canScroll)
			blit(matrixStack, (int) (x + (width*scroll)), y, 241, isPointInRegion(15, 55, 146, 10, mouseX, mouseY) || scrolling ? 20 : 0, 15, 10);
		else blit(matrixStack, x, y, 241, 10, 15, 10);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1, 1, 1,1);
		minecraft.getTextureManager().bindTexture(TEXTURES);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		blit(matrixStack, x, y, 0, 0, xSize, ySize);
		
		InkVatTileEntity te = container.te;
		if(te.getStackInSlot(0).isEmpty())
			blit(matrixStack, guiLeft + 26, guiTop + 70, 176, 0, 16, 16);
		if(te.getStackInSlot(1).isEmpty())
			blit(matrixStack, guiLeft + 46, guiTop + 70, 192, 0, 16, 16);
		if(te.getStackInSlot(2).isEmpty())
			blit(matrixStack, guiLeft + 92, guiTop + 82, 208, 0, 16, 16);
		if(te.getStackInSlot(3).isEmpty())
			blit(matrixStack, guiLeft + 36, guiTop + 89, 224, 0, 16, 16);
	}
	
	
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		List<Integer> colorSelection = container.sortRecipeList();
		scrolling = false;
		
		int sc = (int) Math.ceil(Math.max(0, (colorSelection.size()-16) * scroll));
		sc += sc%2;
		
		for (int i = sc; i < colorSelection.size() && i-sc < 16; i++)
		{
			int x = colorSelectionX + (i-sc)/ 2 * 19;
			int y = colorSelectionY + ((i-sc) % 2) * 18;
			
			if(isPointInRegion(x,y, 19, 18, mouseX, mouseY) && mouseButton == 0 && this.container.enchantItem(this.minecraft.player, i))
			{
				Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
				this.minecraft.playerController.sendEnchantPacket((this.container).windowId, i);
				container.updateInkVatColor(i, colorSelection.get(i));
			}
		}
		
		if(isPointInRegion(scrollBarX, scrollBarY, 146, 10, mouseX, mouseY) && canScroll)
		{
			scrolling = true;
			scroll = MathHelper.clamp((float) ((mouseX - guiLeft - scrollBarX)) / 132f, 0f, 1f);
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(button == 0)
			scrolling = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double x, double y, int mouseButton, double p_231045_6_, double p_231045_8_)
	{
		if(scrolling && canScroll)
		{
			int i = this.guiTop + scrollBarX;
			int j = i + 132;
			scroll = MathHelper.clamp((float) ((x - guiLeft - scrollBarX)) / 132f, 0f, 1f);
		}
		
		return super.mouseDragged(x, y, mouseButton, p_231045_6_, p_231045_8_);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if(canScroll)
			scroll = MathHelper.clamp(scroll + 1/maxScroll * -Math.signum((float)amount), 0.0f, 1.0f);
		
		return true;
	}
}
