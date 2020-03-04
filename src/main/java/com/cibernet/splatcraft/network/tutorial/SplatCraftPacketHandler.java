package com.cibernet.splatcraft.network.tutorial;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class SplatCraftPacketHandler
{
    public static SimpleNetworkWrapper instance;
    private static int ID = 0;

    private static int nextID() {return ID++;}

    public static void registerMessages(String channelName)
    {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

        //Server Packets
        instance.registerMessage(PacketPlayerSetTransformed.Handler.class, PacketPlayerSetTransformed.class, nextID(), Side.SERVER);
        instance.registerMessage(PacketPlayerSetColor.Handler.class, PacketPlayerSetColor.class, nextID(), Side.SERVER);

        //Client Packets
        instance.registerMessage(PacketPlayerReturnTransformed.Handler.class, PacketPlayerReturnTransformed.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketPlayerReturnColor.Handler.class, PacketPlayerReturnColor.class, nextID(), Side.CLIENT);
    }
}
