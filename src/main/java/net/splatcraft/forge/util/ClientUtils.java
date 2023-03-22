package net.splatcraft.forge.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.registries.SplatcraftGameRules;

import java.util.HashMap;
import java.util.TreeMap;

public class ClientUtils
{
    @OnlyIn(Dist.CLIENT)
    protected static final TreeMap<String, Integer> clientColors = new TreeMap<>();
    @OnlyIn(Dist.CLIENT)
    public static final HashMap<String, Stage> clientStages = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void resetClientColors()
    {
        clientColors.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static int getClientPlayerColor(String player)
    {
        return clientColors.getOrDefault(player, -1);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setClientPlayerColor(String player, int color)
    {
        clientColors.put(player, color);
    }

    @OnlyIn(Dist.CLIENT)
    public static void putClientColors(TreeMap<String, Integer> map)
    {
        clientColors.putAll(map);
    }

    public static PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static boolean showDurabilityBar(ItemStack stack)
    {
        return (SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.DURABILITY)) &&
                getClientPlayer().getItemInHand(Hand.MAIN_HAND).equals(stack) && getDurabilityForDisplay(stack) > 0;
    }

    public static double getDurabilityForDisplay(ItemStack stack)
    {
        PlayerEntity player = getClientPlayer();

        if (!SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.REQUIRE_INK_TANK))
        {
            return 0;
        }

        ItemStack chestpiece = player.getItemBySlot(EquipmentSlotType.CHEST);
        if (chestpiece.getItem() instanceof InkTankItem)
        {
            return 1 - InkTankItem.getInkAmount(chestpiece) / ((InkTankItem) chestpiece.getItem()).capacity;
        }
        return 1;
    }

    public static boolean canPerformRoll(PlayerEntity entity)
    {
        MovementInput input = ((ClientPlayerEntity) entity).input;

        return !PlayerCooldown.hasPlayerCooldown(entity) && input.jumping && (input.leftImpulse != 0 || input.forwardImpulse != 0);
    }

    public static Vector3d getDodgeRollVector(PlayerEntity entity)
    {
        MovementInput input = ((ClientPlayerEntity) entity).input;
        return new Vector3d(input.leftImpulse, -0.4f, input.forwardImpulse);
    }
}
