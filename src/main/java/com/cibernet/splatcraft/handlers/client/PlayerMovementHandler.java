package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.weapons.RollerItem;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerMovementHandler
{
    private static final AttributeModifier INK_SWIM_SPEED = new AttributeModifier("Ink swimming speed boost", 0D, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SQUID_SWIM_SPEED = new AttributeModifier("Squid swim speed boost", 0.3D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final AttributeModifier ENEMY_INK_SPEED = new AttributeModifier("Enemy ink speed penalty", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final AttributeModifier SLOW_FALLING = new AttributeModifier(UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA"), "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void playerMovement(TickEvent.PlayerTickEvent event)
    {
        if (!(event.player instanceof ClientPlayerEntity) || event.phase != TickEvent.Phase.END)
            return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        //MovementInput input = player.movementInput;
        ModifiableAttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        ModifiableAttributeInstance swimAttribute = player.getAttribute(ForgeMod.SWIM_SPEED.get());

        if (speedAttribute.hasModifier(INK_SWIM_SPEED) && player.isOnGround())
            speedAttribute.removeModifier(INK_SWIM_SPEED);
        if (speedAttribute.hasModifier(ENEMY_INK_SPEED))
            speedAttribute.removeModifier(ENEMY_INK_SPEED);
        if (swimAttribute.hasModifier(SQUID_SWIM_SPEED))
            swimAttribute.removeModifier(SQUID_SWIM_SPEED);

        if (speedAttribute.getModifier(SplatcraftItems.SPEED_MOD_UUID) != null)
            speedAttribute.removeModifier(SplatcraftItems.SPEED_MOD_UUID);

        if (InkBlockUtils.onEnemyInk(player))
        {
            //player.setMotion(player.getMotion().x, Math.min(player.getMotion().y, 0.05f), player.getMotion().z);
            if (!speedAttribute.hasModifier(ENEMY_INK_SPEED))
                speedAttribute.applyNonPersistentModifier(ENEMY_INK_SPEED);
        }

        if (player.getActiveItemStack().getItem() instanceof WeaponBaseItem && ((WeaponBaseItem) player.getActiveItemStack().getItem()).hasSpeedModifier(player, player.getActiveItemStack()))
        {
            AttributeModifier mod = ((WeaponBaseItem) player.getActiveItemStack().getItem()).getSpeedModifier(player, player.getActiveItemStack());
            if (!speedAttribute.hasModifier(mod))
                speedAttribute.applyNonPersistentModifier(mod);
        }

        if (PlayerInfoCapability.isSquid(player))
        {
            if (InkBlockUtils.canSquidSwim(player) && !speedAttribute.hasModifier(INK_SWIM_SPEED))
                speedAttribute.applyNonPersistentModifier(INK_SWIM_SPEED);
            if (!swimAttribute.hasModifier(SQUID_SWIM_SPEED))
                swimAttribute.applyNonPersistentModifier(SQUID_SWIM_SPEED);
        }

        if (PlayerCooldown.hasPlayerCooldown(player))
        {
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
            player.inventory.currentItem = cooldown.getSlotIndex();
        }

        if (!player.abilities.isFlying)
        {
            if (speedAttribute.hasModifier(INK_SWIM_SPEED))
                player.moveRelative((float) player.getAttributeValue(SplatcraftItems.INK_SWIM_SPEED) * (player.isOnGround() ? 1 : 0.75f), new Vector3d(player.moveStrafing, 0.0f, player.moveForward));

        }

    }

    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event)
    {

        MovementInput input = event.getMovementInput();
        PlayerEntity player = event.getPlayer();

        float speedMod = !input.sneaking ? InkBlockUtils.canSquidHide(player) ? 35f : 2f : 1f;

        input.moveForward *= speedMod;
        //input = player.movementInput;
        input.moveStrafe *= speedMod;
        //input = player.movementInput;

        if (PlayerInfoCapability.isSquid(player) && InkBlockUtils.canSquidClimb(player) && !player.abilities.isFlying)
        {
            ModifiableAttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            boolean flag = player.getMotion().y <= 0.0D;
            if (flag && player.isPotionActive(Effects.SLOW_FALLING))
            {
                if (!gravity.hasModifier(SLOW_FALLING))
                    gravity.applyNonPersistentModifier(SLOW_FALLING);
                player.fallDistance = 0.0F;
            } else if (gravity.hasModifier(SLOW_FALLING))
                gravity.removeModifier(SLOW_FALLING);
            //player.setMotion(player.getMotion().add(0.0D, d0 / 4.0D, 0.0D));

            //if((player.isOnGround() && player.world.getCollisionShapes(player, player.getBoundingBox().offset(xOff, (double)(player.stepHeight), zOff)).toArray().length == 0) || !player.isOnGround())
            {
                if (player.getMotion().getY() < (input.jump ? 0.46f : 0.4f))
                    player.moveRelative(0.055f * (input.jump ? 1.9f : 1.7f), new Vector3d(0.0f, player.moveForward, -Math.min(0, player.moveForward)));
                if (player.getMotion().getY() <= 0 && !input.sneaking)
                    player.moveRelative(0.035f, new Vector3d(0.0f, 1, 0.0f));

                if (input.sneaking)
                    player.setMotion(player.getMotion().x, Math.max(0, player.getMotion().getY()), player.getMotion().z);
            }
        }


        if (player.isHandActive())
        {
            ItemStack stack = player.getActiveItemStack();
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof WeaponBaseItem)
                {
                    input.moveStrafe *= 5.0F;
                    //input = player.movementInput;
                    input.moveForward *= 5.0F;
                    //input = player.movementInput;
                }
            }
        }

        if (PlayerCooldown.hasPlayerCooldown(player))
        {
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);

            if(cooldown.storedItem instanceof RollerItem)
                input.jump = false;

            if (!cooldown.canMove())
            {
                input.moveForward = 0;
                input.moveStrafe = 0;
                input.jump = false;
            } else if (cooldown.storedItem instanceof RollerItem)
            {
                input.moveForward = Math.min(1, Math.abs(input.moveForward)) * Math.signum(input.moveForward) * (float) ((RollerItem) cooldown.storedItem).swingMobility;
                input.moveStrafe = Math.min(1, Math.abs(input.moveStrafe)) * Math.signum(input.moveStrafe) * (float) ((RollerItem) cooldown.storedItem).swingMobility;
            }
            if (cooldown.forceCrouch() && cooldown.getTime() > 1)
            {
                input.sneaking = !player.abilities.isFlying;
            }

        }

    }
}
