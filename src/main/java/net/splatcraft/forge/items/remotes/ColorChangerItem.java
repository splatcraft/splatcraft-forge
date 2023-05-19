package net.splatcraft.forge.items.remotes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.commands.InkColorCommand;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.items.IColoredItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateStageListPacket;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.IHasTeam;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ColorChangerItem extends RemoteItem implements IColoredItem
{
    public ColorChangerItem()
    {
        super(new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1).rarity(Rarity.UNCOMMON), 3);
        SplatcraftItems.inkColoredItems.add(this);
    }

    public static RemoteResult replaceColor(Level level, BlockPos from, BlockPos to, int color, int mode, int affectedColor, String stage, String affectedTeam)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(to.getY(), from.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(to.getY(), from.getY()), Math.max(from.getZ(), to.getZ()));

        if (!level.isInWorldBounds(blockpos2) || !level.isInWorldBounds(blockpos3))
            return createResult(false, new TranslatableComponent("status.change_color.out_of_world"));

        /*
        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!level.isLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslatableComponent("status.change_color.out_of_world"));
                }
            }
        }
        */
        int count = 0;
        int blockTotal = 0;
        for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
        {
            for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
            {
                for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
                {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = level.getBlockState(pos).getBlock();
                    BlockEntity tileEntity = level.getBlockEntity(pos);
                    if (block instanceof IColoredBlock)
                    {
                        int teColor = ((IColoredBlock) block).getColor(level, pos);
                        if (((IColoredBlock) block).canRemoteColorChange(level, pos, teColor, color) && (mode == 0 || (mode == 1) == (affectedTeam.isEmpty() ? teColor == affectedColor :
                                tileEntity instanceof IHasTeam && ((IHasTeam) tileEntity).getTeam().equals(affectedTeam)))
                                && ((IColoredBlock) block).remoteColorChange(level, pos, color))
                        {
                            count++;
                        }
                    }
                    blockTotal++;
                }
            }
        }

        if(mode <= 1 && !affectedTeam.isEmpty() && !stage.isEmpty())
        {
            HashMap<String, Stage> stages = (level.isClientSide() ? ClientUtils.clientStages : SaveInfoCapability.get(level.getServer()).getStages());
            stages.get(stage).setTeamColor(affectedTeam, color);
            if(!level.isClientSide())
                SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));
        }

        return createResult(true, new TranslatableComponent("status.change_color.success", count, level.isClientSide ? ColorUtils.getFormatedColorName(color, false) : InkColorCommand.getColorName(color))).setIntResults(count, count * 15 / blockTotal);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag nbt = stack.getOrCreateTag();

        if(nbt.contains("Team") && !nbt.getString("Team").isEmpty())
        {
            int color = -1;

            if(nbt.contains("Stage") && ClientUtils.clientStages.containsKey(nbt.getString("Stage")))
                color = ClientUtils.clientStages.get(nbt.getString("Stage")).getTeamColor(nbt.getString("Team"));
            tooltip.add(ComponentUtils.mergeStyles(new TextComponent(nbt.getString("Team")), color <= -1 ? TARGETS_STYLE : TARGETS_STYLE.withColor(TextColor.fromRgb(color))));
        }


        if (ColorUtils.isColorLocked(stack))
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof Player && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((Player) entity)
                && PlayerInfoCapability.hasCapability((LivingEntity) entity))
        {
            ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((Player) entity));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition().below();

        if (entity.level.getBlockState(pos).getBlock() instanceof InkwellBlock)
        {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
            {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }

        return false;
    }

    @Override
    public RemoteResult onRemoteUse(Level usedOnWorld, BlockPos from, BlockPos to, ItemStack stack, int colorIn, int mode, Collection<ServerPlayer> targets)
    {
        String stage = "";
        String team = "";

        if(stack.hasTag())
        {
            team = stack.getTag().getString("Team");
            stage = stack.getTag().getString("Stage");
        }

        return replaceColor(getLevel(usedOnWorld, stack), from, to, ColorUtils.getInkColor(stack), mode, colorIn, stage, team);
    }
}
