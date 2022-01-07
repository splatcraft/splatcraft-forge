package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.items.weapons.RollerItem;
import com.cibernet.splatcraft.items.weapons.SlosherItem;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerPosingHandler
{
    @SuppressWarnings("all")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void setupPlayerAngles(PlayerModelEvent.SetupAngles.Post event)
    {
        PlayerEntity player = event.getPlayer();
        PlayerModel<?> model = event.getModelPlayer();

        if (model == null || player == null || PlayerInfoCapability.isSquid(player))
            return;

        IPlayerInfo playerInfo = PlayerInfoCapability.get(player);

        Hand activeHand = player.getUsedItemHand();
        HandSide handSide = player.getMainArm();

        if (activeHand == null)
            return;

        ModelRenderer mainHand = activeHand == Hand.MAIN_HAND && handSide == HandSide.LEFT || activeHand == Hand.OFF_HAND && handSide == HandSide.RIGHT ? model.leftArm : model.rightArm;
        ModelRenderer offHand = mainHand.equals(model.leftArm) ? model.rightArm : model.leftArm;

        ItemStack mainStack = player.getItemInHand(activeHand);
        ItemStack offStack = player.getItemInHand(Hand.values()[(activeHand.ordinal() + 1) % Hand.values().length]);
        int useTime = player.getUseItemRemainingTicks();

        if (!(mainStack.getItem() instanceof WeaponBaseItem))
        {
            return;
        }

        if (useTime > 0 || (playerInfo != null && playerInfo.getPlayerCooldown() != null && playerInfo.getPlayerCooldown().getTime() > 0))
        {
            useTime = mainStack.getItem().getUseDuration(mainStack) - useTime;
            float animTime;
            float angle;

            PlayerCooldown cooldown;

            switch (((WeaponBaseItem) mainStack.getItem()).getPose())
            {
                case DUAL_FIRE:
                    if (offStack.getItem() instanceof WeaponBaseItem && ((WeaponBaseItem) offStack.getItem()).getPose().equals(WeaponPose.DUAL_FIRE))
                    {
                        offHand.yRot = -0.1F + model.getHead().yRot;
                        offHand.xRot = -((float) Math.PI / 2F) + model.getHead().xRot;
                    }
                case FIRE:
                    mainHand.yRot = -0.1F + model.getHead().yRot;
                    mainHand.xRot = -((float) Math.PI / 2F) + model.getHead().xRot;
                    break;
                case BUCKET_SWING:
                    animTime = ((SlosherItem) mainStack.getItem()).startupTicks;
                    mainHand.yRot = 0;
                    mainHand.xRot = -0.36f;

                    if(PlayerCooldown.hasPlayerCooldown(player))
                    {
                        cooldown = PlayerCooldown.getPlayerCooldown(player);
                        angle = (cooldown.getMaxTime() - cooldown.getTime() + event.getPartialTicks()) / animTime;
                        angle = (float) ((cooldown.getMaxTime() - cooldown.getTime() + event.getPartialTicks()) / animTime * Math.PI) + ((float) Math.PI) / 1.8f;
                        if (angle < 6.5f)
                            mainHand.xRot = MathHelper.cos(angle * 0.6662F);
                    }
                    break;
                case BOW_CHARGE:
                    if (mainHand == model.rightArm)
                    {
                        mainHand.yRot = -0.1F + model.getHead().yRot;
                        offHand.yRot = 0.1F + model.getHead().yRot + 0.4F;
                        mainHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                        offHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                    } else
                    {
                        offHand.yRot = -0.1F + model.getHead().yRot - 0.4F;
                        mainHand.yRot = 0.1F + model.getHead().yRot;
                        offHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                        mainHand.xRot = (-(float) Math.PI / 2F) + model.getHead().xRot;
                    }
                    break;
                case ROLL:
                    mainHand.yRot = model.getHead().yRot;


                    if (PlayerCooldown.hasPlayerCooldown(player))
                    {
                        cooldown = PlayerCooldown.getPlayerCooldown(player);
                        animTime = cooldown.isGrounded() ? ((RollerItem) mainStack.getItem()).swingTime : ((RollerItem) mainStack.getItem()).flingTime;
                        angle = (float) ((cooldown.getMaxTime() - cooldown.getTime() + event.getPartialTicks()) / animTime * Math.PI / 2f) + ((float) Math.PI) / 1.8f;
                        mainHand.xRot = MathHelper.cos(angle) + (0.1F * 0.5F - ((float) Math.PI / 10F));//+ 0.36f;
                    } else
                    {
                        mainHand.xRot = 0.1F * 0.5F - ((float) Math.PI / 10F);
                    }
                    break;
                case BRUSH:
                    mainHand.xRot = 0.1F * 0.5F - ((float) Math.PI / 10F);


                    if (PlayerCooldown.hasPlayerCooldown(player))
                    {
                        cooldown = PlayerCooldown.getPlayerCooldown(player);
                        animTime = cooldown.isGrounded() ? ((RollerItem) mainStack.getItem()).swingTime : ((RollerItem) mainStack.getItem()).flingTime;
                        angle = (float) -((cooldown.getMaxTime() - cooldown.getTime() + event.getPartialTicks()) / animTime * Math.PI / 2f) + ((float) Math.PI) / 1.8f;

                        mainHand.yRot = model.getHead().yRot + MathHelper.cos(angle);
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
        BUCKET_SWING
    }
}
