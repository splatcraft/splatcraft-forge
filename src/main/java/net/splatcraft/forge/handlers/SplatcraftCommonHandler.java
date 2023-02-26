package net.splatcraft.forge.handlers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.NonNullList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.items.InkWaxerItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestPlayerInfoPacket;
import net.splatcraft.forge.network.s2c.UpdateBooleanGamerulesPacket;
import net.splatcraft.forge.network.s2c.UpdateClientColorsPacket;
import net.splatcraft.forge.network.s2c.UpdateColorScoresPacket;
import net.splatcraft.forge.network.s2c.UpdateIntGamerulesPacket;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.CommonUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;

import java.util.HashMap;
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
            entity.setDeltaMovement(entity.getDeltaMovement().x, Math.min(entity.getDeltaMovement().y, 0.1f), entity.getDeltaMovement().z);
        }
    }

    @SubscribeEvent
    public static void onLivingDestroyBlock(LivingDestroyBlockEvent event)
    {
        if(!(event.getEntity().level.getBlockEntity(event.getPos()) instanceof InkedBlockTileEntity))
            return;

        InkedBlockTileEntity te = (InkedBlockTileEntity) event.getEntity().level.getBlockEntity(event.getPos());
        BlockState savedState = te.getSavedState();
        if(event.getState().getBlock() instanceof IColoredBlock && (event.isCanceled() ||
                (event.getEntityLiving() instanceof EnderDragonEntity && savedState.is(BlockTags.DRAGON_IMMUNE)) ||
                (event.getEntityLiving() instanceof WitherEntity && savedState.is(BlockTags.WITHER_IMMUNE))))
        {
            ((IColoredBlock) event.getState().getBlock()).remoteInkClear(event.getEntityLiving().level, event.getPos());
            event.setCanceled(true);
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
                if (!stack.isEmpty() && !putStackInSlot(player.inventory, stack, i) && !player.inventory.add(stack))
                {
                    player.drop(stack, true, true);
                }
            }

            PlayerInfoCapability.get(player).setMatchInventory(NonNullList.create());
        }
        PlayerCooldown.setPlayerCooldown(player, null);
    }

    private static boolean putStackInSlot(PlayerInventory inventory, ItemStack stack, int i)
    {
        ItemStack invStack = inventory.getItem(i);

        if (invStack.isEmpty())
        {
            inventory.setItem(i, stack);
            return true;
        }
        if (invStack.sameItem(stack))
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
        ItemStack stack = entity.getItemBySlot(EquipmentSlotType.CHEST);

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
                    player.inventory.add(stack);
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
        if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && SplatcraftGameRules.getBooleanRuleValue(player.level, SplatcraftGameRules.KEEP_MATCH_ITEMS))
        {
            IPlayerInfo playerCapability;
            try
            {
                playerCapability = PlayerInfoCapability.get(player);
            } catch (NullPointerException e)
            {
                return;
            }

            NonNullList<ItemStack> matchInv = NonNullList.withSize(player.inventory.getContainerSize(), ItemStack.EMPTY);

            for (int i = 0; i < matchInv.size(); i++)
            {
                ItemStack stack = player.inventory.getItem(i);
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
        SplatcraftPacketHandler.sendToPlayer(new UpdateIntGamerulesPacket(SplatcraftGameRules.intRules), (ServerPlayerEntity) player);

        int[] colors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
        int i = 0;
        for (int c : ScoreboardHandler.getCriteriaKeySet())
            colors[i++] = c;

        TreeMap<String, Integer> playerColors = new TreeMap<>();

        for (PlayerEntity p : event.getPlayer().level.players())
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

    @Deprecated
    public static final HashMap<PlayerEntity, Integer> LOCAL_COLOR = new HashMap<>();

    @SubscribeEvent
    public static void capabilityUpdateEvent(TickEvent.PlayerTickEvent event)
    {
        IPlayerInfo info = PlayerInfoCapability.get(event.player);
        if(PlayerInfoCapability.hasCapability(event.player))
        {
            if(!event.player.level.isClientSide)
            {
                ItemStack inkBand = CommonUtils.getItemInInventory(event.player, itemStack -> itemStack.getItem().is(SplatcraftTags.Items.INK_BANDS) && InkBlockUtils.hasInkType(itemStack));

                if(!info.getInkBand().equals(inkBand, false))
                {
                    info.setInkBand(inkBand);
                    SplatcraftPacketHandler.sendToDim(new UpdatePlayerInfoPacket(event.player), event.player.level);
                }
            }

                if (event.player.deathTime <= 0 && !info.isInitialized())
                {
                    info.setInitialized(true);
                    info.setPlayer(event.player);
                    if(LOCAL_COLOR.containsKey(event.player))
                        info.setColor(LOCAL_COLOR.get(event.player));

                    if (event.player.level.isClientSide)
                        SplatcraftPacketHandler.sendToServer(new RequestPlayerInfoPacket(event.player));
                }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        World level = event.world;
        if (level.isClientSide) {
            return;
        }
        for (Map.Entry<Integer, Boolean> rule : SplatcraftGameRules.booleanRules.entrySet()) {
            boolean levelValue = level.getGameRules().getBoolean(SplatcraftGameRules.getRuleFromIndex(rule.getKey()));
            if (rule.getValue() != levelValue) {
                SplatcraftGameRules.booleanRules.put(rule.getKey(), levelValue);
                SplatcraftPacketHandler.sendToAll(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.getRuleFromIndex(rule.getKey()), rule.getValue()));
            }
        }
        for (Map.Entry<Integer, Integer> rule : SplatcraftGameRules.intRules.entrySet()) {
            int levelValue = level.getGameRules().getInt(SplatcraftGameRules.getRuleFromIndex(rule.getKey()));
            if (rule.getValue() != levelValue) {
                SplatcraftGameRules.intRules.put(rule.getKey(), levelValue);
                SplatcraftPacketHandler.sendToAll(new UpdateIntGamerulesPacket(SplatcraftGameRules.getRuleFromIndex(rule.getKey()), rule.getValue()));
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

    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getItemStack().getItem() instanceof InkWaxerItem)
            ((InkWaxerItem)event.getItemStack().getItem()).onBlockStartBreak(event.getItemStack(), event.getPos(), event.getWorld());
    }

}
