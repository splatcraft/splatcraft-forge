package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.util.PlayerCharge;

public class UpdateChargeStatePacket extends PlayToServerPacket {
    private final boolean hasCharge;

    public UpdateChargeStatePacket(boolean hasCharge) {
        this.hasCharge = hasCharge;
    }

    public static UpdateChargeStatePacket decode(FriendlyByteBuf buffer) {
        return new UpdateChargeStatePacket(buffer.readBoolean());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(hasCharge);
    }

    @Override
    public void execute(Player player) {
        PlayerCharge.updateServerMap(player, hasCharge);
    }
}
