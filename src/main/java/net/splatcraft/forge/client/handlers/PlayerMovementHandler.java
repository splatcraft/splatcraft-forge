package net.splatcraft.forge.client.handlers;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.RollerItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.registries.SplatcraftAttributes;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;

import java.util.UUID;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerMovementHandler
{
    private static final AttributeModifier INK_SWIM_SPEED = new AttributeModifier("Ink swimming speed boost", 0D, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SQUID_SWIM_SPEED = new AttributeModifier("Squid swim speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final AttributeModifier ENEMY_INK_SPEED = new AttributeModifier("Enemy ink speed penalty", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final AttributeModifier SLOW_FALLING = new AttributeModifier(UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA"), "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void playerMovement(TickEvent.PlayerTickEvent event)
    {
        if (!(event.player instanceof LocalPlayer) || event.phase != TickEvent.Phase.END)
            return;

        LocalPlayer player = (LocalPlayer) event.player;
        //MovementInput input = player.movementInput;
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance swimAttribute = player.getAttribute(ForgeMod.SWIM_SPEED.get());

        if (speedAttribute.hasModifier(INK_SWIM_SPEED))
            speedAttribute.removeModifier(INK_SWIM_SPEED);
        if (speedAttribute.hasModifier(ENEMY_INK_SPEED))
            speedAttribute.removeModifier(ENEMY_INK_SPEED);
        if (swimAttribute.hasModifier(SQUID_SWIM_SPEED))
            swimAttribute.removeModifier(SQUID_SWIM_SPEED);

        if (speedAttribute.getModifier(SplatcraftItems.SPEED_MOD_UUID) != null)
            speedAttribute.removeModifier(SplatcraftItems.SPEED_MOD_UUID);

        if (InkBlockUtils.onEnemyInk(player))
        {
            //player.setDeltaMovement(player.getDeltaMovement().x, Math.min(player.getDeltaMovement().y, 0.05f), player.getDeltaMovement().z);
            if (!speedAttribute.hasModifier(ENEMY_INK_SPEED))
                speedAttribute.addTransientModifier(ENEMY_INK_SPEED);
        }

        ItemStack useStack = player.getUseItem();
        if(PlayerCooldown.hasPlayerCooldown(player))
            useStack = PlayerCooldown.getPlayerCooldown(player).storedStack;

        if (useStack.getItem() instanceof WeaponBaseItem<?> weapon && weapon.hasSpeedModifier(player, player.getUseItem()))
        {
            AttributeModifier mod = weapon.getSpeedModifier(player, player.getUseItem());
            if (!speedAttribute.hasModifier(mod))
                speedAttribute.addTransientModifier(mod);
        }

        if (PlayerInfoCapability.isSquid(player))
        {
            if (InkBlockUtils.canSquidSwim(player) && !speedAttribute.hasModifier(INK_SWIM_SPEED) && player.isOnGround())
                speedAttribute.addTransientModifier(INK_SWIM_SPEED);
            if (!swimAttribute.hasModifier(SQUID_SWIM_SPEED))
                swimAttribute.addTransientModifier(SQUID_SWIM_SPEED);
        }

        if (PlayerCooldown.hasPlayerCooldown(player) && PlayerCooldown.getPlayerCooldown(player).getSlotIndex() >= 0)
        {
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
            player.getInventory().selected = cooldown.getSlotIndex();
        }

        if (!player.getAbilities().flying)
        {
            if (speedAttribute.hasModifier(INK_SWIM_SPEED))
                player.moveRelative((float) player.getAttributeValue(SplatcraftAttributes.inkSwimSpeed.get()) * (player.isOnGround() ? 1 : 0.75f), new Vec3(player.xxa, 0.0f, player.zza).normalize());

        }
    }

    @SubscribeEvent
    public static void onInputUpdate(net.minecraftforge.client.event.MovementInputUpdateEvent event)
    {

        Input input = event.getInput();
        Player player = event.getPlayer();

        float speedMod = !input.shiftKeyDown ? PlayerInfoCapability.isSquid(player) && InkBlockUtils.canSquidHide(player) ? 30f : 2f : 1f;

        input.forwardImpulse *= speedMod;
        //input = player.movementInput;
        input.leftImpulse *= speedMod;
        //input = player.movementInput;

        if (PlayerInfoCapability.isSquid(player) && InkBlockUtils.canSquidClimb(player) && !player.getAbilities().flying)
        {
            AttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            boolean flag = player.getDeltaMovement().y <= 0.0D;
            if (flag && player.hasEffect(MobEffects.SLOW_FALLING)) {
                if (!gravity.hasModifier(SLOW_FALLING))
                    gravity.addTransientModifier(SLOW_FALLING);
                player.fallDistance = 0.0F;
            } else if (gravity.hasModifier(SLOW_FALLING))
                gravity.removeModifier(SLOW_FALLING);
            //player.setDeltaMovement(player.getDeltaMovement().add(0.0D, d0 / 4.0D, 0.0D));

            if (player.getDeltaMovement().y() < (input.jumping ? 0.46f : 0.4f))
                player.moveRelative(0.06f * (input.jumping ? 2f : 1.7f), new Vec3(0.0f, player.zza, -Math.min(0, player.zza)).normalize());
            if (player.getDeltaMovement().y() <= 0 && !input.shiftKeyDown)
                player.moveRelative(0.035f, new Vec3(0.0f, 1, 0.0f));

            if (input.shiftKeyDown)
                player.setDeltaMovement(player.getDeltaMovement().x, Math.max(0, player.getDeltaMovement().y()), player.getDeltaMovement().z);
        }


        if (player.isUsingItem())
        {
            ItemStack stack = player.getUseItem();
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof WeaponBaseItem)
                {
                    input.leftImpulse *= 5.0F;
                    //input = player.movementInput;
                    input.forwardImpulse *= 5.0F;
                    //input = player.movementInput;
                }
            }
        }

        if (PlayerCooldown.hasPlayerCooldown(player))
        {
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);

            if (cooldown.storedStack.getItem() instanceof RollerItem)
                input.jumping = false;

            if (!cooldown.canMove()) {
                input.forwardImpulse = 0;
                input.leftImpulse = 0;
                input.jumping = false;
            } else if (cooldown.storedStack.getItem() instanceof RollerItem) {
                input.forwardImpulse = Math.min(1, Math.abs(input.forwardImpulse)) * Math.signum(input.forwardImpulse) * ((RollerItem) cooldown.storedStack.getItem()).getSettings(cooldown.storedStack).swingMobility;
                input.leftImpulse = Math.min(1, Math.abs(input.leftImpulse)) * Math.signum(input.leftImpulse) * ((RollerItem) cooldown.storedStack.getItem()).getSettings(cooldown.storedStack).swingMobility;
            }
            if (cooldown.forceCrouch() && cooldown.getTime() > 1) {
                input.shiftKeyDown = !player.getAbilities().flying;
            }
        }
    }
}
