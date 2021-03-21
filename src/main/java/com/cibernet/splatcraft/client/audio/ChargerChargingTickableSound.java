package com.cibernet.splatcraft.client.audio;

import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.weapons.ChargerItem;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.PlayerCharge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class ChargerChargingTickableSound extends TickableSound {
    private final PlayerEntity player;
    private float prevPitch = 0;

    public ChargerChargingTickableSound(PlayerEntity player) {
        super(SplatcraftSounds.chargerCharge, SoundCategory.PLAYERS);
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;

        this.player = player;
    }

    @Override
    public boolean canBeSilent() {
        return true;
    }

    @Override
    public void tick() {
        x = player.getPosX();
        y = player.getPosY();
        z = player.getPosZ();

        if (player.isAlive() && player.getActiveItemStack().getItem() instanceof ChargerItem && PlayerInfoCapability.hasCapability(player)) {
            IPlayerInfo info = PlayerInfoCapability.get(player);
            if (!info.isSquid()) {
                volume = WeaponBaseItem.hasInk(player, player.getActiveItemStack()) ? 1 : 0;

                if (PlayerCharge.getChargeValue(player, player.getActiveItemStack()) >= 1 && !isDonePlaying()) {
                    player.world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SplatcraftSounds.chargerReady, SoundCategory.PLAYERS, 1, 1);
                    finishPlaying();
                    return;
                }
                pitch = PlayerCharge.getChargeValue(player, player.getActiveItemStack()) + 0.5f;
                pitch = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), pitch, prevPitch);
                prevPitch = pitch;
                return;
            }
        }
        finishPlaying();
    }
}
