package net.splatcraft.forge.network.c2s;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.registries.SplatcraftSounds;

public class PlayerSetSquidServerPacket extends PlayToServerPacket {
    private final boolean squid;

    public PlayerSetSquidServerPacket(boolean squid) {
        this.squid = squid;
    }

    public static PlayerSetSquidServerPacket decode(PacketBuffer buffer)
    {
        return new PlayerSetSquidServerPacket(buffer.readBoolean());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBoolean(squid);
    }

    @Override
    public void execute(PlayerEntity player) {
        IPlayerInfo target = PlayerInfoCapability.get(player);

        target.setIsSquid(squid);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), squid ? SplatcraftSounds.squidTransform : SplatcraftSounds.squidRevert, SoundCategory.PLAYERS, 0.75F, ((player.level.getRandom().nextFloat() - player.level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);

        SplatcraftPacketHandler.sendToTrackers(new PlayerSetSquidClientPacket(player.getUUID(), squid), player);
    }
}
