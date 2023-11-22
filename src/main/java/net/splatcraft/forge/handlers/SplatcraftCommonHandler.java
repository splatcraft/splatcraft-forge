package net.splatcraft.forge.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
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
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.client.layer.PlayerInkColoredSkinLayer;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.items.InkWaxerItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.RequestPlayerInfoPacket;
import net.splatcraft.forge.network.c2s.SendPlayerOverlayPacket;
import net.splatcraft.forge.network.s2c.ReceivePlayerOverlayPacket;
import net.splatcraft.forge.network.s2c.UpdateBooleanGamerulesPacket;
import net.splatcraft.forge.network.s2c.UpdateClientColorsPacket;
import net.splatcraft.forge.network.s2c.UpdateColorScoresPacket;
import net.splatcraft.forge.network.s2c.UpdateIntGamerulesPacket;
import net.splatcraft.forge.network.s2c.UpdatePlayerInfoPacket;
import net.splatcraft.forge.network.s2c.UpdateStageListPacket;
import net.splatcraft.forge.network.s2c.UpdateWeaponSettingsPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.CommonUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;

@Mod.EventBusSubscriber
public class SplatcraftCommonHandler {
    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();

        if (!(entity instanceof Player)) {
            return;
        }

        if (InkBlockUtils.onEnemyInk(event.getEntityLiving())) {
            entity.setDeltaMovement(entity.getDeltaMovement().x, Math.min(entity.getDeltaMovement().y, 0.1f), entity.getDeltaMovement().z);
        }
    }


    @SubscribeEvent
    public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
        if (!(event.getEntity().level.getBlockEntity(event.getPos()) instanceof InkedBlockTileEntity te)) {
            return;
        }

        BlockState savedState = te.getSavedState();
        if (event.getState().getBlock() instanceof IColoredBlock && (event.isCanceled() ||
                (event.getEntityLiving() instanceof EnderDragon && savedState.is(BlockTags.DRAGON_IMMUNE)) ||
                (event.getEntityLiving() instanceof WitherBoss && savedState.is(BlockTags.WITHER_IMMUNE)))) {
            ((IColoredBlock) event.getState().getBlock()).remoteInkClear(event.getEntityLiving().level, event.getPos());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        Player player = event.getPlayer();
        event.getOriginal().reviveCaps(); // Mod devs should not have to do this
        PlayerInfoCapability.get(player).readNBT(PlayerInfoCapability.get(event.getOriginal()).writeNBT(new CompoundTag()));
        event.getOriginal().invalidateCaps();

        event.getOriginal().invalidateCaps();

        NonNullList<ItemStack> matchInv = PlayerInfoCapability.get(player).getMatchInventory();

        if (!matchInv.isEmpty()) {
            for (int i = 0; i < matchInv.size(); i++) {
                ItemStack stack = matchInv.get(i);
                if (!stack.isEmpty() && !putStackInSlot(player.getInventory(), stack, i) && !player.getInventory().add(stack)) {
                    player.drop(stack, true, true);
                }
            }

            PlayerInfoCapability.get(player).setMatchInventory(NonNullList.create());
        }
        PlayerCooldown.setPlayerCooldown(player, null);
    }

    private static boolean putStackInSlot(Inventory inventory, ItemStack stack, int i) {
        ItemStack invStack = inventory.getItem(i);

        if (invStack.isEmpty()) {
            inventory.setItem(i, stack);
            return true;
        }
        if (invStack.sameItem(stack)) {
            int invCount = invStack.getCount();
            int count = Math.min(invStack.getMaxStackSize(), stack.getCount() + invStack.getCount());
            invStack.setCount(count);
            stack.shrink(count - invCount);

            return stack.isEmpty();
        }
        return false;
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);

        if (stack.getItem() instanceof InkTankItem) {
            ((InkTankItem) stack.getItem()).refill(stack);
        }
    }

    @SubscribeEvent
    public static void onLivingDeathDrops(LivingDropsEvent event) {
        //handle inked wool drops
        if (event.getEntityLiving() instanceof Sheep && InkOverlayCapability.hasCapability(event.getEntityLiving())) {
            InkOverlayInfo info = InkOverlayCapability.get(event.getEntityLiving());


            if (info.getWoolColor() > -1) {
                for (ItemEntity itemEntity : event.getDrops())
                {
                    ItemStack stack = itemEntity.getItem();
                    if (stack.is(ItemTags.WOOL)) {
                        itemEntity.setItem(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool.get(), stack.getCount()), info.getWoolColor()), true));
                    }
                }
            }
        }

        //Handle keepMatchItems
        if (event.getEntityLiving() instanceof Player player) {
            NonNullList<ItemStack> matchInv = PlayerInfoCapability.get(player).getMatchInventory();

            event.getDrops().removeIf(drop -> matchInv.contains(drop.getItem()));

            for (int i = 0; i < matchInv.size(); i++) {
                ItemStack stack = matchInv.get(i);
                if (!stack.isEmpty() && !putStackInSlot(player.getInventory(), stack, i)) {
                    player.getInventory().add(stack);
                }
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerAboutToDie(LivingDamageEvent event) {
        if (!(event.getEntityLiving() instanceof Player player) || event.getEntityLiving().getHealth() - event.getAmount() > 0) {
            return;
        }

        if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.KEEP_MATCH_ITEMS)) {
            PlayerInfo playerCapability;
            try {
                playerCapability = PlayerInfoCapability.get(player);
            } catch (NullPointerException e) {
                return;
            }

            NonNullList<ItemStack> matchInv = NonNullList.withSize(player.getInventory().getContainerSize(), ItemStack.EMPTY);

            for (int i = 0; i < matchInv.size(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(SplatcraftTags.Items.MATCH_ITEMS)) {
                    matchInv.set(i, stack);
                }
            }

            playerCapability.setMatchInventory(matchInv);
        }
    }

    public static final HashMap<UUID, byte[]> COLOR_SKIN_OVERLAY_SERVER_CACHE = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        SplatcraftPacketHandler.sendToPlayer(new UpdateBooleanGamerulesPacket(SplatcraftGameRules.booleanRules), player);
        SplatcraftPacketHandler.sendToPlayer(new UpdateIntGamerulesPacket(SplatcraftGameRules.intRules), player);
        SplatcraftPacketHandler.sendToPlayer(new UpdateWeaponSettingsPacket(), player);

        int[] criteriaColors = new int[ScoreboardHandler.getCriteriaKeySet().size()];
        int criteriaColorIndex = 0;
        for (int criteriaColor : ScoreboardHandler.getCriteriaKeySet()) {
            criteriaColors[criteriaColorIndex++] = criteriaColor;
        }

        TreeMap<String, Integer> playerColors = new TreeMap<>();

        for (Player p : event.getPlayer().level.players()) {
            if (PlayerInfoCapability.hasCapability(p)) {
                playerColors.put(p.getDisplayName().getString(), PlayerInfoCapability.get(p).getColor());
            }
        }

        SplatcraftPacketHandler.sendToAll(new UpdateClientColorsPacket(event.getPlayer().getDisplayName().getString(), PlayerInfoCapability.get(event.getPlayer()).getColor()));
        SplatcraftPacketHandler.sendToPlayer(new UpdateClientColorsPacket(playerColors), player);
        SplatcraftPacketHandler.sendToPlayer(new UpdateColorScoresPacket(true, true, criteriaColors), player);
        SplatcraftPacketHandler.sendToPlayer(new UpdateStageListPacket(SaveInfoCapability.get(event.getPlayer().level.getServer()).getStages()), player);
        if (!COLOR_SKIN_OVERLAY_SERVER_CACHE.isEmpty()) {
            COLOR_SKIN_OVERLAY_SERVER_CACHE.forEach(((uuid, bytes) -> SplatcraftPacketHandler.sendToPlayer(new ReceivePlayerOverlayPacket(uuid, bytes), player)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientLogIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
        LocalPlayer player = event.getPlayer();
        File file = Paths.get(SplatcraftConfig.Client.inkColoredSkinLayerPath).toFile();
        if (player != null && file.exists()) {
            try {
                SplatcraftPacketHandler.sendToServer(new SendPlayerOverlayPacket(player.getUUID(), file));
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientLogOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        PlayerInkColoredSkinLayer.TEXTURES.values().forEach(Minecraft.getInstance().getTextureManager()::release);
        PlayerInkColoredSkinLayer.TEXTURES.clear();

        if (event.getPlayer() != null) {
            SplatcraftPacketHandler.sendToServer(new SendPlayerOverlayPacket(event.getPlayer().getUUID(), new byte[0]));
        }
    }

    @Deprecated
    public static final HashMap<Player, Integer> LOCAL_COLOR = new HashMap<>();

    @SubscribeEvent
    public static void capabilityUpdateEvent(TickEvent.PlayerTickEvent event) {
        if (PlayerInfoCapability.hasCapability(event.player)) {
            PlayerInfo info = PlayerInfoCapability.get(event.player);
            if (event.player.deathTime <= 0 && !info.isInitialized()) {
                info.setInitialized(true);
                info.setPlayer(event.player);
                if (LOCAL_COLOR.containsKey(event.player)) {
                    info.setColor(LOCAL_COLOR.get(event.player));
                }

                if (event.side.isClient()) {
                    SplatcraftPacketHandler.sendToServer(new RequestPlayerInfoPacket(event.player));
                }
            }

            if (event.side.isServer()) {
                ItemStack inkBand = CommonUtils.getItemInInventory(event.player, itemStack -> itemStack.is(SplatcraftTags.Items.INK_BANDS) && InkBlockUtils.hasInkType(itemStack));

                if (!info.getInkBand().equals(inkBand, false)) {
                    info.setInkBand(inkBand);
                    SplatcraftPacketHandler.sendToTrackersAndSelf(new UpdatePlayerInfoPacket(event.player), event.player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        Level level = event.world;
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
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (InkOverlayCapability.hasCapability(entity)) {
            if (entity.isInWater()) {
                InkOverlayCapability.get(entity).setAmount(0);
            } else {
                InkOverlayCapability.get(entity).addAmount(-0.01f);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getItemStack().getItem() instanceof InkWaxerItem) {
            ((InkWaxerItem) event.getItemStack().getItem()).onBlockStartBreak(event.getItemStack(), event.getPos(), event.getWorld());
        }
    }

}
