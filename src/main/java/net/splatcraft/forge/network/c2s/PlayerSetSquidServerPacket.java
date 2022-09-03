package net.splatcraft.forge.network.c2s;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerSetSquidClientPacket;
import net.splatcraft.forge.registries.SplatcraftSounds;

import java.util.UUID;

public class PlayerSetSquidServerPacket extends PlayToServerPacket {
    UUID target;
    private final boolean squid;

    public PlayerSetSquidServerPacket(UUID player, boolean squid) {
        this.squid = squid;
        this.target = player;
    }

    public static PlayerSetSquidServerPacket decode(PacketBuffer buffer)
    {
        return new PlayerSetSquidServerPacket(buffer.readUUID(), buffer.readBoolean());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUUID(target);
        buffer.writeBoolean(squid);
    }

    @Override
    public void execute(PlayerEntity player) {
        World level = player.level;
        IPlayerInfo target = PlayerInfoCapability.get(level.getPlayerByUUID(this.target));

        target.setIsSquid(squid);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), squid ? SplatcraftSounds.squidTransform : SplatcraftSounds.squidRevert, SoundCategory.PLAYERS, 0.75F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);

        SplatcraftPacketHandler.sendToDim(new PlayerSetSquidClientPacket(this.target, squid), player.level);
    }

}
