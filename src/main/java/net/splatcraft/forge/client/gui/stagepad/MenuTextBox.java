package net.splatcraft.forge.client.gui.stagepad;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class MenuTextBox extends EditBox
{
	int relativeX;
	int relativeY;
	public MenuTextBox(Font font, int x, int y, int width, int height, Component unfocusedText, boolean bordered)
	{
		super(font, x, y, width, height, unfocusedText);
		relativeX = x;
		relativeY = y;
		setBordered(bordered);
	}

	public interface Factory
	{
		MenuTextBox newInstance(Font font);
	}

	public interface Setter
	{
		void apply();
	}
}
