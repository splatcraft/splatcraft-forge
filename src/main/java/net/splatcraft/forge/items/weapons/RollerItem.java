package net.splatcraft.forge.items.weapons;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.client.audio.RollerRollTickableSound;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.handlers.PlayerPosingHandler;
import net.splatcraft.forge.handlers.WeaponHandler;
import net.splatcraft.forge.items.weapons.settings.RollerWeaponSettings;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.BlockInkedResult;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.InkDamageUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import net.splatcraft.forge.util.WeaponTooltip;

public class RollerItem extends WeaponBaseItem {
    public static final ArrayList<RollerItem> rollers = Lists.newArrayList();

    public RollerWeaponSettings settings;
    public boolean isMoving;

    public static RegistryObject<RollerItem> create(DeferredRegister<Item> registry, RollerWeaponSettings settings) {
        return registry.register(settings.name, () -> new RollerItem(settings));
    }

    public static RegistryObject<RollerItem> create(DeferredRegister<Item> registry, RegistryObject<RollerItem> parent, String name) {
        return registry.register(name, () -> new RollerItem(parent.get().settings));
    }

    protected RollerItem(RollerWeaponSettings settings) {
        super(settings);
        this.settings = settings;
        rollers.add(this);

        addStat(new WeaponTooltip("range", (stack, level) -> (int) ((settings.flingProjectileSpeed + settings.swingProjectileSpeed) * 50)));
        addStat(new WeaponTooltip("ink_speed", (stack, level) -> (int) (settings.dashMobility / 2f * 100)));
        addStat(new WeaponTooltip("handling", (stack, level) -> (int) ((20 - (settings.flingTime + settings.swingTime) / 2f) * 5)));
    }

    public static void applyRecoilKnockback(LivingEntity entity, double pow) {
        entity.setDeltaMovement(new Vec3(Math.cos(Math.toRadians(entity.getYRot() + 90)) * -pow, entity.getDeltaMovement().y(), Math.sin(Math.toRadians(entity.getYRot() + 90)) * -pow));
        entity.hurtMarked = true;
    }

    @OnlyIn(Dist.CLIENT)
    protected static void playRollSound(boolean isBrush) {
        Minecraft.getInstance().getSoundManager().queueTickingSound(new RollerRollTickableSound(Minecraft.getInstance().player, isBrush));
    }

    public ClampedItemPropertyFunction getUnfolded() {
        return (stack, level, entity, seed) ->
        {
            if (entity instanceof Player && PlayerCooldown.hasOverloadedPlayerCooldown((Player) entity)) {

                PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown((Player) entity);
                if (cooldown.getTime() > (cooldown.isGrounded() ? -10 : 0)) {
                    ItemStack cooldownStack = cooldown.getHand() == (InteractionHand.MAIN_HAND) ? ((Player) entity).getInventory().items.get(cooldown.getSlotIndex())
                            : entity.getOffhandItem();
                    return stack.equals(cooldownStack) && (settings.isBrush || cooldown.isGrounded()) ? 1 : 0;
                }
            }
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;
        };
    }

    @Override
    public void weaponUseTick(Level level, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (!(entity instanceof Player))
            return;

        int startupTicks = entity.isOnGround() ? settings.swingTime : settings.flingTime;
        if (getUseDuration(stack) - timeLeft < startupTicks) {
            //if (getInkAmount(entity, stack) > inkConsumption){
            PlayerCooldown cooldown = new PlayerCooldown(stack, startupTicks, ((Player) entity).getInventory().selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround());
            PlayerCooldown.setPlayerCooldown((Player) entity, cooldown);
            //} else
            if (settings.isBrush && reduceInk(entity, this, entity.isOnGround() ? settings.swingConsumption : settings.flingConsumption, entity.isOnGround() ? settings.swingInkRecoveryCooldown : settings.flingInkRecoveryCooldown, timeLeft % 4 == 0)) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SplatcraftSounds.brushFling, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                int total = settings.rollSize * 2 + 1;
                for (int i = 0; i < total; i++) {
                    InkProjectileEntity proj = new InkProjectileEntity(level, entity, stack, InkBlockUtils.getInkType(entity), 1.6f, settings);
                    proj.setProjectileType(InkProjectileEntity.Types.ROLLER);
                    proj.trailSize = proj.getProjectileSize() * 0.5f;
                    proj.shootFromRotation(entity, entity.getXRot(), entity.getYRot() + (i - total / 2f) * 20, 0, entity.isOnGround() ? settings.swingProjectileSpeed : settings.flingProjectileSpeed, 0.05f);
                    proj.moveTo(proj.getX(), proj.getY() - entity.getEyeHeight() / 2f, proj.getZ());
                    level.addFreshEntity(proj);
                }
            }
            return;
        }

