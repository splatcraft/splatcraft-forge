package net.splatcraft.forge.client.handlers;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.mixin.MinecraftClientAccessor;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.PlayerSetSquidServerPacket;
import net.splatcraft.forge.network.c2s.SwapSlotWithOffhandPacket;
import net.splatcraft.forge.util.CommonUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SplatcraftKeyHandler {
    // TODO for Octo: make a proper input queue
    public static final HashMap<KeyMapping, Integer> pressState = new HashMap<>();

    public static KeyMapping squidKey;
    public static KeyMapping subWeaponHotkey;

    public static boolean canUseHotkeys = true;

    private static int slot = -1;

    public static void registerKeys() {
        squidKey = new KeyMapping("key.squidForm", GLFW.GLFW_KEY_Z, "key.categories.splatcraft");
        pressState.put(squidKey, 0);
        ClientRegistry.registerKeyBinding(squidKey);

        subWeaponHotkey = new KeyMapping("key.subWeaponHotkey", -1, "key.categories.splatcraft");
        pressState.put(subWeaponHotkey, -2);
        ClientRegistry.registerKeyBinding(subWeaponHotkey);


    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !PlayerInfoCapability.hasCapability(player) || !canUseHotkeys)
            return;

        if (subWeaponHotkey.isDown())
            pressState.put(subWeaponHotkey, Math.max(0,Math.min(pressState.get(subWeaponHotkey) + 1, 2)));
        else pressState.put(subWeaponHotkey, Math.min(0,Math.max(pressState.get(subWeaponHotkey)-1, -2)));

        if(pressState.get(subWeaponHotkey) == 1)
        {
            ItemStack sub = CommonUtils.getItemInInventory(player, itemStack -> itemStack.getItem() instanceof SubWeaponItem);
            PlayerInfo cap = PlayerInfoCapability.get(player);

            if (sub.isEmpty() || (cap.isSquid() && player.level.getBlockCollisions(player,
                    new AABB(-0.3 + player.getX(), player.getY(), -0.3 + player.getZ(), 0.3 + player.getX(), 0.6 + player.getY(), 0.3 + player.getZ())).iterator().hasNext()))
                player.displayClientMessage(new TranslatableComponent("status.cant_use"), true);
            else {
                if (cap.isSquid()) {
                    cap.setIsSquid(false);
                    SplatcraftPacketHandler.sendToServer(new PlayerSetSquidServerPacket(player.getUUID(), false));
                }

                if (!player.getItemInHand(InteractionHand.OFF_HAND).equals(sub))
                {
                    slot = player.getInventory().findSlotMatchingItem(sub);
                    SplatcraftPacketHandler.sendToServer(new SwapSlotWithOffhandPacket(slot, false));

                    ItemStack stack = player.getOffhandItem();
                    player.setItemInHand(InteractionHand.OFF_HAND, player.getInventory().getItem(slot));
                    player.getInventory().setItem(slot, stack);
                    player.stopUsingItem();
                }
                else slot = -1;

                startUsingItemInHand(InteractionHand.OFF_HAND);
            }
        }
        else if(pressState.get(subWeaponHotkey) == -1)
        {
            if(player.getUsedItemHand() == InteractionHand.OFF_HAND)
                mc.gameMode.releaseUsingItem(player);

            if (slot != -1)
            {
                ItemStack stack = player.getOffhandItem();
                player.setItemInHand(InteractionHand.OFF_HAND, player.getInventory().getItem(slot));
                player.getInventory().setItem(slot, stack);
                player.stopUsingItem();

                SplatcraftPacketHandler.sendToServer(new SwapSlotWithOffhandPacket(slot, false));
            }

        }


        if (player.getVehicle() == null && !PlayerCooldown.hasPlayerCooldown(player) &&
                !player.level.getBlockCollisions(player,
                        new AABB(-0.3 + player.getX(), player.getY(), -0.3 + player.getZ(), 0.3 + player.getX(), 0.6 + player.getY(), 0.3 + player.getZ())).iterator().hasNext())
        {
            if (KeyMode.HOLD.equals(SplatcraftConfig.Client.squidKeyMode.get()))
            {
                boolean isPlayerSquid = PlayerInfoCapability.isSquid(player);
                if (isPlayerSquid && !squidKey.isDown() || !isPlayerSquid && squidKey.isDown())
                    pressState.put(squidKey, Math.min(pressState.get(squidKey) + 1, 1));
                else pressState.put(squidKey, 0);
            } else {
                if (squidKey.isDown())
                    pressState.put(squidKey, Math.min(pressState.get(squidKey) + 1, 2));
                else pressState.put(squidKey, 0);
            }


            if (pressState.get(squidKey) == 1)
                onSquidKeyPress();
        } else pressState.put(squidKey, 0);
    }

    public static void onSquidKeyPress() {
        Player player = Minecraft.getInstance().player;
        if (player != null && !player.isSpectator() && PlayerInfoCapability.hasCapability(player) && Minecraft.getInstance().screen == null) {
            PlayerInfo capability = PlayerInfoCapability.get(player);
            boolean newSquid = !capability.isSquid();
            capability.setIsSquid(newSquid);
            SplatcraftPacketHandler.sendToServer(new PlayerSetSquidServerPacket(player.getUUID(), newSquid));
        }
    }

    public enum KeyMode
    {
        HOLD,
        TOGGLE
    }

    public static void startUsingItemInHand(InteractionHand hand)
    {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.gameMode.isDestroying())
        {
            ((MinecraftClientAccessor)mc).setRightClickDelay(4);
            /*
            mc.rightClickDelay = 4;
            if (!mc.player.isHandsBusy()) {
                if (mc.hitResult == null) {
                    LOGGER.warn("Null returned as 'hitResult', mc shouldn't happen!");
                }
                */

            //for(InteractionHand hand : InteractionHand.values())
            {
                net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, mc.options.keyUse, hand);
                if (inputEvent.isCanceled()) {
                    if (inputEvent.shouldSwingHand()) mc.player.swing(hand);
                    return;
                }
                ItemStack itemstack = mc.player.getItemInHand(hand);
                if (mc.hitResult != null) {
                    switch(mc.hitResult.getType()) {
                        case ENTITY:
                            EntityHitResult entityraytraceresult = (EntityHitResult)mc.hitResult;
                            Entity entity = entityraytraceresult.getEntity();
                            InteractionResult actionresulttype = mc.gameMode.interactAt(mc.player, entity, entityraytraceresult, hand);
                            if (!actionresulttype.consumesAction()) {
                                actionresulttype = mc.gameMode.interact(mc.player, entity, hand);
                            }

                            if (actionresulttype.consumesAction()) {
                                if (actionresulttype.shouldSwing()) {
                                    if (inputEvent.shouldSwingHand())
                                        mc.player.swing(hand);
                                }

                                return;
                            }
                            break;
                        case BLOCK:
                            BlockHitResult blockraytraceresult = (BlockHitResult)mc.hitResult;
                            int i = itemstack.getCount();
                            InteractionResult actionresulttype1 = mc.gameMode.useItemOn(mc.player, mc.level, hand, blockraytraceresult);
                            if (actionresulttype1.consumesAction()) {
                                if (actionresulttype1.shouldSwing()) {
                                    if (inputEvent.shouldSwingHand())
                                        mc.player.swing(hand);
                                    if (!itemstack.isEmpty() && (itemstack.getCount() != i || mc.gameMode.hasInfiniteItems())) {
                                        mc.gameRenderer.itemInHandRenderer.itemUsed(hand);
                                    }
                                }

                                return;
                            }

                            if (actionresulttype1 == InteractionResult.FAIL) {
                                return;
                            }
                    }
                }

                if (itemstack.isEmpty() && (mc.hitResult == null || mc.hitResult.getType() == HitResult.Type.MISS))
                    net.minecraftforge.common.ForgeHooks.onEmptyClick(mc.player, hand);

                if (!itemstack.isEmpty()) {
                    InteractionResult actionresulttype2 = mc.gameMode.useItem(mc.player, mc.level, hand);
                    if (actionresulttype2.consumesAction()) {
                        if (actionresulttype2.shouldSwing()) {
                            mc.player.swing(hand);
                        }

                        mc.gameRenderer.itemInHandRenderer.itemUsed(hand);
                    }
                }
            }

        }
    }
}
