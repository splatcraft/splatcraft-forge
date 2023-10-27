package net.splatcraft.forge.network.c2s;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.items.weapons.DualieItem;

public class DodgeRollPacket extends PlayC2SPacket
{
    UUID target;
    ItemStack mainDualie;
    ItemStack offhandDualie;

    public DodgeRollPacket(Player player, ItemStack mainDualie, ItemStack offhandDualie)
    {
        this(player.getUUID(), mainDualie, offhandDualie);
    }

    public DodgeRollPacket(UUID target, ItemStack mainDualie, ItemStack offhandDualie)
    {
        this.target = target;
        this.mainDualie = mainDualie;
        this.offhandDualie = offhandDualie;
    }

    public static DodgeRollPacket decode(FriendlyByteBuf buffer)
    {
        return new DodgeRollPacket(buffer.readUUID(), buffer.readItem(), buffer.readItem());
    }

    @Override
    public void execute(Player player)
    {
        Player target = player.level.getPlayerByUUID(this.target);
        ((DualieItem) mainDualie.getItem()).performRoll(target, mainDualie, offhandDualie);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(target);
        buffer.writeItem(mainDualie);
        buffer.writeItem(offhandDualie);
    }
}
