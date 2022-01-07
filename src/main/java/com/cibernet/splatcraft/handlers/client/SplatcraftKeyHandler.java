package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.PlayerSetSquidServerPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.stream.Collectors;

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

        if(player.getVehicle() == null && player.level.getBlockCollisions(player,
                new AxisAlignedBB(-0.3 + player.getX(), player.getY(), -0.3 + player.getZ(), 0.3 + player.getX(), 0.6 + player.getY(), 0.3 + player.getZ()))
                .collect(Collectors.toList()).isEmpty())
        {
            if (KeyMode.HOLD.equals(SplatcraftConfig.Client.squidKeyMode.get()))
            {
                boolean isPlayerSquid = PlayerInfoCapability.isSquid(player);

                if (isPlayerSquid && !squidKey.isDown() || !isPlayerSquid && squidKey.isDefault())
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
