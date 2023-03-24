package net.splatcraft.forge.items.remotes;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
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
    public ColorChangerItem(String name)
    {
        super(name, new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1).rarity(Rarity.UNCOMMON), 3);
        SplatcraftItems.inkColoredItems.add(this);
    }

    public static RemoteResult replaceColor(World level, BlockPos from, BlockPos to, int color, int mode, int affectedColor, String stage, String affectedTeam)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(to.getY(), from.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(to.getY(), from.getY()), Math.max(from.getZ(), to.getZ()));

        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
        {
            return createResult(false, new TranslationTextComponent("status.change_color.out_of_level"));
        }


        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!level.isLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslationTextComponent("status.change_color.out_of_level"));
                }
            }
        }
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
                    TileEntity tileEntity = level.getBlockEntity(pos);
                    if (block instanceof IColoredBlock)
                    {
                        int teColor = ((IColoredBlock) block).getColor(level, pos);

                        if (((IColoredBlock) block).canRemoteColorChange(level, pos, teColor, color) && (mode == 0 || (mode == 1) == (affectedTeam.isEmpty() ? teColor == affectedColor : ((InkColorTileEntity) tileEntity).getTeam().equals(affectedTeam)))
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

        return createResult(true, new TranslationTextComponent("status.change_color.success", count, level.isClientSide ? ColorUtils.getFormatedColorName(color, false) : InkColorCommand.getColorName(color))).setIntResults(count, count * 15 / blockTotal);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundNBT nbt = stack.getOrCreateTag();

        if(nbt.contains("Team") && !nbt.getString("Team").isEmpty())
        {
            int color = -1;

            if(nbt.contains("Stage") && ClientUtils.clientStages.containsKey(nbt.getString("Stage")))
                color = ClientUtils.clientStages.get(nbt.getString("Stage")).getTeamColor(nbt.getString("Team"));
            tooltip.add(TextComponentUtils.mergeStyles(new StringTextComponent(nbt.getString("Team")), color <= -1 ? TARGETS_STYLE : TARGETS_STYLE.withColor(Color.fromRgb(color))));
        }


        if (ColorUtils.isColorLocked(stack))
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World level, @NotNull Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity)
                && PlayerInfoCapability.hasCapability((LivingEntity) entity))
        {
            ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
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
    public RemoteResult onRemoteUse(World usedOnWorld, BlockPos from, BlockPos to, ItemStack stack, int colorIn, int mode, Collection<ServerPlayerEntity> targets)
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
