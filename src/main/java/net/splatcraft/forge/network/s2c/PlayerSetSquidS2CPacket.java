package net.splatcraft.forge.network.s2c;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;

public class PlayerSetSquidS2CPacket extends PlayS2CPacket
{
    UUID target;
    private final boolean squid;

    public PlayerSetSquidS2CPacket(UUID player, boolean squid) {
        this.squid = squid;
        this.target = player;
    }

    public static PlayerSetSquidS2CPacket decode(FriendlyByteBuf buffer)
    {
        return new PlayerSetSquidS2CPacket(buffer.readUUID(), buffer.readBoolean());
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(target);
        buffer.writeBoolean(squid);
    }

    @Override
    public void execute() {
        Player player = Minecraft.getInstance().level.getPlayerByUUID(this.target);
        if (player == null) {
            return;
        }
        PlayerInfo target = PlayerInfoCapability.get(player);
        target.setIsSquid(squid);
    }
}
