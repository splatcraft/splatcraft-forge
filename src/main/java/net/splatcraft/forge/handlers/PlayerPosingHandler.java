package net.splatcraft.forge.handlers;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.items.weapons.RollerItem;
import net.splatcraft.forge.items.weapons.SlosherItem;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.util.PlayerCooldown;

@Mod.EventBusSubscriber
public class PlayerPosingHandler
{

    @SuppressWarnings("all")
    @OnlyIn(Dist.CLIENT)
    public static void setupPlayerAngles(Player player, PlayerModel model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float deltaTicks)
    {

        if (model == null || player == null || !PlayerInfoCapability.hasCapability(player) || PlayerInfoCapability.isSquid(player))
            return;

        PlayerInfo playerInfo = PlayerInfoCapability.get(player);

        InteractionHand activeHand = player.getUsedItemHand();
        HumanoidArm handSide = player.getMainArm();

        if (activeHand == null)
            return;

        ModelPart mainHand = activeHand == InteractionHand.MAIN_HAND && handSide == HumanoidArm.LEFT || activeHand == InteractionHand.OFF_HAND && handSide == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;
        ModelPart offHand = mainHand.equals(model.leftArm) ? model.rightArm : model.leftArm;

        ItemStack mainStack = player.getItemInHand(activeHand);
        ItemStack offStack = player.getItemInHand(InteractionHand.values()[(activeHand.ordinal() + 1) % InteractionHand.values().length]);
        int useTime = player.getUseItemRemainingTicks();

        if (!(mainStack.getItem() instanceof WeaponBaseItem)) {
            return;
        }

        if (useTime > 0 || player.getCooldowns().isOnCooldown(mainStack.getItem())
                || (playerInfo != null && playerInfo.getPlayerCooldown() != null && playerInfo.getPlayerCooldown().getTime() > 0)) {
            useTime = mainStack.getItem().getUseDuration(mainStack) - useTime;
            float animTime;
            float angle;

            PlayerCooldown cooldown;

            switch (((WeaponBaseItem) mainStack.getItem()).getPose(mainStack)) {
                case DUAL_FIRE:
                    if (offStack.getItem() instanceof WeaponBaseItem && ((WeaponBaseItem) offStack.getItem()).getPose(offStack).equals(WeaponPose.DUAL_FIRE)) {
                        offHand.yRot = -0.1F + model.getHead().yRot;
                        offHand.xRot = -((float) Math.PI / 2F) + model.getHead().xRot;
                    }
                case FIRE:
                    mainHand.yRot = -0.1F + model.getHead().yRot;
                    mainHand.xRot = -((float) Math.PI / 2F) + model.getHead().xRot;
                    break;
                case SUB_HOLD:
                    if (!(mainStack.getItem() instanceof SubWeaponItem) || useTime < ((SubWeaponItem) mainStack.getItem()).getSettings(mainStack).holdTime) {
                        mainHand.yRot = -0.1F + model.getHead().yRot;
                        mainHand.xRot = ((float) Math.PI / 8F);
                        mainHand.zRot = ((float) Math.PI / 6F) * (mainHand == model.leftArm ? -1 : 1);
                    }
                    break;
                case BUCKET_SWING:
                    animTime = ((SlosherItem) mainStack.getItem()).getSettings(mainStack).startupTicks;
                    mainHand.yRot = 0;
                    mainHand.xRot = -0.36f;

                    if (PlayerCooldown.hasPlayerCooldown(player)) {
                        cooldown = PlayerCooldown.getPlayerCooldown(player);
                        angle = (cooldown.getMaxTime() - cooldown.getTime() + deltaTicks) / animTime;
                        angle = (float) ((cooldown.getMaxTime() - cooldown.getTime() + deltaTicks) / animTime * Math.PI) + ((float) Math.PI) / 1.8f;
                        if (angle < 6.5f)
                            mainHand.xRot = Mth.cos(angle * 0.6662F);
                    }
                    break;
                case BOW_CHARGE:
                    if (mainHand == model.rightArm) {
                        mainHand.yRot = -0.1F + model.getHead().yRot;
                        offHand.yRot = 0.1F + model.getHead().yRot + 0.4F;
                        mainHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                        offHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                    } else {
                        offHand.yRot = -0.1F + model.getHead().yRot - 0.4F;
                        mainHand.yRot = 0.1F + model.getHead().yRot;
                        offHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                        mainHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                    }
                    break;
                case ROLL:
                    mainHand.yRot = model.getHead().yRot;


                    if (PlayerCooldown.hasPlayerCooldown(player)) {
                        cooldown = PlayerCooldown.getPlayerCooldown(player);
                        animTime = cooldown.isGrounded() ? ((RollerItem) mainStack.getItem()).getSettings(mainStack).swingTime : ((RollerItem) mainStack.getItem()).getSettings(mainStack).flingTime;
                        angle = (float) ((cooldown.getMaxTime() - cooldown.getTime() + deltaTicks) / animTime * Math.PI / 2f) + ((float) Math.PI) / 1.8f;
                        mainHand.xRot = Mth.cos(angle) + (0.1F * 0.5F - ((float) Math.PI / 10F));//+ 0.36f;
                    } else {
                        mainHand.xRot = 0.1F * 0.5F - ((float) Math.PI / 10F);
                    }
                    break;
                case BRUSH:
                    mainHand.xRot = 0.1F * 0.5F - ((float) Math.PI / 10F);


                    if (PlayerCooldown.hasPlayerCooldown(player)) {
                        cooldown = PlayerCooldown.getPlayerCooldown(player);
                        animTime = cooldown.isGrounded() ? ((RollerItem) mainStack.getItem()).getSettings(mainStack).swingTime : ((RollerItem) mainStack.getItem()).getSettings(mainStack).flingTime;
                        angle = (float) -((cooldown.getMaxTime() - cooldown.getTime() + deltaTicks) / animTime * Math.PI / 2f) + ((float) Math.PI) / 1.8f;

                        mainHand.yRot = model.getHead().yRot + Mth.cos(angle);
                    } else mainHand.yRot = model.getHead().yRot;
                    break;
            }
        }
    }

    public enum WeaponPose
    {
        NONE,
        FIRE,
        DUAL_FIRE,
        ROLL,
        BRUSH,
        BOW_CHARGE,
        BUCKET_SWING,
        SUB_HOLD
    }


}
