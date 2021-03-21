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
        {
            return;
        }

        IPlayerInfo playerInfo = PlayerInfoCapability.get(player);

        Hand activeHand = player.getActiveHand();
        HandSide handSide = player.getPrimaryHand();

        if (activeHand == null)
        {
            return;
        }

        ModelRenderer mainHand = activeHand == Hand.MAIN_HAND && handSide == HandSide.LEFT || activeHand == Hand.OFF_HAND && handSide == HandSide.RIGHT ? model.bipedLeftArm : model.bipedRightArm;
        ModelRenderer offHand = mainHand.equals(model.bipedLeftArm) ? model.bipedRightArm : model.bipedLeftArm;

        ItemStack mainStack = player.getHeldItem(activeHand);
        ItemStack offStack = player.getHeldItem(Hand.values()[(activeHand.ordinal() + 1) % Hand.values().length]);
        int useTime = player.getItemInUseCount();

        if (!(mainStack.getItem() instanceof WeaponBaseItem))
        {
            return;
        }

        if (useTime > 0 || (playerInfo != null && playerInfo.getPlayerCooldown() != null && playerInfo.getPlayerCooldown().getTime() > 0))
        {
            useTime = mainStack.getItem().getUseDuration(mainStack) - useTime;
            float animTime;
            float angle;

            switch (((WeaponBaseItem) mainStack.getItem()).getPose())
            {
                case DUAL_FIRE:
                    if (offStack.getItem() instanceof WeaponBaseItem && ((WeaponBaseItem) offStack.getItem()).getPose().equals(WeaponPose.DUAL_FIRE))
                    {
                        offHand.rotateAngleY = -0.1F + model.getModelHead().rotateAngleY;
                        offHand.rotateAngleX = -((float) Math.PI / 2F) + model.getModelHead().rotateAngleX;
                    }
                case FIRE:
                    mainHand.rotateAngleY = -0.1F + model.getModelHead().rotateAngleY;
                    mainHand.rotateAngleX = -((float) Math.PI / 2F) + model.getModelHead().rotateAngleX;
                    break;
                case BUCKET_SWING:
                    animTime = ((SlosherItem) mainStack.getItem()).startupTicks * 0.5f;
                    mainHand.rotateAngleY = 0;
                    mainHand.rotateAngleX = -0.36f;

                    angle = useTime / animTime + event.getPartialTicks();

                    if (angle < 6.5f)
                    {
                        mainHand.rotateAngleX = MathHelper.cos(angle * 0.6662F);
                    }
                    break;
                case BOW_CHARGE:
                    if (mainHand == model.bipedRightArm)
                    {
                        mainHand.rotateAngleY = -0.1F + model.getModelHead().rotateAngleY;
                        offHand.rotateAngleY = 0.1F + model.getModelHead().rotateAngleY + 0.4F;
                        mainHand.rotateAngleX = (-(float) Math.PI / 2F) + model.getModelHead().rotateAngleX;
                        offHand.rotateAngleX = (-(float) Math.PI / 2F) + model.getModelHead().rotateAngleX;
                    } else
                    {
                        offHand.rotateAngleY = -0.1F + model.getModelHead().rotateAngleY - 0.4F;
                        mainHand.rotateAngleY = 0.1F + model.getModelHead().rotateAngleY;
                        offHand.rotateAngleX = (-(float) Math.PI / 2F) + model.getModelHead().rotateAngleX;
                        mainHand.rotateAngleX = (-(float) Math.PI / 2F) + model.getModelHead().rotateAngleX;
                    }
                    break;
                case ROLL:
                    animTime = player.isOnGround() ? ((RollerItem) mainStack.getItem()).swingTime : ((RollerItem) mainStack.getItem()).flingTime;
                    mainHand.rotateAngleY = model.getModelHead().rotateAngleY;


                    if (PlayerCooldown.hasPlayerCooldown(player))
                    {
                        PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
                        angle = (float) ((cooldown.getMaxTime() - cooldown.getTime() + event.getPartialTicks()) / animTime * Math.PI / 2f) + ((float) Math.PI) / 1.8f;
                        mainHand.rotateAngleX = MathHelper.cos(angle) + (0.1F * 0.5F - ((float) Math.PI / 10F));//+ 0.36f;
                    } else
                    {
                        mainHand.rotateAngleX = 0.1F * 0.5F - ((float) Math.PI / 10F);
                    }
                    break;
                case BRUSH:
                    animTime = player.isOnGround() ? ((RollerItem) mainStack.getItem()).swingTime : ((RollerItem) mainStack.getItem()).flingTime;
                    mainHand.rotateAngleX = 0.1F * 0.5F - ((float) Math.PI / 10F);


                    if (PlayerCooldown.hasPlayerCooldown(player))
                    {
                        PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
                        angle = (float) ((cooldown.getMaxTime() - cooldown.getTime() + event.getPartialTicks()) / animTime * Math.PI / 2f) + ((float) Math.PI) / 1.8f;

                        mainHand.rotateAngleY = model.getModelHead().rotateAngleY + MathHelper.cos(angle);//+ 0.36f;
                    } else
                    {
                        mainHand.rotateAngleY = model.getModelHead().rotateAngleY;
                    }
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
