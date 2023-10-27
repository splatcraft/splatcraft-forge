package net.splatcraft.forge.items.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.client.audio.ChargerChargingTickableSound;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.weapons.settings.WeaponSettings;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.ReleaseChargePacket;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;
import org.jetbrains.annotations.NotNull;

public class ChargerItem extends WeaponBaseItem<WeaponSettings>
{
    private AttributeModifier SPEED_MODIFIER;
    public ChargerChargingTickableSound chargingSound;

    public static RegistryObject<ChargerItem> create(DeferredRegister<Item> register, String settings, String name)
    {
        return register.register(name, () -> new ChargerItem(settings));
    }

    public static RegistryObject<ChargerItem> create(DeferredRegister<Item> register, RegistryObject<ChargerItem> parent, String name)
    {
        return register.register(name, () -> new ChargerItem(parent.get().settingsId.toString()));
    }

    protected ChargerItem(String settings)
    {
        super(settings);

        addStat(new WeaponTooltip("range", (stack, level) -> (int) (getSettings(stack).projectileSpeed / getSettings(stack).projectileLifespan * 100)));
        addStat(new WeaponTooltip("charge_speed", (stack, level) -> (int) ((40 - getSettings(stack).startupTicks) / 40f * 100)));
        addStat(new WeaponTooltip("mobility", (stack, level) -> (int) (getSettings(stack).chargerMobility * 100)));
    }

    @Override
    public Class<WeaponSettings> getSettingsClass() {
        return WeaponSettings.class;
    }

    public void onRelease(Level level, Player player, ItemStack stack, float charge)
    {
        InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), getSettings(stack).projectileSize, getSettings(stack));
        proj.setChargerStats(charge, (int) (getSettings(stack).projectileLifespan * charge), charge >= getSettings(stack).chargerPiercesAt);
        proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, getSettings(stack).projectileSpeed, 0.1f);
        level.addFreshEntity(proj);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.chargerShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        reduceInk(player, this, getInkConsumption(charge, stack), getSettings(stack).inkRecoveryCooldown, false);
        PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(stack, 10, player.getInventory().selected, player.getUsedItemHand(), true, false, false, player.isOnGround()));
        player.getCooldowns().addCooldown(this, 7);
    }

    @OnlyIn(Dist.CLIENT)
    protected static void playChargeReadySound(Player player) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(player.getUUID()))
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SplatcraftSounds.chargerReady, Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.PLAYERS)));
    }


    @OnlyIn(Dist.CLIENT)
    protected static void playChargingSound(Player player) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(player.getUUID()))
            Minecraft.getInstance().getSoundManager().queueTickingSound(new ChargerChargingTickableSound(Minecraft.getInstance().player));
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (entity instanceof Player player) {
            float prevCharge = PlayerCharge.getChargeValue(player, stack);
            float newCharge = prevCharge + (getSettings(stack).chargeSpeed * (!entity.isOnGround() && !getSettings(stack).fastMidAirCharge ? 0.33f : 1));
            if (level.isClientSide && !player.getCooldowns().isOnCooldown(this) && enoughInk(entity, this, getInkConsumption(newCharge, stack), 0, timeLeft % 4 == 0)) {
                if (prevCharge < 1 && newCharge >= 1) {
                    playChargeReadySound(player);
                } else if (newCharge < 1)
                    playChargingSound(player);
                PlayerCharge.addChargeValue(player, stack, newCharge - prevCharge);
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);

        if (level.isClientSide && !PlayerInfoCapability.isSquid(entity) && entity instanceof Player player) {
            PlayerCharge charge = PlayerCharge.getCharge(player);
            if (charge != null && charge.charge > 0.05f) {
                PlayerCooldown.setPlayerCooldown((Player) entity, new PlayerCooldown(stack, 10, ((Player) entity).getInventory().selected, entity.getUsedItemHand(), true, false, false, entity.isOnGround()));
                SplatcraftPacketHandler.sendToServer(new ReleaseChargePacket(charge.charge, stack));
                charge.reset();
            }
        }
    }


    public float getInkConsumption(float charge, ItemStack stack)
    {
        WeaponSettings settings = getSettings(stack);
        return settings.minInkConsumption + (settings.inkConsumption - settings.minInkConsumption) * charge;
    }

    @Override
    public AttributeModifier getSpeedModifier(LivingEntity entity, ItemStack stack)
    {
        if(SPEED_MODIFIER == null)
            SPEED_MODIFIER = new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Charger mobility", getSettings(stack).chargerMobility - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        return SPEED_MODIFIER;
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose(ItemStack stack)
    {
        return PlayerPosingHandler.WeaponPose.BOW_CHARGE;
    }
}
