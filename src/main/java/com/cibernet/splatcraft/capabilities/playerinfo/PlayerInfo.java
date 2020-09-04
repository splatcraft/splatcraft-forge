package com.cibernet.splatcraft.capabilities.playerinfo;

import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class PlayerInfo implements IPlayerInfo
{
	private int color = ColorUtils.getRandomStarterColor();
	private boolean isSquid = false;
	private boolean initialized = false;
	private NonNullList<ItemStack> matchInventory = NonNullList.create();
	private PlayerCooldown playerCooldown = null;
	
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
	public CompoundNBT writeNBT(CompoundNBT nbt)
	{
		nbt.putInt("Color",getColor());
		nbt.putBoolean("IsSquid", isSquid());
		nbt.putBoolean("Initialized", initialized);
		
		if(!matchInventory.isEmpty())
		{
			CompoundNBT invNBT = new CompoundNBT();
			ItemStackHelper.saveAllItems(invNBT, matchInventory);
			nbt.put("MatchInventory", invNBT);
		}
		
		if(playerCooldown != null)
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
		setInitialized(nbt.getBoolean("Initialized"));
		
		if(nbt.contains("MatchInventory"))
		{
			NonNullList<ItemStack> nbtInv = NonNullList.withSize(41, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(nbt.getCompound("MatchInventory"), nbtInv);
			setMatchInventory(nbtInv);
		}
		
		if(nbt.contains("PlayerCooldown"))
			setPlayerCooldown(PlayerCooldown.readNBT(nbt.getCompound("PlayerCooldown")));
	}
}
