package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.data.capabilities.inkoverlay.IInkOverlayInfo;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
import com.cibernet.splatcraft.network.base.PlayToClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class UpdateInkOverlayPacket extends PlayToClientPacket
{
    int entityId;
    CompoundNBT nbt;

    public UpdateInkOverlayPacket(LivingEntity entity, IInkOverlayInfo info)
    {
        this(entity.getId(), info.writeNBT(new CompoundNBT()));
    }

    public UpdateInkOverlayPacket(int entity, CompoundNBT info)
    {
        this.entityId = entity;
        this.nbt = info;
    }

    public static UpdateInkOverlayPacket decode(PacketBuffer buffer)
    {
        return new UpdateInkOverlayPacket(buffer.readInt(), buffer.readNbt());
    }

    @Override
    public void execute()
    {
        Entity entity = Minecraft.getInstance().level.getEntity(entityId);

        if (!(entity instanceof LivingEntity) || !InkOverlayCapability.hasCapability((LivingEntity) entity))
        {
            return;
        }
        InkOverlayCapability.get((LivingEntity) entity).readNBT(nbt);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeNbt(nbt);
    }
}
