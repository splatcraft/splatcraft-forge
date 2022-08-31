package net.splatcraft.forge.network;

import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.network.base.PlayToServerPacket;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.UUID;

public class PlayerSetSquidServerPacket extends PlayToServerPacket
{
    UUID target;
    private int squid = -1;

    public PlayerSetSquidServerPacket(PlayerEntity player)
    {
        target = player.getUUID();
    }

    public PlayerSetSquidServerPacket(PlayerEntity player, boolean set)
    {
        squid = set ? 1 : 0;
        target = player.getUUID();
    }

    protected PlayerSetSquidServerPacket(UUID player, int squid)
    {
        this.squid = squid;
        this.target = player;
    }

    public static PlayerSetSquidServerPacket decode(PacketBuffer buffer)
    {
        return new PlayerSetSquidServerPacket(buffer.readUUID(), buffer.readInt());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUUID(target);
        buffer.writeInt(squid);
    }

    @Override
    public void execute(PlayerEntity player)
    {
        World level = player.level;
        IPlayerInfo target = PlayerInfoCapability.get(level.getPlayerByUUID(this.target));

        if (squid == -1)
        {
            squid = !target.isSquid() ? 1 : 0;
        }
        target.setIsSquid(squid == 1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), squid == 1 ? SplatcraftSounds.squidTransform : SplatcraftSounds.squidRevert, SoundCategory.PLAYERS, 0.75F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);

        SplatcraftPacketHandler.sendToDim(new PlayerSetSquidClientPacket(this.target, squid), player.level);
    }

}
