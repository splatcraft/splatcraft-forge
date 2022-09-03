package net.splatcraft.forge.network.c2s;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.splatcraft.forge.items.weapons.DualieItem;

import java.util.UUID;

public class DodgeRollPacket extends PlayToServerPacket
{
    UUID target;
    ItemStack mainDualie;
    ItemStack offhandDualie;

    public DodgeRollPacket(PlayerEntity player, ItemStack mainDualie, ItemStack offhandDualie)
    {
        this(player.getUUID(), mainDualie, offhandDualie);
    }

    public DodgeRollPacket(UUID target, ItemStack mainDualie, ItemStack offhandDualie)
    {
        this.target = target;
        this.mainDualie = mainDualie;
        this.offhandDualie = offhandDualie;
    }

    public static DodgeRollPacket decode(PacketBuffer buffer)
    {
        return new DodgeRollPacket(buffer.readUUID(), buffer.readItem(), buffer.readItem());
    }

    @Override
    public void execute(PlayerEntity player)
    {
        PlayerEntity target = player.level.getPlayerByUUID(this.target);
        DualieItem.performRoll(target, mainDualie, offhandDualie);
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeUUID(target);
        buffer.writeItem(mainDualie);
        buffer.writeItem(offhandDualie);
    }
}
