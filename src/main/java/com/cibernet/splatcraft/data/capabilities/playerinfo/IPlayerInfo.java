package com.cibernet.splatcraft.data.capabilities.playerinfo;

import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
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
