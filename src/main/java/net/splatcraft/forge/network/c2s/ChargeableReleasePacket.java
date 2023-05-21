package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.items.weapons.ChargerItem;
import net.splatcraft.forge.util.PlayerCharge;

public class ChargeableReleasePacket extends PlayToServerPacket
{
    private final float charge;
    private final ItemStack stack;

    public ChargeableReleasePacket(float charge, ItemStack stack)
    {
        this.charge = charge;
        this.stack = stack;
    }

    public static ChargeableReleasePacket decode(FriendlyByteBuf buffer)
    {
        return new ChargeableReleasePacket(buffer.readFloat(), buffer.readItem());
    }

    @Override
    public void execute(Player player)
    {
        PlayerCharge.setCharge(player, new PlayerCharge(stack, charge));

        if (stack.getItem() instanceof ChargerItem weapon) {
            weapon.onRelease(player.level, player, stack, charge);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(charge);
        buffer.writeItem(stack);
    }
}
