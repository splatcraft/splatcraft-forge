package net.splatcraft.forge.data.capabilities.playerinfo;

import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public interface IPlayerInfo
{
    boolean isInitialized();

    void setInitialized(boolean init);

    int getColor();

    void setColor(int color);

    boolean isSquid();

    void setIsSquid(boolean isSquid);

    InkBlockUtils.InkType getInkType();
    void setInkType(InkBlockUtils.InkType type);

    int getInkTypeData();
    void setInkTypeData(int data);
    boolean hasInkTypeData();

    NonNullList<ItemStack> getMatchInventory();

    void setMatchInventory(NonNullList<ItemStack> inventory);

    PlayerCooldown getPlayerCooldown();

    void setPlayerCooldown(PlayerCooldown cooldown);

    boolean hasPlayerCooldown();

    PlayerCharge getPlayerCharge();

    void setPlayerCharge(PlayerCharge charge);

    CompoundNBT writeNBT(CompoundNBT nbt);

    void readNBT(CompoundNBT nbt);
}
