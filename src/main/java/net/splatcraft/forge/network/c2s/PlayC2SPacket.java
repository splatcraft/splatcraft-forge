package net.splatcraft.forge.network.c2s;

import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.splatcraft.forge.network.SplatcraftPacket;

public abstract class PlayC2SPacket extends SplatcraftPacket
{
    @Override
    public void consume(Supplier<NetworkEvent.Context> ctx)
    {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
        {
            ctx.get().enqueueWork(() -> this.execute(ctx.get().getSender()));
        }
        ctx.get().setPacketHandled(true);
    }

    public abstract void execute(Player player);
}
