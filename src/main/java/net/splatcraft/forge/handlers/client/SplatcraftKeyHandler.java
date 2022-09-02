package net.splatcraft.forge.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.PlayerSetSquidServerPacket;
import net.splatcraft.forge.network.c2s.SwapSlotWithOffhandPacket;
import net.splatcraft.forge.util.CommonUtils;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SplatcraftKeyHandler
{
    private static final HashMap<KeyBinding, Integer> pressState = new HashMap<>();

    public static KeyBinding squidKey;
    public static KeyBinding subWeaponHotkey;

    private static int slot = -1;

    public static void registerKeys()
    {
        squidKey = new KeyBinding("key.squidForm", GLFW.GLFW_KEY_Z, "key.categories.splatcraft");
        pressState.put(squidKey, 0);
        ClientRegistry.registerKeyBinding(squidKey);

        subWeaponHotkey = new KeyBinding("key.subWeaponHotkey", -1, "key.categories.splatcraft");
        pressState.put(subWeaponHotkey, -2);
        ClientRegistry.registerKeyBinding(subWeaponHotkey);


    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || !PlayerInfoCapability.hasCapability(player))
            return;

        if (subWeaponHotkey.isDown())
            pressState.put(subWeaponHotkey, Math.max(0,Math.min(pressState.get(subWeaponHotkey) + 1, 2)));
        else pressState.put(subWeaponHotkey, Math.min(0,Math.max(pressState.get(subWeaponHotkey)-1, -2)));

        if(pressState.get(subWeaponHotkey) == 1)
        {
            ItemStack sub = CommonUtils.getItemInInventory(player, itemStack -> itemStack.getItem() instanceof SubWeaponItem);

            if(!sub.isEmpty() && !player.getItemInHand(Hand.OFF_HAND).equals(sub))
            {
                slot = player.inventory.findSlotMatchingItem(sub);
                SplatcraftPacketHandler.sendToServer(new SwapSlotWithOffhandPacket(slot, false));
            }
            else slot = -1;

            startUseItem(Hand.OFF_HAND);
            mc.options.keyUse.setDown(true);
        }
        else if(pressState.get(subWeaponHotkey) == -1)
        {
            if(slot != -1)
                SplatcraftPacketHandler.sendToServer(new SwapSlotWithOffhandPacket(slot, false));

            if(player.getUsedItemHand() == Hand.OFF_HAND)
            {
                mc.gameMode.releaseUsingItem(player);
                mc.options.keyUse.setDown(false);
            }
        }


        if(player.getVehicle() == null && player.level.getBlockCollisions(player,
                new AxisAlignedBB(-0.3 + player.getX(), player.getY(), -0.3 + player.getZ(), 0.3 + player.getX(), 0.6 + player.getY(), 0.3 + player.getZ()))
                .collect(Collectors.toList()).isEmpty())
        {
            if (KeyMode.HOLD.equals(SplatcraftConfig.Client.squidKeyMode.get()))
            {
                boolean isPlayerSquid = PlayerInfoCapability.isSquid(player);

                if (isPlayerSquid && !squidKey.isDown() || !isPlayerSquid && squidKey.isDown())
                    pressState.put(squidKey, Math.min(pressState.get(squidKey) + 1, 1));
                else pressState.put(squidKey, 0);
            } else
            {
                if (squidKey.isDown())
                    pressState.put(squidKey, Math.min(pressState.get(squidKey) + 1, 2));
                else pressState.put(squidKey, 0);
            }

            if (pressState.get(squidKey) == 1)
                onSquidKeyPress();
        } else pressState.put(squidKey, 0);
    }

    public static void onSquidKeyPress()
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && PlayerInfoCapability.hasCapability(player) && Minecraft.getInstance().screen == null) {
            IPlayerInfo capability = PlayerInfoCapability.get(player);
            SplatcraftPacketHandler.sendToServer(new PlayerSetSquidServerPacket(player));
            capability.setIsSquid(!capability.isSquid());
        }
    }

    public enum KeyMode
    {
        HOLD,
        TOGGLE
    }

    private static void startUseItem(Hand hand)
    {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.gameMode.isDestroying())
        {
            ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, 4, "field_71467_ac");

            mc.player.startUsingItem(hand);

            if (!mc.player.isHandsBusy()) {
                if (mc.hitResult == null) {
                    //LOGGER.warn("Null returned as 'hitResult', mc shouldn't happen!");
                }

                /*for(Hand hand : Hand.values())*/ {
                    net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, mc.options.keyUse, hand);
                    if (inputEvent.isCanceled()) {
                        if (inputEvent.shouldSwingHand()) mc.player.swing(hand);
                        return;
                    }
                    ItemStack itemstack = mc.player.getItemInHand(hand);
                    if (mc.hitResult != null) {
                        switch(mc.hitResult.getType()) {
                            case ENTITY:
                                EntityRayTraceResult entityraytraceresult = (EntityRayTraceResult)mc.hitResult;
                                Entity entity = entityraytraceresult.getEntity();
                                ActionResultType actionresulttype = mc.gameMode.interactAt(mc.player, entity, entityraytraceresult, hand);
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
                                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)mc.hitResult;
                                int i = itemstack.getCount();
                                ActionResultType actionresulttype1 = mc.gameMode.useItemOn(mc.player, mc.level, hand, blockraytraceresult);
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

                                if (actionresulttype1 == ActionResultType.FAIL) {
                                    return;
                                }
                        }
                    }

                    if (itemstack.isEmpty() && (mc.hitResult == null || mc.hitResult.getType() == RayTraceResult.Type.MISS))
                        net.minecraftforge.common.ForgeHooks.onEmptyClick(mc.player, hand);

                    if (!itemstack.isEmpty()) {
                        ActionResultType actionresulttype2 = mc.gameMode.useItem(mc.player, mc.level, hand);
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
}
