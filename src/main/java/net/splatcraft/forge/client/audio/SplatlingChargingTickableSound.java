package net.splatcraft.forge.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.IChargeableWeapon;
import net.splatcraft.forge.util.PlayerCharge;
import org.jetbrains.annotations.Nullable;

public class SplatlingChargingTickableSound extends AbstractTickableSoundInstance
{
    private final Player player;
    private final SoundEvent soundEvent;

    private static final int maxFadeTime = 30;
    private int fadeTime = -1;
    private boolean isFadeIn = false;

    @Nullable
    private Boolean playingSecondLevel = null;

    public SplatlingChargingTickableSound(Player player, SoundEvent sound)
    {
        super(sound, SoundSource.PLAYERS);
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;

        this.player = player;
        soundEvent = sound;
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
            if (!info.isSquid() && PlayerCharge.chargeMatches(player, player.getUseItem()))
            {
                float charge = PlayerCharge.getChargeValue(player, player.getUseItem());
                float prevCharge = info.getPlayerCharge().prevCharge;

                if(playingSecondLevel == null)
                    playingSecondLevel = charge > 1;


                if(!isFadeIn && fadeTime == 0)
                {
                    stop();
                    return;
                }
                else if(fadeTime > maxFadeTime)
                    fadeTime = -1;
                else if(fadeTime > 0)
                {
                    fadeTime += isFadeIn ? 1 : -1;
                    volume = fadeTime / (float) maxFadeTime;
                }

                pitch = (Mth.lerp(Minecraft.getInstance().getDeltaFrameTime(), prevCharge, charge) / info.getPlayerCharge().totalCharges) * 0.5f + 0.5f;
                return;
            }
        }
        stop();
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }

    public void fadeOut()
    {
        fadeTime = maxFadeTime;
        isFadeIn = false;
    }

    public void fadeIn()
    {
        fadeTime = 0;
        isFadeIn = true;
    }
}
