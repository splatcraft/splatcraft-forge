package net.splatcraft.forge.data.capabilities.playerinfo;

import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class PlayerInfo implements IPlayerInfo
{
    private int color;
    private boolean isSquid = false;
    private boolean initialized = false;
    private NonNullList<ItemStack> matchInventory = NonNullList.create();
    private PlayerCooldown playerCooldown = null;
    private PlayerCharge playerCharge = null;

    private ItemStack inkBand = ItemStack.EMPTY;

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
    public ItemStack getInkBand() {
        return inkBand;
    }

    @Override
    public void setInkBand(ItemStack stack) {
        inkBand = stack;
    }

    @Override
    public InkBlockUtils.InkType getInkType() {
        return InkBlockUtils.getInkTypeFromStack(inkBand);
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
        nbt.putString("InkType", getInkType().getSerializedName());
        nbt.putBoolean("Initialized", initialized);

        if(!inkBand.isEmpty())
            nbt.put("InkBand", getInkBand().serializeNBT());

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
        setColor(ColorUtils.getColorFromNbt(nbt));
        setIsSquid(nbt.getBoolean("IsSquid"));
        setInitialized(nbt.getBoolean("Initialized"));

        if(nbt.contains("InkBand"))
            setInkBand(ItemStack.of(nbt.getCompound("InkBand")));
        else setInkBand(ItemStack.EMPTY);

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
