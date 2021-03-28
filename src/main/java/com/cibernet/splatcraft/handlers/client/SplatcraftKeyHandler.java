package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.PlayerSetSquidServerPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SplatcraftKeyHandler
{
    private static final HashMap<KeyBinding, Integer> pressState = new HashMap<>();

    public static KeyBinding squidKey;

    public static void registerKeys()
    {
        squidKey = new KeyBinding("key.squidForm", GLFW.GLFW_KEY_Z, "key.categories.splatcraft");
        pressState.put(squidKey, 0);
        ClientRegistry.registerKeyBinding(squidKey);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || ! PlayerInfoCapability.hasCapability(player))
            return;

        if(player.getRidingEntity() == null && player.world.getCollisionShapes(player,
                new AxisAlignedBB(-0.3 + player.getPosX(), player.getPosY(), -0.3 + player.getPosZ(), 0.3 + player.getPosX(), 0.6 + player.getPosY(), 0.3 + player.getPosZ()))
                .collect(Collectors.toList()).isEmpty())
        {
            if (KeyMode.HOLD.equals(SplatcraftConfig.Client.squidKeyMode.get()))
            {
                boolean isPlayerSquid = PlayerInfoCapability.isSquid(player);

                if (isPlayerSquid && !squidKey.isKeyDown() || !isPlayerSquid && squidKey.isKeyDown())
                    pressState.put(squidKey, Math.min(pressState.get(squidKey) + 1, 1));
                else pressState.put(squidKey, 0);
            } else
            {
                if (squidKey.isKeyDown())
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
        if (player != null && PlayerInfoCapability.hasCapability(player))
        {
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
}
