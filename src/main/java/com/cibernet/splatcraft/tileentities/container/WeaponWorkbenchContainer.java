package com.cibernet.splatcraft.tileentities.container;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

public class WeaponWorkbenchContainer extends PlayerInventoryContainer<WeaponWorkbenchContainer>
{
	public WeaponWorkbenchContainer(PlayerInventory player, IWorldPosCallable callable, int id)
	{
		super(SplatcraftTileEntitites.weaponWorkbenchContainer, player, callable, 8, 144, id);
	}

	public 	WeaponWorkbenchContainer(int id, PlayerInventory playerInventory)
	{
		this(playerInventory, IWorldPosCallable.DUMMY, id);
	}
}
