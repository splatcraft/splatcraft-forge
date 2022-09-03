package net.splatcraft.forge.network.c2s;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.splatcraft.forge.items.weapons.IChargeableWeapon;
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

    public static ChargeableReleasePacket decode(PacketBuffer buffer)
    {
        return new ChargeableReleasePacket(buffer.readFloat(), buffer.readItem());
    }

    @Override
    public void execute(PlayerEntity player)
    {
        PlayerCharge.setCharge(player, new PlayerCharge(stack, charge));

        if (stack.getItem() instanceof IChargeableWeapon)
        {
            IChargeableWeapon weapon = (IChargeableWeapon) stack.getItem();
            weapon.onRelease(player.level, player, stack, charge);
        }
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeFloat(charge);
        buffer.writeItem(stack);
    }
}
