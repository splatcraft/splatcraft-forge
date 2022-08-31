package net.splatcraft.forge.tileentities.container;

import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;

public class WeaponWorkbenchContainer extends PlayerInventoryContainer<WeaponWorkbenchContainer>
{
    public WeaponWorkbenchContainer(PlayerInventory player, IWorldPosCallable callable, int id)
    {
        super(SplatcraftTileEntitites.weaponWorkbenchContainer, player, callable, 8, 144, id);
    }

    public WeaponWorkbenchContainer(int id, PlayerInventory playerInventory)
    {
        this(playerInventory, IWorldPosCallable.NULL, id);
    }
}
