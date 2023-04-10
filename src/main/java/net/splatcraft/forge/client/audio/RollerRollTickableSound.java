package net.splatcraft.forge.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.handlers.WeaponHandler;
import net.splatcraft.forge.items.weapons.RollerItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.registries.SplatcraftSounds;

public class RollerRollTickableSound extends AbstractTickableSoundInstance
{
    private final Player player;
    private float distance = 0.0F;


    public RollerRollTickableSound(Player player, boolean isBrush)
    {
        super(isBrush ? SplatcraftSounds.brushRoll : SplatcraftSounds.rollerRoll, SoundSource.PLAYERS);
        this.looping = true;
        this.delay = 0;

        this.player = player;
        this.volume = 0;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public boolean canStartSilent()
    {
        return true;
    }

    @Override
    public void tick()
    {
        if (this.player.isAlive() && player.getUseItem().getItem() instanceof RollerItem)
        {
            ItemStack roller = player.getUseItem();
            if (!WeaponBaseItem.enoughInk(player, roller.getItem(), Math.max(((RollerItem) roller.getItem()).settings.rollConsumption, ((RollerItem) roller.getItem()).settings.dashConsumption), 7, false)) {
                stop();
                return;
            }

            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();

            Vec3 motion = player.equals(Minecraft.getInstance().player) ? player.getDeltaMovement() : player.position().subtract(WeaponHandler.getPlayerPrevPos(player));
            double vol = Math.max(Math.abs(player.yHeadRotO - player.yHeadRot), motion.multiply(1, 0, 1).length()) * 3f;

            if (vol >= 0.01D)
            {
                this.distance = Mth.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
                this.volume = (float) Mth.lerp(Mth.clamp(vol, 0.0F, 0.5F), 0.0F, 1F);
            } else
            {
                this.distance = 0.0F;
                this.volume = 0.0F;
            }

        } else stop();
    }
}
