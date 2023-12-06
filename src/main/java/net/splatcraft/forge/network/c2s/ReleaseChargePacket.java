package net.splatcraft.forge.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.items.weapons.IChargeableWeapon;
import net.splatcraft.forge.util.PlayerCharge;

public class ReleaseChargePacket extends PlayC2SPacket
{
    private final float charge;
    private final ItemStack stack;
    private final boolean resetCharge;

    public ReleaseChargePacket(float charge, ItemStack stack)
    {
        this(charge, stack, true);
    }
    public ReleaseChargePacket(float charge, ItemStack stack, boolean resetCharge)
    {
        this.charge = charge;
        this.stack = stack;
        this.resetCharge = resetCharge;
    }

    public static ReleaseChargePacket decode(FriendlyByteBuf buffer)
    {
        return new ReleaseChargePacket(buffer.readFloat(), buffer.readItem(), buffer.readBoolean());
    }

    @Override
    public void execute(Player player)
    {
        if (!PlayerCharge.hasCharge(player)) {
            throw new IllegalStateException(
                    String.format("%s attempted to release a charge (%.2f; %s), but the server does not recall them having a charge",
                            player.getGameProfile(), charge, stack.getItem()));
        }

        if (stack.getItem() instanceof IChargeableWeapon weapon) {
            weapon.onReleaseCharge(player.level, player, stack, charge);
        }

        if(resetCharge)
            PlayerCharge.updateServerMap(player, false);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeFloat(charge);
        buffer.writeItem(stack);
        buffer.writeBoolean(resetCharge);
    }
}
