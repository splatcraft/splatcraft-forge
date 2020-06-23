package com.cibernet.splatcraft.network;

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
        instance.registerMessage(PacketWeaponLeftClick.Handler.class, PacketWeaponLeftClick.class, nextID(), Side.SERVER);
        instance.registerMessage(PacketSetVatOutput.Handler.class, PacketSetVatOutput.class, nextID(), Side.SERVER);
        instance.registerMessage(PacketGetPlayerData.Handler.class, PacketGetPlayerData.class, nextID(), Side.SERVER);
        instance.registerMessage(PacketReturnChargeRelease.Handler.class, PacketReturnChargeRelease.class, nextID(), Side.SERVER);
        instance.registerMessage(PacketCraftWeapon.Handler.class, PacketCraftWeapon.class, nextID(), Side.SERVER);
        instance.registerMessage(PacketDodgeRoll.Handler.class, PacketDodgeRoll.class, nextID(), Side.SERVER);

        //Client Packets
        instance.registerMessage(PacketPlayerReturnTransformed.Handler.class, PacketPlayerReturnTransformed.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketPlayerReturnColor.Handler.class, PacketPlayerReturnColor.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketReturnPlayerData.Handler.class, PacketReturnPlayerData.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketChargeRelease.Handler.class, PacketChargeRelease.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketUpdateGamerule.Handler.class, PacketUpdateGamerule.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketSendColorScores.Handler.class, PacketSendColorScores.class, nextID(), Side.CLIENT);
        instance.registerMessage(PacketInkLandParticles.Handler.class, PacketInkLandParticles.class, nextID(), Side.CLIENT);
    }
}
