package net.splatcraft.forge.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayCapability;
import net.splatcraft.forge.data.capabilities.inkoverlay.InkOverlayInfo;

public class UpdateInkOverlayPacket extends PlayS2CPacket
{
    int entityId;
    CompoundTag nbt;

    public UpdateInkOverlayPacket(LivingEntity entity, InkOverlayInfo info)
    {
        this(entity.getId(), info.writeNBT(new CompoundTag()));
    }

    public UpdateInkOverlayPacket(int entity, CompoundTag info)
    {
        this.entityId = entity;
        this.nbt = info;
    }

    public static UpdateInkOverlayPacket decode(FriendlyByteBuf buffer)
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
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeNbt(nbt);
    }
}
