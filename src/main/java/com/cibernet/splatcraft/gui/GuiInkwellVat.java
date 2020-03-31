package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.gui.container.ContainerInkwellVat;
import com.cibernet.splatcraft.network.PacketSetVatOutput;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.tileentities.TileEntityInkwellVat;
import com.cibernet.splatcraft.utils.InkColors;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class GuiInkwellVat extends GuiContainer
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(SplatCraft.MODID, "textures/gui/inkwell_crafting.png");
	private final InventoryPlayer player;
	private final TileEntityInkwellVat te;

	private float scroll = 0.0f;
	private List<InkColors> colorSelection;
	private int clientSelectedColor = -1;
	private boolean scrolling = false;

	public GuiInkwellVat(InventoryPlayer player, TileEntityInkwellVat te)
	{
		super(new ContainerInkwellVat(player,te));
		this.player = player;
		this.te = te;
		clientSelectedColor = te.getField(0);
		ySize = 208;
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		colorSelection = te.getColorList();
		clientSelectedColor = te.getField(0);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURES);
		drawTexturedModalRect(guiLeft, guiTop, 0 ,0, xSize, ySize);

		TileEntityInkwellVat inventory = te;
		if(inventory.getStackInSlot(0).isEmpty())
			drawTexturedModalRect(guiLeft + 26, guiTop + 70, 176, 0, 16, 16);
		if(inventory.getStackInSlot(1).isEmpty())
			drawTexturedModalRect(guiLeft + 46, guiTop + 70, 192, 0, 16, 16);
		if(inventory.getStackInSlot(2).isEmpty())
			drawTexturedModalRect(guiLeft + 92, guiTop + 82, 208, 0, 16, 16);
		if(inventory.getStackInSlot(3).isEmpty())
			drawTexturedModalRect(guiLeft + 36, guiTop + 89, 224, 0, 16, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String displayName = te.getDisplayName().getUnformattedComponentText();
		fontRenderer.drawString(displayName, (xSize/2 - fontRenderer.getStringWidth(displayName)/2), 4, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

		mc.getTextureManager().bindTexture(TEXTURES);
		if(colorSelection != null)
		{
			int sc = (int) Math.ceil(Math.max(0, (colorSelection.size()-16) * scroll));
			sc += sc%2;
			for(int i = sc; i < colorSelection.size() && i-sc < 16; i++)
			{
				int color = colorSelection.get(i).getColor();
				
				float r = (float) (color >> 16 & 255) / 255.0F;
				float g = (float) (color >> 8 & 255) / 255.0F;
				float b = (float) (color & 255) / 255.0F;
				
				int x = 12 + (i-sc)/ 2 * 19;
				int y = 16 + ((i-sc) % 2) * 18;
				
				GlStateManager.color(r, g, b);
				drawTexturedModalRect(x, y, 34, 220, 19, 18);
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				if(i == clientSelectedColor)
					drawTexturedModalRect(x, y, 34, 238, 19, 18);
			}
			
			boolean isScrolling = isPointInRegion(15, 55, 146, 10, mouseX, mouseY) || scrolling;
			if(colorSelection.size()-16 > 0)
				drawTexturedModalRect((int)(15 + 132*scroll), 55, 241, isScrolling ? 20 : 0, 15, 10);
			else drawTexturedModalRect(15, 55, 241, 10, 15, 10);
			
		} else
		{
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(15, 55, 241, 10, 15, 10);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		int selecCol = te.getField(0);
		
		if(colorSelection != null)
		{
			int sc = (int) Math.ceil(Math.max(0, (colorSelection.size()-16) * scroll));
			sc += sc%2;
			for (int i = sc; i < colorSelection.size() && i-sc < 16; i++)
			{
				int x = 12 + (i-sc)/ 2 * 19;
				int y = 16 + ((i-sc) % 2) * 18;
				
				if(isPointInRegion(x,y, 19, 18, mouseX, mouseY) && mouseButton == 0)
					selecCol = i;
			}
		}
		else selecCol = -1;
		clientSelectedColor = selecCol;
		te.setField(0, selecCol);
		SplatCraftPacketHandler.instance.sendToServer(new PacketSetVatOutput(te, te.getRecipeStack(), clientSelectedColor));
		super.mouseClicked(mouseX, mouseY, mouseButton);

	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int mouseScroll = Mouse.getEventDWheel();
		float maxScroll = 0;
		float sc = scroll;
		
		if(isPointInRegion(15, 55, 146, 10, mouseX, mouseY) && Mouse.isButtonDown(0))
			scrolling = true;
		else if(!Mouse.isButtonDown(0))
			scrolling = false;
		
		if(colorSelection != null)
			maxScroll = (float) Math.ceil(colorSelection.size()/2.0) - 8;
		
		if(maxScroll <= 0) return;
		
		if(scrolling)
			sc = (mouseX - (guiLeft+15))/146f;
		else if(mouseScroll != 0) sc += 1/maxScroll * -Math.signum(mouseScroll);
		
		scroll = Math.max(0, Math.min(1, sc));
		
	}
}
