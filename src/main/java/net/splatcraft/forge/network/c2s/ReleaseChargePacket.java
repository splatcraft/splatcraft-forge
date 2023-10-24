package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.items.weapons.IChargeableWeapon;
import net.splatcraft.forge.util.PlayerCharge;

public class ReleaseChargePacket extends PlayToServerPacket
{
    private final float charge;
    private final ItemStack stack;

    public ReleaseChargePacket(float charge, ItemStack stack)
    {
        this.charge = charge;
        this.stack = stack;
    }

    public static ReleaseChargePacket decode(FriendlyByteBuf buffer)
    {
        return new ReleaseChargePacket(buffer.readFloat(), buffer.readItem());
    }

    @Override
    public void execute(Player player)
    {
        if (!PlayerCharge.hasCharge(player)) {
            throw new IllegalStateException("Released a non-existent charge");
        }

        if (stack.getItem() instanceof IChargeableWeapon weapon) {
            weapon.onRelease(player.level, player, stack, charge);
        }

        PlayerCharge.updateServerMap(player, false);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(charge);
        buffer.writeItem(stack);
    }
}
