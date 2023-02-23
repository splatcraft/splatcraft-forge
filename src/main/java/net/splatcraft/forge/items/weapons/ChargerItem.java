package net.splatcraft.forge.items.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.client.audio.ChargerChargingTickableSound;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.ChargeableReleasePacket;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;

public class ChargerItem extends WeaponBaseItem
{
    private final AttributeModifier SPEED_MODIFIER;
    public WeaponSettings settings;
    public float chargeSpeed;
    public float dischargeSpeed;

    public ChargerItem(WeaponSettings settings)
    {
        super(settings);
        this.settings = settings;
        setRegistryName(this.settings.name);
        this.chargeSpeed = 1f / (float)settings.startupTicks;
        this.dischargeSpeed = 1f / (float)settings.dischargeTicks;

        SPEED_MODIFIER = new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Charger mobility", settings.chargerMobility - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (settings.projectileSpeed / settings.projectileLifespan * 100)));
        addStat(new WeaponTooltip("charge_speed", (stack, level) -> (int) ((40 - settings.startupTicks) / 40f * 100)));
        addStat(new WeaponTooltip("mobility", (stack, level) -> (int) (settings.chargerMobility * 100)));
    }

    public void onRelease(World level, PlayerEntity player, ItemStack stack, float charge)
    {
        InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), settings.projectileSize, settings);
        proj.setChargerStats(charge, (int) (settings.projectileLifespan * charge), charge >= settings.chargerPiercesAt);
        proj.shootFromRotation(player, player.xRot, player.yRot, 0.0f, settings.projectileSpeed, 0.1f);
        level.addFreshEntity(proj);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.chargerShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        reduceInk(player, getInkConsumption(charge), settings.inkRecoveryCooldown, false);
        PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(stack, 10, player.inventory.selected, player.getUsedItemHand(), true, false, false, player.isOnGround()));
        player.getCooldowns().addCooldown(this, 7);
    }

    @OnlyIn(Dist.CLIENT)
    protected static void playChargeReadySound(PlayerEntity player) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(player.getUUID()))
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SplatcraftSounds.chargerReady, Minecraft.getInstance().options.getSoundSourceVolume(SoundCategory.PLAYERS)));
    }

    @OnlyIn(Dist.CLIENT)
    protected static void playChargingSound(PlayerEntity player) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(player.getUUID()))
            Minecraft.getInstance().getSoundManager().queueTickingSound(new ChargerChargingTickableSound(Minecraft.getInstance().player));
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            float prevCharge = PlayerCharge.getChargeValue(player, stack);
            float newCharge = prevCharge + (chargeSpeed * (!entity.isOnGround() && !settings.fastMidAirCharge ? 0.33f : 1));
            if (enoughInk(entity, getInkConsumption(newCharge), 0, timeLeft % 4 == 0) && level.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
                if (prevCharge < 1 && newCharge >= 1) {
                    playChargeReadySound(player);
                } else if (newCharge < 1)
                    playChargingSound(player);
                PlayerCharge.addChargeValue(player, stack, newCharge - prevCharge);
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull World level, LivingEntity entity, int timeLeft)
    {
        super.releaseUsing(stack, level, entity, timeLeft);

        if (level.isClientSide && !PlayerInfoCapability.isSquid(entity) && entity instanceof PlayerEntity)
        {
            float charge = PlayerCharge.getChargeValue((PlayerEntity) entity, stack);
            if (charge > 0.05f)
            {
                PlayerCharge.reset((PlayerEntity) entity);
                PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(stack, 10, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, false, entity.isOnGround()));
                SplatcraftPacketHandler.sendToServer(new ChargeableReleasePacket(charge, stack));
            }
            PlayerCharge.setCanDischarge((PlayerEntity) entity, true);
        }
    }


    public float getInkConsumption(float charge)
    {
        return settings.minInkConsumption + (settings.inkConsumption - settings.minInkConsumption) * charge;
    }

    @Override
    public AttributeModifier getSpeedModifier(LivingEntity entity, ItemStack stack)
    {
        return SPEED_MODIFIER;
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.BOW_CHARGE;
    }
}
