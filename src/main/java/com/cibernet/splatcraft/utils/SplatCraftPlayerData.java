package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.SplatCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class SplatCraftPlayerData
{
	
	static Map<UUID, PlayerData> dataMap = new HashMap();
	
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
	
	public static int getInkColor(EntityPlayer playerIn) { return getPlayerData(playerIn).inkColor; }
	public static boolean getIsSquid(EntityPlayer playerIn) { return getPlayerData(playerIn).isSquid; }
	
	public static void setInkColor(EntityPlayer playerIn, int inkColor) {getPlayerData(playerIn).inkColor = inkColor;}
	public static void setIsSquid(EntityPlayer playerIn, boolean isSquid) {getPlayerData(playerIn).isSquid = isSquid;}
	
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
}
