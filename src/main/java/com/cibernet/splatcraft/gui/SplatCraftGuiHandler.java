package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.gui.container.ContainerInkwellVat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class SplatCraftGuiHandler implements IGuiHandler
{
	public static final int INKWELL_VAT_GUI = 0;
	
	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
			case INKWELL_VAT_GUI:
				return new ContainerInkwellVat(player.inventory);
		}
		return null;
	}
	
	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
			case INKWELL_VAT_GUI:
				return new GuiInkwellVat(player.inventory);
		}
		return null;
	}
}
