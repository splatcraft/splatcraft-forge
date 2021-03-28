package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.InkTankItem;
import com.cibernet.splatcraft.network.*;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.TreeMap;

@Mod.EventBusSubscriber
public class SplatcraftCommonHandler
{

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event)
    {
        LivingEntity entity = event.getEntityLiving();

        if (!(entity instanceof PlayerEntity))
        {
            return;
        }

        if (InkBlockUtils.onEnemyInk(event.getEntityLiving()))
        {
            entity.setMotion(entity.getMotion().x, Math.min(entity.getMotion().y, 0.1f), entity.getMotion().z);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(final PlayerEvent.Clone event)
    {
        PlayerEntity player = event.getPlayer();
        PlayerInfoCapability.get(player).readNBT(PlayerInfoCapability.get(event.getOriginal()).writeNBT(new CompoundNBT()));

        NonNullList<ItemStack> matchInv = PlayerInfoCapability.get(player).getMatchInventory();

        if (!matchInv.isEmpty())
        {
            for (int i = 0; i < matchInv.size(); i++)
            {
                ItemStack stack = matchInv.get(i);
                if (!stack.isEmpty() && !putStackInSlot(player.inventory, stack, i) && !player.inventory.addItemStackToInventory(stack))
                {
                    player.dropItem(stack, true, true);
                }
            }

            PlayerInfoCapability.get(player).setMatchInventory(NonNullList.create());
        }
        PlayerCooldown.setPlayerCooldown(player, null);
    }

    private static boolean putStackInSlot(PlayerInventory inventory, ItemStack stack, int i)
    {
        ItemStack invStack = inventory.getStackInSlot(i);

        if (invStack.isEmpty())
        {
            inventory.setInventorySlotContents(i, stack);
            return true;
        }
        if (invStack.isItemEqual(stack))
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
        ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);

        if (stack.getItem() instanceof InkTankItem)
        {
            ((InkTankItem) stack.getItem()).refill(stack);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeathDrops(LivingDropsEvent event)
    {
        if (event.getEntityLiving() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            NonNullList<ItemStack> matchInv = PlayerInfoCapability.get(player).getMatchInventory();

            event.getDrops().removeIf(drop -> matchInv.contains(drop.getItem()));

            for (int i = 0; i < matchInv.size(); i++)
            {
                ItemStack stack = matchInv.get(i);
                if (!stack.isEmpty() && !putStackInSlot(player.inventory, stack, i))
                {
                    player.inventory.addItemStackToInventory(stack);
                }
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerAboutToDie(LivingDamageEvent event)
    {
        if (!(event.getEntityLiving() instanceof PlayerEntity) || event.getEntityLiving().getHealth() - event.getAmount() > 0)
        {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (!player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && SplatcraftGameRules.getBooleanRuleValue(player.world, SplatcraftGameRules.KEEP_MATCH_ITEMS))
        {
            IPlayerInfo playerCapability;
            try
            {
                playerCapability = PlayerInfoCapability.get(player);
            } catch (NullPointerException e)
            {
                return;
            }

            NonNullList<ItemStack> matchInv = NonNullList.withSize(player.inventory.getSizeInventory(), ItemStack.EMPTY);

            for (int i = 0; i < matchInv.size(); i++)
            {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (SplatcraftTags.Items.MATCH_ITEMS.contains(stack.getItem()))
                {
                    matchInv.set(i, stack);
                }
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
        for (int c : ScoreboardHandler.getCriteriaKeySet())
            colors[i++] = c;

        TreeMap<String, Integer> playerColors = new TreeMap<>();

        for (PlayerEntity p : event.getPlayer().world.getPlayers())
        {
            if (PlayerInfoCapability.hasCapability(p))
                playerColors.put(p.getDisplayName().getString(), PlayerInfoCapability.get(p).getColor());
        }

        SplatcraftPacketHandler.sendToAll(new UpdateClientColorsPacket(event.getPlayer().getDisplayName().getString(), PlayerInfoCapability.get(event.getPlayer()).getColor()));
        SplatcraftPacketHandler.sendToPlayer(new UpdateClientColorsPacket(playerColors), (ServerPlayerEntity) player);
        SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, colors), (ServerPlayerEntity) player);
    }

    @SubscribeEvent
    public static void onDataReload(AddReloadListenerEvent event)
    {

    }

    @SubscribeEvent
    public static void capabilityUpdateEvent(TickEvent.PlayerTickEvent event)
    {
        IPlayerInfo info = PlayerInfoCapability.get(event.player);
        if(PlayerInfoCapability.hasCapability(event.player))
        {
            InkBlockUtils.InkType checkedInkType = InkBlockUtils.checkInkType(event.player);
            if(!event.player.world.isRemote && !checkedInkType.equals(info.getInkType()))
            {
                info.setInkType(checkedInkType);
                SplatcraftPacketHandler.sendToDim(new UpdatePlayerInfoPacket(event.player), event.player.world);
            }

            try
            {
                if (event.player.deathTime <= 0 && !info.isInitialized())
                {
                    PlayerInfoCapability.get(event.player).setInitialized(true);
                    PlayerInfoCapability.get(event.player).setColor(ColorUtils.getRandomStarterColor());
                    if (event.player.world.isRemote)
                        SplatcraftPacketHandler.sendToServer(new RequestPlayerInfoPacket(event.player));
                }
            } catch (NullPointerException ignored) {}
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;
        if (world.isRemote)
        {
            return;
        }
        for (Map.Entry<Integer, Boolean> rule : SplatcraftGameRules.booleanRules.entrySet())
        {
            boolean worldValue = world.getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(rule.getKey()));
            if (rule.getValue() != worldValue)
            {
                SplatcraftGameRules.booleanRules.put(rule.getKey(), worldValue);
                SplatcraftPacketHandler.sendToAll(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.getRuleFromIndex(rule.getKey()), rule.getValue()));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        if (InkOverlayCapability.hasCapability(entity))
        {
            if (entity.isInWater())
            {
                InkOverlayCapability.get(entity).setAmount(0);
            } else
            {
                InkOverlayCapability.get(entity).addAmount(-0.01f);
            }
        }
    }

}
