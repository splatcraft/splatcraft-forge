package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.items.weapons.DualieItem;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class DodgeRollPacket extends PlayToServerPacket
{
    UUID target;
    ItemStack mainDualie;
    ItemStack offhandDualie;

    public DodgeRollPacket(PlayerEntity player, ItemStack mainDualie, ItemStack offhandDualie)
    {
        this(player.getUniqueID(), mainDualie, offhandDualie);
    }

    public DodgeRollPacket(UUID target, ItemStack mainDualie, ItemStack offhandDualie)
    {
        this.target = target;
        this.mainDualie = mainDualie;
        this.offhandDualie = offhandDualie;
    }

    public static DodgeRollPacket decode(PacketBuffer buffer)
    {
        return new DodgeRollPacket(buffer.readUniqueId(), buffer.readItemStack(), buffer.readItemStack());
    }

    @Override
    public void execute(PlayerEntity player)
    {
        PlayerEntity target = player.world.getPlayerByUuid(this.target);
        DualieItem.performRoll(target, mainDualie, offhandDualie);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUniqueId(target);
        buffer.writeItemStack(mainDualie);
        buffer.writeItemStack(offhandDualie);
    }
}
