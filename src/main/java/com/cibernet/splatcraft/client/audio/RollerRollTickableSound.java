package com.cibernet.splatcraft.client.audio;

import com.cibernet.splatcraft.handlers.WeaponHandler;
import com.cibernet.splatcraft.items.weapons.RollerItem;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class RollerRollTickableSound extends TickableSound
{
    private final PlayerEntity player;
    private float distance = 0.0F;

    public RollerRollTickableSound(PlayerEntity player, boolean isBrush)
    {
        super(isBrush ? SplatcraftSounds.brushRoll : SplatcraftSounds.rollerRoll, SoundCategory.PLAYERS);
        this.repeat = true;
        this.repeatDelay = 0;

        this.player = player;
        this.volume = 0;
        this.x = player.getPosX();
        this.y = player.getPosY();
        this.z = player.getPosZ();
    }


    @Override
    public boolean canBeSilent()
    {
        return true;
    }

    @Override
    public void tick()
    {
        if (this.player.isAlive() && player.getActiveItemStack().getItem() instanceof RollerItem)
        {
            ItemStack roller = player.getActiveItemStack();
            if (WeaponBaseItem.getInkAmount(player, roller) < Math.max(((RollerItem) roller.getItem()).rollConsumptionMin, ((RollerItem) roller.getItem()).rollConsumptionMax))
            {
                finishPlaying();
                return;
            }

            this.x = (float) this.player.getPosX();
            this.y = (float) this.player.getPosY();
            this.z = (float) this.player.getPosZ();

            Vector3d motion = player.equals(Minecraft.getInstance().player) ? player.getMotion() : player.getPositionVec().subtract(WeaponHandler.getPlayerPrevPos(player));
            float vol = Math.max(Math.abs(player.prevRotationYawHead - player.rotationYawHead), MathHelper.sqrt(Entity.horizontalMag(motion))) * 3f;

            if ((double) vol >= 0.01D)
            {
                this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
                this.volume = MathHelper.lerp(MathHelper.clamp(vol, 0.0F, 0.5F), 0.0F, 1F);
            } else
            {
                this.distance = 0.0F;
                this.volume = 0.0F;
            }

        } else finishPlaying();
    }
}
