package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ICharge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class SplatCraftPlayerData
{
	
	static Map<UUID, PlayerData> dataMap = new HashMap();
	static Map<UUID, TempPlayerData> tempDataMap = new HashMap();
	
	public static PlayerData getPlayerData(EntityPlayer playerIn)
	{
		Objects.requireNonNull(playerIn);
		return getPlayerData(playerIn.getUniqueID());
	}
	
	public static PlayerData getPlayerData(UUID uuid)
	{
		if(!dataMap.containsKey(uuid))
		{
			PlayerData data = new PlayerData(uuid);
			dataMap.put(uuid, data);
		}
		return dataMap.get(uuid);
	}
	public static TempPlayerData getTempPlayerData(EntityPlayer playerIn)
	{
		Objects.requireNonNull(playerIn);
		return getTempPlayerData(playerIn.getUniqueID());
	}
	
	public static TempPlayerData getTempPlayerData(UUID uuid)
	{
		if(!tempDataMap.containsKey(uuid))
		{
			TempPlayerData data = new TempPlayerData(uuid);
			tempDataMap.put(uuid, data);
		}
		return tempDataMap.get(uuid);
	}
	
	public static void deleteTempData(UUID uuid)
	{
		if(tempDataMap.containsKey(uuid))
			tempDataMap.remove(uuid);
	}
	public static void deleteTempData() {tempDataMap.clear();}
	
	public static int getInkColor(EntityPlayer playerIn) { return getPlayerData(playerIn).inkColor; }
	public static boolean getIsSquid(EntityPlayer playerIn) { return getPlayerData(playerIn).isSquid; }
	
	public static void setInkColor(EntityPlayer playerIn, int inkColor) {getPlayerData(playerIn).inkColor = inkColor;}
	public static void setIsSquid(EntityPlayer playerIn, boolean isSquid) {getPlayerData(playerIn).isSquid = isSquid;}
	
	public static boolean canDischarge(EntityPlayer player) {return getTempPlayerData(player).canDischarge;}
	public static void setCanDischarge(UUID player, boolean discharge) {getTempPlayerData(player).canDischarge = discharge;}
	public static float getWeaponCharge(EntityPlayer playerIn, ItemStack stack) { return getTempPlayerData(playerIn).chargedWeapon.isItemEqual(stack) ? getTempPlayerData(playerIn).charge : 0;}
	public static float getWeaponCharge(UUID playerIn, ItemStack stack) { return getTempPlayerData(playerIn).chargedWeapon.isItemEqual(stack) ? getTempPlayerData(playerIn).charge : 0;}
	public static void setWeaponCharge(EntityPlayer playerIn, ItemStack stack, float charge) {setWeaponCharge(playerIn.getUniqueID(), stack, charge);}
	public static void setWeaponCharge(UUID playerIn, ItemStack stack, float charge)
	{
		TempPlayerData data = getTempPlayerData(playerIn);
		data.charge = charge;
		data.chargedWeapon = stack;
	}
	public static void addWeaponCharge(EntityPlayer playerIn, ItemStack stack, float add)
	{
		if(!playerIn.world.isRemote)
			return;
		TempPlayerData data = getTempPlayerData(playerIn);
		if(data.chargedWeapon.isItemEqual(stack))
		{
			data.charge = Math.max(0, Math.min(data.charge + add, 1f));
		}
		else
		{
			data.charge = Math.max(0, add);
			data.chargedWeapon = stack;
		}
	}
	
	public static void dischargeWeapon(EntityPlayer player)
	{
		if(!player.world.isRemote)
			return;
		TempPlayerData data = getTempPlayerData(player);
		Item item = data.chargedWeapon.getItem();
		if(item instanceof ICharge)
		{
			data.charge = Math.max(0, data.charge - ((ICharge) item).getDischargeSpeed());
		}
		else data.charge = 0;
	}
	
	public static void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		Iterator var2 = dataMap.values().iterator();
		
		while(var2.hasNext()) {
			SplatCraftPlayerData.PlayerData data = (SplatCraftPlayerData.PlayerData)var2.next();
			list.appendTag(data.writeToNBT(new NBTTagCompound()));
		}
		
		nbt.setTag("playerData", list);
	}
	
	public static void readFromNBT(NBTTagCompound nbt) {
		dataMap.clear();
		if (nbt != null) {
			NBTTagList list = nbt.getTagList("playerData", 10);
			
			for(int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound dataCompound = list.getCompoundTagAt(i);
				SplatCraftPlayerData.PlayerData data = new SplatCraftPlayerData.PlayerData();
				data.readFromNBT(dataCompound);
				dataMap.put(data.player, data);
			}
			
		}
	}
	
	public static class PlayerData
	{
		public UUID player;
		public int inkColor = SplatCraft.DEFAULT_INK;
		public boolean isSquid = false;
		
		public PlayerData(UUID uuid) {player = uuid;}
		public PlayerData() {}
		
		private void readFromNBT(NBTTagCompound nbt)
		{
			if(nbt.hasUniqueId("uuid"))
				this.player = nbt.getUniqueId("uuid");
			else return;
			
			if(nbt.hasKey("inkColor"))
				this.inkColor = nbt.getInteger("inkColor");
			if(nbt.hasKey("isSquid"))
				this.isSquid = nbt.getBoolean("isSquid");
			
		}
		
		private NBTTagCompound writeToNBT(NBTTagCompound nbt)
		{
			nbt.setUniqueId("uuid", player);
			nbt.setInteger("inkColor", inkColor);
			nbt.setBoolean("isSquid", isSquid);
			
			return nbt;
		}
	}
	
	public static class TempPlayerData
	{
		public UUID player;
		public ItemStack chargedWeapon = ItemStack.EMPTY;
		public float charge = 0;
		public boolean canDischarge = false;
		
		public TempPlayerData(UUID uuid) {player = uuid;}
		public TempPlayerData() {}
	}
}
