package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
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
        target = player.getUniqueID();
    }

    public PlayerSetSquidServerPacket(PlayerEntity player, boolean set)
    {
        squid = set ? 1 : 0;
        target = player.getUniqueID();
    }

    protected PlayerSetSquidServerPacket(UUID player, int squid)
    {
        this.squid = squid;
        this.target = player;
    }

    public static PlayerSetSquidServerPacket decode(PacketBuffer buffer)
    {
        return new PlayerSetSquidServerPacket(buffer.readUniqueId(), buffer.readInt());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUniqueId(target);
        buffer.writeInt(squid);
    }

    @Override
    public void execute(PlayerEntity player)
    {
        World world = player.world;
        IPlayerInfo target = PlayerInfoCapability.get(world.getPlayerByUuid(this.target));

        if (squid == -1)
        {
            squid = !target.isSquid() ? 1 : 0;
        }
        target.setIsSquid(squid == 1);
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), squid == 1 ? SplatcraftSounds.squidTransform : SplatcraftSounds.squidRevert, SoundCategory.PLAYERS, 0.75F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);

        SplatcraftPacketHandler.sendToDim(new PlayerSetSquidClientPacket(this.target, squid), player.world);
    }

}
