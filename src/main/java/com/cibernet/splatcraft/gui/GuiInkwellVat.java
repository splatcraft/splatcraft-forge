package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.gui.container.ContainerInkwellVat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class GuiInkwellVat extends GuiContainer
{
	public GuiInkwellVat(InventoryPlayer player)
	{
		super(new ContainerInkwellVat(player));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
	
	}
}
