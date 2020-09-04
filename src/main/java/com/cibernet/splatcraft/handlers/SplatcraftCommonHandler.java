package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.data.tags.SplatcraftTags;
import com.cibernet.splatcraft.items.InkTankItem;
import com.cibernet.splatcraft.network.*;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Map;

@Mod.EventBusSubscriber
public class SplatcraftCommonHandler
{
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerClone(final PlayerEvent.Clone event)
	{
		PlayerEntity player = event.getPlayer();
		PlayerInfoCapability.get(player).readNBT(PlayerInfoCapability.get(event.getOriginal()).writeNBT(new CompoundNBT()));
		
		NonNullList<ItemStack> matchInv = PlayerInfoCapability.get(player).getMatchInventory();
		
		if(!matchInv.isEmpty())
		{
			for(int i  = 0; i < matchInv.size(); i++)
			{
				ItemStack stack = matchInv.get(i);
				if(!stack.isEmpty() && !putStackInSlot(player.inventory, stack, i) && !player.inventory.addItemStackToInventory(stack))
					player.dropItem(stack, true, true);
			}
			
			PlayerInfoCapability.get(player).setMatchInventory(NonNullList.create());
		}
		PlayerCooldown.setPlayerCooldown(player, null);
	}
	
	private static boolean putStackInSlot(PlayerInventory inventory, ItemStack stack, int i)
	{
		ItemStack invStack = inventory.getStackInSlot(i);
		
		if(invStack.isEmpty())
		{
			inventory.setInventorySlotContents(i, stack);
			return true;
		}
		if(invStack.isItemEqual(stack))
		{
			int invCount = invStack.getCount();
			int count = Math.min(invStack.getMaxStackSize(), stack.getCount() + invStack.getCount());
			invStack.setCount(count);
			stack.shrink(count - invCount);
			
			return stack.isEmpty();
		}
		return false;
	}
	
	@SubscribeEvent
	public static void onLivingDeath(final LivingDeathEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST.CHEST);
		
		if(stack.getItem() instanceof InkTankItem)
			((InkTankItem) stack.getItem()).refill(stack);
	}
	
	@SubscribeEvent
	public static void onPlayerDeathDrops(LivingDropsEvent event)
	{
		if(event.getEntityLiving() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();
			NonNullList<ItemStack> matchInv = PlayerInfoCapability.get(player).getMatchInventory();
			
			for(ItemEntity drop : new ArrayList<>(event.getDrops()))
			{
				if(matchInv.contains(drop.getItem()))
					event.getDrops().remove(drop);
			}
			
			for(int i  = 0; i < matchInv.size(); i++)
			{
				ItemStack stack = matchInv.get(i);
				if(!stack.isEmpty() && !putStackInSlot(player.inventory, stack, i))
					player.inventory.addItemStackToInventory(stack);
			}
			
		}
	}
	
	@SubscribeEvent
	public static void onPlayerAboutToDie(LivingDamageEvent event)
	{
		if(!(event.getEntityLiving() instanceof PlayerEntity) || (event.getEntityLiving().getHealth()-event.getAmount()) > 0)
			return;
		
		PlayerEntity player = (PlayerEntity) event.getEntityLiving();
		if(!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && SplatcraftGameRules.getBooleanRuleValue(player.world, SplatcraftGameRules.KEEP_MATCH_ITEMS))
		{
			IPlayerInfo playerCapability;
			try { playerCapability = PlayerInfoCapability.get(player);}
			catch(NullPointerException e) {return;}
			
			if(playerCapability == null)
				return;
			
			NonNullList<ItemStack> matchInv = NonNullList.withSize(player.inventory.getSizeInventory(), ItemStack.EMPTY);
			
			for(int i = 0; i < matchInv.size(); i++)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(SplatcraftTags.Items.getTag(SplatcraftTags.Items.MATCH_ITEMS).contains(stack.getItem()))
					matchInv.set(i, stack);
			}
			
			playerCapability.setMatchInventory(matchInv);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		PlayerEntity player = event.getPlayer();
		SplatcraftPacketHandler.sendToPlayer(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.booleanRules), (ServerPlayerEntity) player);
		
		int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
		int i = 0;
		for(int c : ScoreboardHandler.getCriteriaKeySet())
			colors[i++] = c;
		
		SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, colors), (ServerPlayerEntity) player);
	}
	
	@SubscribeEvent
	public static void onDataReload(AddReloadListenerEvent event)
	{
	
	}
	
	@SubscribeEvent
	public static void capabilityUpdateEvent(TickEvent.PlayerTickEvent event)
	{
		try
		{
			if(event.player.deathTime <= 0 && !PlayerInfoCapability.get(event.player).isInitialized())
			{
				if(event.player.world.isRemote)
					SplatcraftPacketHandler.sendToServer(new RequestPlayerInfoPacket(event.player));
				PlayerInfoCapability.get(event.player).setInitialized(true);
			}
		} catch(NullPointerException e) {}
	}
	
	@SubscribeEvent
	public static void gameruleUpdateEvent(TickEvent.WorldTickEvent event)
	{
		World world = event.world;
		if(world.isRemote)
			return;
		for(Map.Entry<Integer, Boolean> rule : SplatcraftGameRules.booleanRules.entrySet())
		{
			boolean worldValue = world.getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(rule.getKey()));
			if(rule.getValue() != worldValue)
			{
				SplatcraftGameRules.booleanRules.put(rule.getKey(), worldValue);
				SplatcraftPacketHandler.sendToAll(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.getRuleFromIndex(rule.getKey()), rule.getValue()));
			}
		}
	}
}
