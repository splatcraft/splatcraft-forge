package com.cibernet.splatcraft.client.audio;

import com.cibernet.splatcraft.items.weapons.RollerItem;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

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
            System.out.println("roll");
            ItemStack roller = player.getActiveItemStack();
            if (WeaponBaseItem.getInkAmount(player, roller) < Math.max(((RollerItem) roller.getItem()).rollConsumptionMin, ((RollerItem) roller.getItem()).rollConsumptionMax))
            {
                finishPlaying();
                return;
            }

            this.x = (float) this.player.getPosX();
            this.y = (float) this.player.getPosY();
            this.z = (float) this.player.getPosZ();
            float lvt_1_1_ = MathHelper.sqrt(Entity.horizontalMag(this.player.getMotion())) * 3f;
            if ((double) lvt_1_1_ >= 0.01D)
            {
                this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
                this.volume = MathHelper.lerp(MathHelper.clamp(lvt_1_1_, 0.0F, 0.5F), 0.0F, 1F);
            } else
            {
                this.distance = 0.0F;
                this.volume = 0.0F;
            }
            System.out.println(volume);

        } else
        {
            finishPlaying();
        }
    }
}
