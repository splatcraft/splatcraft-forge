package net.splatcraft.forge.client.audio;

import net.splatcraft.forge.data.capabilities.playerinfo.IPlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.ChargerItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.PlayerCharge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class ChargerChargingTickableSound extends TickableSound
{
    private final PlayerEntity player;
    private float prevPitch = 0;

    public ChargerChargingTickableSound(PlayerEntity player)
    {
        super(SplatcraftSounds.chargerCharge, SoundCategory.PLAYERS);
        this.attenuation = AttenuationType.NONE;
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

        if (player.isAlive() && player.getUseItem().getItem() instanceof ChargerItem && PlayerInfoCapability.hasCapability(player))
        {
            IPlayerInfo info = PlayerInfoCapability.get(player);
            if (!info.isSquid())
            {
                volume = WeaponBaseItem.hasInk(player, player.getUseItem()) ? 1 : 0;

                if (PlayerCharge.getChargeValue(player, player.getUseItem()) >= 1 && !isStopped())
                {
                    player.level.playSound(player, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.chargerReady, SoundCategory.PLAYERS, 1, 1);
                    stop();
                    return;
                }
                pitch = PlayerCharge.getChargeValue(player, player.getUseItem()) + 0.5f;
                pitch = MathHelper.lerp(Minecraft.getInstance().getDeltaFrameTime(), pitch, prevPitch);
                prevPitch = pitch;
                return;
            }
        }
        stop();
    }
}
