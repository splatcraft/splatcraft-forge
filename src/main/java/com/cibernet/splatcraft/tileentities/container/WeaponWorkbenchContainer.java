package com.cibernet.splatcraft.tileentities.container;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class WeaponWorkbenchContainer extends PlayerInventoryContainer<WeaponWorkbenchContainer>
{
	public WeaponWorkbenchContainer(PlayerInventory player, BlockPos pos, int id)
	{
		super(SplatcraftTileEntitites.weaponWorkbenchContainer, player, pos, 8, 120, id);
	}
	
	public 	WeaponWorkbenchContainer(final int windowId, final PlayerInventory inv, final PacketBuffer buffer)
	{
		this(inv, buffer.readBlockPos(), windowId);
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return isWithinUsableDistance(callableInteract, playerIn, SplatcraftBlocks.weaponWorkbench);
	}
}
