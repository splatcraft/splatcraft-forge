package net.splatcraft.forge.items.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.client.audio.ChargerChargingTickableSound;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.ChargeableReleasePacket;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCharge;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponStat;
import org.jetbrains.annotations.NotNull;

public class ChargerItem extends WeaponBaseItem implements IChargeableWeapon
{
    private final AttributeModifier SPEED_MODIFIER;
    private final double mobility;
    public float projectileSize;
    public float projectileSpeed;
    public int projectileLifespan;
    public float chargeSpeed;
    public float dischargeSpeed;
    public float damage;
    public float pierceCharge;
    public float minConsumption;
    public float maxConsumption;
    public boolean airCharge;

    public ChargerItem(String name, float projectileSize, float projectileSpeed, int projectileLifespan, int chargeTime, int dischargeTime, float damage, float minConsumption, float maxConsumption, double mobility, boolean canAirCharge, float pierceCharge)
    {
        setRegistryName(name);

        this.projectileSize = projectileSize;
        this.projectileLifespan = projectileLifespan;
        this.chargeSpeed = 1f / (float) chargeTime;
        this.dischargeSpeed = 1f / (float) dischargeTime;
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;

        this.airCharge = canAirCharge;
        this.pierceCharge = pierceCharge;

        this.mobility = mobility;

        SPEED_MODIFIER = new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Charger Mobility", mobility - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

        this.maxConsumption = maxConsumption;
        this.minConsumption = minConsumption;

        addStat(new WeaponStat("range", (stack, level) -> (int) (projectileSpeed / projectileLifespan * 100)));
        addStat(new WeaponStat("charge_speed", (stack, level) -> (int) ((40 - chargeTime) * 100 / 40f)));
        addStat(new WeaponStat("mobility", (stack, level) -> (int) (mobility * 100)));
    }

    public ChargerItem(String name, ChargerItem parent)
    {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.projectileLifespan, (int) (1f / parent.chargeSpeed), (int) (1f / parent.dischargeSpeed), parent.damage, parent.minConsumption, parent.maxConsumption, parent.mobility, parent.airCharge, parent.pierceCharge);
    }

    @OnlyIn(Dist.CLIENT)
    protected static void playChargingSound(PlayerEntity player)
    {

        Minecraft.getInstance().getSoundManager().play(new ChargerChargingTickableSound(player));
    }

    @Override
    public float getDischargeSpeed()
    {
        return dischargeSpeed;
    }

    @Override
    public float getChargeSpeed()
    {
        return chargeSpeed;
    }

    @Override
    public void onRelease(World level, PlayerEntity player, ItemStack stack, float charge)
    {
        InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), projectileSize, charge > 0.95f ? damage : damage * charge / 4f + damage / 4f);
        proj.setChargerStats((int) (projectileLifespan * charge), charge >= pierceCharge);
        proj.shootFromRotation(player, player.xRot, player.yRot, 0.0f, projectileSpeed, 0.1f);
        level.addFreshEntity(proj);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.chargerShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
        PlayerCooldown.setPlayerCooldown(player, new PlayerCooldown(10, player.inventory.selected, player.getUsedItemHand(), true, false, false, player.isOnGround()));
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (entity instanceof PlayerEntity && level.isClientSide) {
            PlayerCharge.addChargeValue((PlayerEntity) entity, stack, chargeSpeed * (!entity.isOnGround() && !airCharge ? 0.5f : 1), timeLeft % 4 == 0);
        }
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(@NotNull World level, PlayerEntity player, @NotNull Hand hand)
    {
        ActionResult<ItemStack> result = super.use(level, player, hand);

        if (level.isClientSide && !(player.isSwimming() && !player.isInWater()))
            playChargingSound(player);

        return result;
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
                PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(10, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, false, entity.isOnGround()));
                SplatcraftPacketHandler.sendToServer(new ChargeableReleasePacket(charge, stack));
            }
            PlayerCharge.setCanDischarge((PlayerEntity) entity, true);
        }
    }


    public float getInkConsumption(float charge)
    {
        return minConsumption + (maxConsumption - minConsumption) * charge;
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
