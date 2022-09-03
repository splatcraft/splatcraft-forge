package net.splatcraft.forge.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class SplatcraftPacket
{
    public abstract void encode(PacketBuffer buffer);

    public abstract void consume(Supplier<NetworkEvent.Context> ctx);
    /*
    {
        if(getDirection() == EnumDirection.PLAY_TO_CLIENT)
        {
            
        }
        if(getDirection() == EnumDirection.PLAY_TO_SERVER)
            if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
                ctx.get().enqueueWork(() -> this.execute(ctx.get().getSender()));
        
        ctx.get().setPacketHandled(true);
    }
    */


}
