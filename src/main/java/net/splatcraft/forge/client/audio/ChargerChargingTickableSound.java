package net.splatcraft.forge.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.IChargeableWeapon;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.PlayerCharge;

public class ChargerChargingTickableSound extends AbstractTickableSoundInstance
{
    private final Player player;
    private float prevPitch = 0;

    public ChargerChargingTickableSound(Player player)
    {
        super(SplatcraftSounds.chargerCharge, SoundSource.PLAYERS);
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;

        this.player = player;
    }

    @Override
    public boolean canStartSilent()
    {
        return true;
    }

    @Override
    public void tick()
    {
        x = player.getX();
        y = player.getY();
        z = player.getZ();

        if (player.isAlive() && player.getUseItem().getItem() instanceof IChargeableWeapon && PlayerInfoCapability.hasCapability(player))
        {
            PlayerInfo info = PlayerInfoCapability.get(player);
            if (!info.isSquid())
            {
                float charge = PlayerCharge.getChargeValue(player, player.getUseItem());
                if (charge >= 1 && !isStopped())
                {
                    stop();
                    return;
                }
                pitch = charge + 0.5f;
                pitch = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime(), prevPitch, pitch);
                prevPitch = pitch;
                return;
            }
        }
        stop();
    }
}
