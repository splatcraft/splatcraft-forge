package net.splatcraft.forge.items.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.splatcraft.forge.client.handlers.SplatcraftKeyHandler;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.items.weapons.settings.SplatlingWeaponSettings;
import net.splatcraft.forge.items.weapons.settings.SplatlingWeaponSettings.FiringData;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.ReleaseChargePacket;
import net.splatcraft.forge.network.c2s.UpdateChargeStatePacket;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SplatlingItem extends WeaponBaseItem<SplatlingWeaponSettings> implements IChargeableWeapon {
	private AttributeModifier SPEED_MODIFIER;
	public ChargerChargingTickableSound chargingSound;

	protected SplatlingItem(String settingsId)
	{
		super(settingsId);
	}

	@Override
	public Class<SplatlingWeaponSettings> getSettingsClass() {
		return SplatlingWeaponSettings.class;
	}

	public static RegistryObject<SplatlingItem> create(DeferredRegister<Item> register, String settings, String name) {
		return register.register(name, () -> new SplatlingItem(settings));
	}

	public static RegistryObject<SplatlingItem> create(DeferredRegister<Item> register, RegistryObject<SplatlingItem> parent, String name) {
		return register.register(name, () -> new SplatlingItem(parent.get().settingsId.toString()));
	}

	@OnlyIn(Dist.CLIENT)
	protected static void playChargeReadySound(Player player, float pitch) {
		if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(player.getUUID()))
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SplatcraftSounds.splatlingReady, pitch, Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.PLAYERS)));
	}


	@OnlyIn(Dist.CLIENT)
	protected void playChargingSound(Player player) {
		if (Minecraft.getInstance().player == null || !Minecraft.getInstance().player.getUUID().equals(player.getUUID()) || (chargingSound != null && !chargingSound.isStopped())) {
			return;
		}

		chargingSound = new ChargerChargingTickableSound(Minecraft.getInstance().player, SplatcraftSounds.splatlingCharge);
		Minecraft.getInstance().getSoundManager().play(chargingSound);
	}

	private static final int maxCharges = 2;

	@Override
	public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft)
	{
		if (entity instanceof Player player && level.isClientSide)
		{
			SplatlingWeaponSettings settings = getSettings(stack);

			float prevCharge = PlayerCharge.getChargeValue(player, stack);
			float newCharge = prevCharge + 1f / (prevCharge >= 1 ? settings.secondLevelChargeTime : settings.firstLevelChargeTime);

			if (!enoughInk(entity, this, getScaledSettingFloat(getSettings(stack), newCharge, FiringData::getInkConsumption), 0, timeLeft % 4 == 0))
			{
				if(!hasInkInTank(player, this) || !InkTankItem.canRecharge(player.getItemBySlot(EquipmentSlot.CHEST), true))
					return;
				newCharge = prevCharge + 1f / (prevCharge >= 1 ? settings.emptyTankSecondLevelChargeTime : settings.emptyTankFirstLevelChargeTime);
			}


			if (prevCharge < maxCharges && newCharge >= Math.ceil(prevCharge) && prevCharge > 0) {
				playChargeReadySound(player, newCharge / maxCharges);
			} else if (newCharge < maxCharges) {
				playChargingSound(player);
			}

			PlayerCharge.addChargeValue(player, stack, newCharge - prevCharge, true, maxCharges);
		}
	}

	@Override
	public void onPlayerCooldownEnd(Level level, Player player, ItemStack stack, PlayerCooldown cooldown)
	{
		if(cooldown.getTime() > 0 && level.isClientSide && PlayerCharge.hasCharge(player))
		{
			PlayerCharge charge = PlayerCharge.getCharge(player);
			charge.reset();
			SplatcraftPacketHandler.sendToServer(new UpdateChargeStatePacket(false));
		}
	}

	@Override
	public void onPlayerCooldownTick(Level level, Player player, ItemStack stack, PlayerCooldown cooldown)
	{
		if(level.isClientSide)
			return;

		SplatlingWeaponSettings settings = getSettings(stack);
		float charge = stack.getOrCreateTag().getFloat("Charge");

		FiringData firingData =  charge > 1 ? settings.secondChargeLevelData : settings.firstChargeLevelData;
		int firingSpeed = getScaledSettingInt(settings, charge, FiringData::getFiringSpeed);

		if (firingSpeed > 0 && (cooldown.getTime() - 1) % firingSpeed == 0)
		{
			for(int i = 0; i < firingData.projectileCount; i++)
			{
				InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), firingData.projectileSize, settings);
				proj.setSplatlingStats(settings, charge);
				proj.shootFromRotation(player, player.getXRot(), player.getYRot(), firingData.pitchCompensation, getScaledSettingFloat(settings, charge, FiringData::getProjectileSpeed),
						player.isOnGround() ? firingData.groundInaccuracy : firingData.airInaccuracy);
				level.addFreshEntity(proj);
			}

			level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.splatlingShot, SoundSource.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);

		}
	}

	@Override
	public void onReleaseCharge(Level level, Player player, ItemStack stack, float charge)
	{
		SplatlingWeaponSettings settings = getSettings(stack);

		stack.getOrCreateTag().putFloat("Charge", charge);

		int cooldownTime = (int) (getDecayTicks(stack) * charge);
		reduceInk(player, this, getScaledSettingFloat(settings, charge, FiringData::getInkConsumption), cooldownTime + getScaledSettingInt(settings, charge, FiringData::getInkRecoveryCooldown), true);
		PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(stack, cooldownTime, player.getInventory().selected, player.getUsedItemHand(), true, false, !settings.canRechargeWhileFiring, player.isOnGround()).setCancellable());
	}

	@Override
	public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entity, int timeLeft)
	{
		super.releaseUsing(stack, level, entity, timeLeft);

		if (level.isClientSide && entity instanceof Player player)
		{
			if(PlayerCooldown.hasPlayerCooldown(player) && PlayerCooldown.getPlayerCooldown(player).preventWeaponUse())
				return;

			PlayerCharge charge = PlayerCharge.getCharge(player);

			if(charge == null)
				return;

			/*
			if(PlayerInfoCapability.isSquid(player))
			{
				charge.reset();
				SplatcraftPacketHandler.sendToServer(new UpdateChargeStatePacket(false));
			}
			else */


			if (!SplatcraftKeyHandler.isSquidKeyDown() && charge.charge > 0.05f) //checking for squid key press so it doesn't immediately release charge when squidding
			{
				SplatlingWeaponSettings settings = getSettings(stack);
				PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(stack, (int) (settings.firingDuration * charge.charge), player.getInventory().selected, player.getUsedItemHand(), true, false, !settings.canRechargeWhileFiring, player.isOnGround()).setCancellable());
				SplatcraftPacketHandler.sendToServer(new ReleaseChargePacket(charge.charge, stack, false));
			}

		}
	}

	public static float getScaledSettingFloat(SplatlingWeaponSettings settings, float charge, Function<SplatlingWeaponSettings.FiringData, Float> getter)
	{
		float min = getter.apply(settings.firstChargeLevelData);
		float max = getter.apply(settings.secondChargeLevelData);
		return min + (max - min) * Mth.clamp(charge, 0, 1);
	}

	public static int getScaledSettingInt(SplatlingWeaponSettings settings, float charge, Function<SplatlingWeaponSettings.FiringData, Integer> getter)
	{
		float min = getter.apply(settings.firstChargeLevelData);
		float max = getter.apply(settings.secondChargeLevelData);
		return Math.round(min + (max - min) * Mth.clamp(charge, 0, 1));
	}

	@Override
	public AttributeModifier getSpeedModifier(LivingEntity entity, ItemStack stack)
	{
		if(SPEED_MODIFIER == null)
			SPEED_MODIFIER = new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Splatling mobility", getSettings(stack).moveSpeed - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

		return SPEED_MODIFIER;
	}

	@Override
	public PlayerPosingHandler.WeaponPose getPose(ItemStack stack) {
		return PlayerPosingHandler.WeaponPose.SPLATLING;
	}

	@Override
	public int getDischargeTicks(ItemStack stack) {
		return getSettings(stack).chargeStorageTime;
	}

	@Override
	public int getDecayTicks(ItemStack stack) {
		return getSettings(stack).firingDuration;
	}
}