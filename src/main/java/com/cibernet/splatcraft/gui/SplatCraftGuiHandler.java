package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.gui.container.ContainerInkwellVat;
import com.cibernet.splatcraft.gui.container.ContainerPlayerInv;
import com.cibernet.splatcraft.tileentities.TileEntityInkwellVat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class SplatCraftGuiHandler implements IGuiHandler
{
	public static final int INKWELL_VAT_GUI = 0;
	public static final int WEAPON_STATION_GUI = 1;
	
	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x,y,z);
		TileEntity te = world.getTileEntity(pos);
		switch(ID)
		{
			case INKWELL_VAT_GUI:
				return new ContainerInkwellVat(player, (TileEntityInkwellVat) te);
			case WEAPON_STATION_GUI:
				return new ContainerPlayerInv(player.inventory, pos, 8, 120);
		}
		return null;
	}
	
	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x,y,z);
		TileEntity te = world.getTileEntity(pos);
		switch(ID)
		{
			case INKWELL_VAT_GUI:
				return new GuiInkwellVat(player, (TileEntityInkwellVat) te);
			case WEAPON_STATION_GUI:
				return new GuiWeaponStation(player, pos);
		}
		return null;
	}
}