        float toConsume = Math.min(1, (float) (getUseDuration(stack) - timeLeft) / (float) settings.dashTime) * (settings.dashConsumption - settings.rollConsumption) + settings.rollConsumption;
        isMoving = Math.abs(entity.yHeadRotO - entity.yHeadRot) > 0 || (level.isClientSide ? Math.abs(entity.getDeltaMovement().x()) > 0 || Math.abs(entity.getDeltaMovement().z()) > 0
                : entity.position().multiply(1, 0, 1).distanceTo(WeaponHandler.getPlayerPrevPos((Player) entity).multiply(1, 0, 1)) > 0);

        double dxOff = 0;
        double dzOff = 0;
        for (int i = 1; i <= 2; i++) {
            dxOff = Math.cos(Math.toRadians(entity.getYRot() + 90)) * i;
            dzOff = Math.sin(Math.toRadians(entity.getYRot() + 90)) * i;

            BlockPos pos = new BlockPos(entity.getX() + dxOff, entity.getY(), entity.getZ() + dzOff);
            if (!InkBlockUtils.canInkPassthrough(level, pos))
                break;
        }

        boolean doPush = false;
        if (isMoving) {
            BlockInkedResult result = BlockInkedResult.FAIL;
            for (int i = 0; i < settings.rollSize; i++) {
                double off = (double) i - (settings.rollSize - 1) / 2d;
                double xOff = Math.cos(Math.toRadians(entity.getYRot())) * off;
                double zOff = Math.sin(Math.toRadians(entity.getYRot())) * off;

                for (float yOff = 0; yOff >= -3; yOff--) {
                    if (!enoughInk(entity, this, toConsume, 0, timeLeft % 4 == 0)) {
                        break;
                    }

                    if (yOff == -3) {
                        dxOff = Math.cos(Math.toRadians(entity.getYRot() + 90));
                        dzOff = Math.sin(Math.toRadians(entity.getYRot() + 90));
                    }

                    BlockPos pos = new BlockPos(entity.getX() + xOff + dxOff, entity.getY() + yOff, entity.getZ() + zOff + dzOff);

                    if (level.getBlockState(pos).getBlock() instanceof ColoredBarrierBlock && ((ColoredBarrierBlock) level.getBlockState(pos).getBlock()).canAllowThrough(pos, entity))
                        continue;

                    if (!InkBlockUtils.canInkPassthrough(level, pos)) {
                        VoxelShape shape = level.getBlockState(pos).getCollisionShape(level, pos);

                        result = InkBlockUtils.playerInkBlock((Player) entity, level, pos, ColorUtils.getInkColor(stack), settings.rollDamage, InkBlockUtils.getInkType(entity));
                        double blockHeight = shape.isEmpty() ? 0 : shape.bounds().maxY;

                        if (yOff != -3 && !(shape.bounds().minX <= 0 && shape.bounds().minZ <= 0 && shape.bounds().maxX >= 1 && shape.bounds().maxZ >= 1)) {
                            BlockInkedResult secondResult = InkBlockUtils.playerInkBlock((Player) entity, level, pos.below(), ColorUtils.getInkColor(stack), settings.rollDamage, InkBlockUtils.getInkType(entity));
                            if (result == BlockInkedResult.FAIL) {
                                result = secondResult;
                            }
                        }

                        if (result != BlockInkedResult.FAIL && i < settings.rollHitboxSize) {
                            level.addParticle(new InkSplashParticleData(ColorUtils.getInkColor(stack), 1), entity.getX() + xOff + dxOff, pos.getY() + blockHeight + 0.1, entity.getZ() + zOff + dzOff, 0, 0, 0);
                            if (i > 0) {
                                double xhOff = dxOff + Math.cos(Math.toRadians(entity.getYRot())) * (off - 0.5);
                                double zhOff = dzOff + Math.sin(Math.toRadians(entity.getYRot())) * (off - 0.5);
                                level.addParticle(new InkSplashParticleData(ColorUtils.getInkColor(stack), 1), entity.getX() + xhOff, pos.getY() + blockHeight + 0.1, entity.getZ() + zhOff, 0, 0, 0);
                            }
                        }
                        break;
                    }
                }

                if (level.isClientSide) {
                    // Damage and knockback are dealt server-side
                    continue;
                }

                if (i >= settings.rollHitboxSize) {
                    continue;
                }

                BlockPos attackPos = new BlockPos(entity.getX() + xOff + dxOff, entity.getY() - 1, entity.getZ() + zOff + dzOff);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(attackPos, attackPos.offset(1, 2, 1)), EntitySelector.NO_SPECTATORS.and(e ->
                {
                    if (e instanceof LivingEntity) {
                        if (InkDamageUtils.isSplatted((LivingEntity) e)) return false;
                        return InkDamageUtils.canDamage(e, entity) || e instanceof SquidBumperEntity;
                    }
                    return false;
                }))) {
                    if (!target.equals(entity) && (!enoughInk(entity, this, toConsume, 0, false) || !InkDamageUtils.doRollDamage(level, target, settings.rollDamage, ColorUtils.getInkColor(stack), entity, stack, false) || !InkDamageUtils.isSplatted(target))) {
                        doPush = true;
                    }
                }
            }
            if (result != BlockInkedResult.FAIL)
                reduceInk(entity, this, toConsume, settings.rollInkRecoveryCooldown, false);
        }
        if (doPush)
            applyRecoilKnockback(entity, 0.8);
    }

    @Override
    public void onPlayerCooldownEnd(Level level, Player player, ItemStack stack, PlayerCooldown cooldown) {
        boolean airborne = !cooldown.isGrounded();

        if (level.isClientSide)
            playRollSound(settings.isBrush);

        if (!settings.isBrush && reduceInk(player, this, airborne ? settings.flingConsumption : settings.swingConsumption, airborne ? settings.flingInkRecoveryCooldown : settings.swingInkRecoveryCooldown, true)) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.rollerFling, SoundSource.PLAYERS, 0.8F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
            for (int i = 0; i < settings.rollSize; i++) {

                InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), 1.6f, settings);
                proj.throwerAirborne = airborne;
                proj.setRollerSwingStats();
                proj.shootFromRotation(player, player.getXRot(), player.getYRot(), airborne ? 0.0f : settings.swingProjectilePitchCompensation, airborne ? settings.flingProjectileSpeed : settings.swingProjectileSpeed, 0.05f);
                if (airborne) {
                    double off = (double) i - (settings.rollSize - 1) / 2d;
                    double yOff = Math.sin(Math.toRadians(player.getXRot() + 90));
                    double y2Off = Math.cos(Math.toRadians(player.getXRot() + 90));
                    double xOff = Math.cos(Math.toRadians(player.getYRot() + 90)) * off * y2Off;
                    double zOff = Math.sin(Math.toRadians(player.getYRot() + 90)) * off * y2Off;
                    proj.moveTo(proj.getX() + xOff, proj.getY() + yOff * off, proj.getZ() + zOff);
                } else {
                    double off = (double) i - (settings.rollSize - 1) / 2d;
                    double xOff = Math.cos(Math.toRadians(player.getYRot())) * off;
                    double zOff = Math.sin(Math.toRadians(player.getYRot())) * off;
                    proj.moveTo(proj.getX() + xOff, proj.getY() - player.getEyeHeight() / 2f, proj.getZ() + zOff);
                }
                level.addFreshEntity(proj);
            }
        }
    }

    @Override
    public boolean hasSpeedModifier(LivingEntity entity, ItemStack stack) {
        if (entity instanceof Player && PlayerCooldown.hasPlayerCooldown((Player) entity) || !entity.getUseItem().equals(stack))
            return false;
        return super.hasSpeedModifier(entity, stack);
    }

    @Override
    public AttributeModifier getSpeedModifier(LivingEntity entity, ItemStack stack) {
        double appliedMobility;
        int useTime = entity.getUseItemRemainingTicks() - entity.getUseItemRemainingTicks();

        if (enoughInk(entity, this, Math.min(settings.dashConsumption, settings.rollConsumption), 0, false)) {
            if (entity instanceof Player && PlayerCooldown.hasPlayerCooldown((Player) entity))
                appliedMobility = settings.swingMobility;
            else
                appliedMobility = Math.min(1, (float) useTime / (float) settings.dashTime) * (settings.dashMobility - settings.rollMobility) + settings.rollMobility;
        } else {
            appliedMobility = 0.7;
        }

        return new AttributeModifier(SplatcraftItems.SPEED_MOD_UUID, "Roller Mobility", appliedMobility - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose() {
        return settings.isBrush ? PlayerPosingHandler.WeaponPose.BRUSH : PlayerPosingHandler.WeaponPose.ROLL;
    }
}
