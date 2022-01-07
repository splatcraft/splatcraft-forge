package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class PlayerSetSquidClientPacket extends PlayToClientPacket
{
    UUID target;
    private int squid = -1;

    public PlayerSetSquidClientPacket(PlayerEntity player)
    {
        target = player.getUUID();
    }

    public PlayerSetSquidClientPacket(PlayerEntity player, boolean set)
    {
        squid = set ? 1 : 0;
        target = player.getUUID();
    }

    protected PlayerSetSquidClientPacket(UUID player, int squid)
    {
        this.squid = squid;
        this.target = player;
    }

    public static PlayerSetSquidClientPacket decode(PacketBuffer buffer)
    {
        return new PlayerSetSquidClientPacket(buffer.readUUID(), buffer.readInt());
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUUID(target);
        buffer.writeInt(squid);
    }

    @Override
    public void execute()
    {
        if (Minecraft.getInstance().level.getPlayerByUUID(this.target) == null)
        {
            return;
        }
        IPlayerInfo target = PlayerInfoCapability.get(Minecraft.getInstance().level.getPlayerByUUID(this.target));

        if (squid == -1)
        {
            squid = !target.isSquid() ? 1 : 0;
        }
        target.setIsSquid(squid == 1);

    }

}
