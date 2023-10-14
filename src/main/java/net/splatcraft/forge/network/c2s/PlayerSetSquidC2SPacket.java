package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidS2CPacket;
import net.splatcraft.forge.registries.SplatcraftSounds;

public class PlayerSetSquidC2SPacket extends PlayC2SPacket {
    private final boolean squid;

    public PlayerSetSquidC2SPacket(boolean squid) {
        this.squid = squid;
    }

    public static PlayerSetSquidC2SPacket decode(FriendlyByteBuf buffer)
    {
        return new PlayerSetSquidC2SPacket(buffer.readBoolean());
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(squid);
    }

    @Override
    public void execute(Player player) {
        PlayerInfo target = PlayerInfoCapability.get(player);
        if (squid == target.isSquid()) {
            throw new IllegalStateException(String.format("Squid state did not change for %s (%s)", player.getGameProfile(), squid));
        }

        target.setIsSquid(squid);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), squid ? SplatcraftSounds.squidTransform : SplatcraftSounds.squidRevert, SoundSource.PLAYERS, 0.75F, ((player.level.getRandom().nextFloat() - player.level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);

        SplatcraftPacketHandler.sendToTrackers(new PlayerSetSquidS2CPacket(player.getUUID(), squid), player);
    }

}
