package com.cibernet.splatcraft.data.capabilities.playerinfo;

import com.cibernet.splatcraft.registries.SplatcraftInkColors;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCharge;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class PlayerInfo implements IPlayerInfo
{
    private int color;
    private boolean isSquid = false;
    private boolean initialized = false;
    private NonNullList<ItemStack> matchInventory = NonNullList.create();
    private PlayerCooldown playerCooldown = null;
    private PlayerCharge playerCharge = null;
    private InkBlockUtils.InkType inkType = InkBlockUtils.InkType.NORMAL;

    public PlayerInfo(int defaultColor)
    {
        color = defaultColor;
    }

    public PlayerInfo()
    {
        this(SplatcraftInkColors.undyed.getColor());
    }

    @Override
    public boolean isInitialized()
    {
        return initialized;
    }

    @Override
    public void setInitialized(boolean init)
    {
        initialized = init;
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    public void setColor(int color)
    {
        this.color = color;
    }

    @Override
    public boolean isSquid()
    {
        return isSquid;
    }

    @Override
    public void setIsSquid(boolean isSquid)
    {
        this.isSquid = isSquid;
    }

    @Override
    public InkBlockUtils.InkType getInkType() {
        return inkType;
    }

    @Override
    public void setInkType(InkBlockUtils.InkType type)
    {
        inkType = type;
    }

    @Override
    public NonNullList<ItemStack> getMatchInventory()
    {
        return matchInventory;
    }

    @Override
    public void setMatchInventory(NonNullList<ItemStack> inventory)
    {
        this.matchInventory = inventory;
    }

    @Override
    public PlayerCooldown getPlayerCooldown()
    {
        return playerCooldown;
    }

    @Override
    public void setPlayerCooldown(PlayerCooldown cooldown)
    {
        this.playerCooldown = cooldown;
    }

    @Override
    public boolean hasPlayerCooldown()
    {
        return playerCooldown != null && playerCooldown.getTime() > 0;
    }

    @Override
    public PlayerCharge getPlayerCharge()
    {
        return playerCharge;
    }

    @Override
    public void setPlayerCharge(PlayerCharge charge)
    {
        playerCharge = charge;
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT nbt)
    {
        nbt.putInt("Color", getColor());
        nbt.putBoolean("IsSquid", isSquid());
        nbt.putString("InkType", getInkType().getString());
        nbt.putBoolean("Initialized", initialized);

        if (!matchInventory.isEmpty())
        {
            CompoundNBT invNBT = new CompoundNBT();
            ItemStackHelper.saveAllItems(invNBT, matchInventory);
            nbt.put("MatchInventory", invNBT);
        }

        if (playerCooldown != null)
        {
            CompoundNBT cooldownNBT = new CompoundNBT();
            playerCooldown.writeNBT(cooldownNBT);
            nbt.put("PlayerCooldown", cooldownNBT);
        }

        return nbt;
    }

    @Override
    public void readNBT(CompoundNBT nbt)
    {
        setColor(nbt.getInt("Color"));
        setIsSquid(nbt.getBoolean("IsSquid"));
        setInkType(InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("InkType")), InkBlockUtils.InkType.NORMAL));
        setInitialized(nbt.getBoolean("Initialized"));

        if (nbt.contains("MatchInventory"))
        {
            NonNullList<ItemStack> nbtInv = NonNullList.withSize(41, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(nbt.getCompound("MatchInventory"), nbtInv);
            setMatchInventory(nbtInv);
        }

        if (nbt.contains("PlayerCooldown"))
        {
            setPlayerCooldown(PlayerCooldown.readNBT(nbt.getCompound("PlayerCooldown")));
        }
    }
}
