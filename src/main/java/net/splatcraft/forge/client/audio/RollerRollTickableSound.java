package net.splatcraft.forge.client.audio;

import net.splatcraft.forge.handlers.WeaponHandler;
import net.splatcraft.forge.items.weapons.RollerItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.registries.SplatcraftSounds;
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
            if (WeaponBaseItem.getInkAmount(player, roller) < Math.max(((RollerItem) roller.getItem()).rollConsumptionMin, ((RollerItem) roller.getItem()).rollConsumptionMax))
            {
                stop();
                return;
            }

            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();

            Vector3d motion = player.equals(Minecraft.getInstance().player) ? player.getDeltaMovement() : player.position().subtract(WeaponHandler.getPlayerPrevPos(player));
            float vol = Math.max(Math.abs(player.yHeadRotO - player.yHeadRot), MathHelper.sqrt(Entity.getHorizontalDistanceSqr(motion))) * 3f;

            if ((double) vol >= 0.01D)
            {
                this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
                this.volume = MathHelper.lerp(MathHelper.clamp(vol, 0.0F, 0.5F), 0.0F, 1F);
            } else
            {
                this.distance = 0.0F;
                this.volume = 0.0F;
            }

        } else stop();
    }
}
