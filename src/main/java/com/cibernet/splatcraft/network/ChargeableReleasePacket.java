package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.items.weapons.IChargeableWeapon;
import com.cibernet.splatcraft.network.base.PlayToServerPacket;
import com.cibernet.splatcraft.util.PlayerCharge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

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
        return new ChargeableReleasePacket(buffer.readFloat(), buffer.readItemStack());
    }

    @Override
    public void execute(PlayerEntity player)
    {
        PlayerCharge.setCharge(player, new PlayerCharge(stack, charge));

        if (stack.getItem() instanceof IChargeableWeapon)
        {
            IChargeableWeapon weapon = (IChargeableWeapon) stack.getItem();
            weapon.onRelease(player.world, player, stack, charge);
        }
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeFloat(charge);
        buffer.writeItemStack(stack);
    }
}
