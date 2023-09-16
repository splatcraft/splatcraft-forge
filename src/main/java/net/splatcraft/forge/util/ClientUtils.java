package net.splatcraft.forge.util;

import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
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

    public static Player getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static boolean showDurabilityBar(ItemStack stack)
    {
        return (SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.DURABILITY)) &&
                getClientPlayer().getItemInHand(InteractionHand.MAIN_HAND).equals(stack) && getDurabilityForDisplay(stack) > 0;
    }

    public static double getDurabilityForDisplay(ItemStack stack)
    {
        Player player = getClientPlayer();

        if (!SplatcraftGameRules.getLocalizedRule(player.level, player.blockPosition(), SplatcraftGameRules.REQUIRE_INK_TANK))
        {
            return 0;
        }

        ItemStack chestpiece = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestpiece.getItem() instanceof InkTankItem)
        {
            return InkTankItem.getInkAmount(chestpiece) / ((InkTankItem) chestpiece.getItem()).capacity;
        }
        return 1;
    }

    public static boolean canPerformRoll(Player entity)
    {
        Input input = ((LocalPlayer) entity).input;

        return !PlayerCooldown.hasPlayerCooldown(entity) && input.jumping && (input.leftImpulse != 0 || input.forwardImpulse != 0);
    }

    public static Vec3 getDodgeRollVector(Player entity) {
        Input input = ((LocalPlayer) entity).input;
        return new Vec3(input.leftImpulse, -0.4f, input.forwardImpulse);
    }

    public static boolean shouldRenderSide(BlockEntity te, Direction direction) {
        if (te.getLevel() == null)
            return false;

        BlockPos tePos = te.getBlockPos();

        Vector3f lookVec = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
        Vec3 blockVec = Vec3.atBottomCenterOf(tePos).add(lookVec.x(), lookVec.y(), lookVec.z());

        Vec3 directionVec3d = blockVec.subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()).normalize();
        Vector3f directionVec = new Vector3f((float) directionVec3d.x, (float) directionVec3d.y, (float) directionVec3d.z);
        if (lookVec.dot(directionVec) > 0) {
            if (direction == null) return true;
            BlockState relative = te.getLevel().getBlockState(tePos.relative(direction));
            return relative.getMaterial().equals(Material.BARRIER) || !relative.getMaterial().isSolidBlocking() || !relative.isCollisionShapeFullBlock(te.getLevel(), tePos.relative(direction));
        }

        return false;
    }
}
