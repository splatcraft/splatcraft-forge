package net.splatcraft.forge.tileentities.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.splatcraft.forge.registries.SplatcraftTileEntities;

public class WeaponWorkbenchContainer extends PlayerInventoryContainer<WeaponWorkbenchContainer>
{
    public WeaponWorkbenchContainer(Inventory player, ContainerLevelAccess callable, int id)
    {
        super(SplatcraftTileEntities.weaponWorkbenchContainer.get(), player, callable, 8, 144, id);
    }

    public WeaponWorkbenchContainer(int id, Inventory playerInventory)
    {
        this(playerInventory, ContainerLevelAccess.NULL, id);
    }
}
