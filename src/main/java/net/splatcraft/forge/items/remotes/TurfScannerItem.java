package net.splatcraft.forge.items.remotes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.SendScanTurfResultsPacket;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftStats;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class TurfScannerItem extends RemoteItem
{
    public TurfScannerItem()
    {
        super(new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1), 2);
    }

    public static RemoteResult scanTurf(Level level, Level outputWorld, BlockPos blockpos, BlockPos blockpos1, int mode, Collection<ServerPlayer> targets)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos1.getY(), blockpos.getY()), Math.min(blockpos.getZ(), blockpos1.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos1.getY(), blockpos.getY()), Math.max(blockpos.getZ(), blockpos1.getZ()));


        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
        {
            return createResult(false, new TranslatableComponent("status.scan_turf.out_of_world"));
        }

        /*
        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!level.isLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslatableComponent("status.scan_turf.out_of_world"));
                }
            }
        }
        */
        if (level.isClientSide)
        {
            return createResult(true, null);
        }
        TreeMap<Integer, Integer> scores = new TreeMap<>();
        int blockTotal = 0;
        int affectedBlockTotal = 0;

        if (mode == 0)
        {
            for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
            {
                for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
                {
                    int y = getTopSolidOrLiquidBlock(new BlockPos(x, 1, z), level, Math.min(blockpos3.getY() + 2, 255)).below().getY();

                    if (y > blockpos3.getY() || y < blockpos2.getY())
                        continue;

                    BlockPos checkPos = new BlockPos(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (InkBlockUtils.isUninkable(level, checkPos))
                        continue;

                    if (!checkState.getMaterial().blocksMotion() || checkState.getMaterial().isLiquid() || InkBlockUtils.isUninkable(level, checkPos))
                        continue;

                    blockTotal++;

                    if (level.getBlockEntity(checkPos) instanceof InkColorTileEntity && level.getBlockState(checkPos).getBlock() instanceof IColoredBlock)
                    {
                        InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(checkPos);
                        IColoredBlock block = (IColoredBlock) level.getBlockState(checkPos).getBlock();
                        int color = te.getColor();

                        if (color >= 0 && level.getBlockState(checkPos).is(SplatcraftTags.Blocks.SCAN_TURF_SCORED))
                        {
                            if (scores.containsKey(color))
                                scores.replace(color, scores.get(color) + 1);
                            else scores.put(color, 1);
                            affectedBlockTotal++;
                        }


                    }
                }
            }
        } else if (mode == 1)
        {
            for (int x = blockpos2.getX(); x <= blockpos3.getX(); x++)
            {
                for (int z = blockpos2.getZ(); z <= blockpos3.getZ(); z++)
                {
                    for (int y = blockpos2.getY(); y <= blockpos3.getY(); y++)
                    {
                        BlockPos checkPos = new BlockPos(x, y, z);
                        BlockState checkState = level.getBlockState(checkPos);
                        boolean isWall = false;

                        for (int j = 1; j <= 2; j++)
                        {
                            if (level.isOutsideBuildHeight(checkPos.above(j)))
                                break;
                            if (!InkBlockUtils.canInkPassthrough(level, checkPos.above(j)))
                            {
                                isWall = true;
                                break;
                            }

                            if (j > blockpos3.getY())
                                break;
                        }

                        if (isWall || InkBlockUtils.isUninkable(level, checkPos))
                            continue;

                        if (!checkState.getMaterial().blocksMotion() || checkState.getMaterial().isLiquid() || InkBlockUtils.isUninkable(level, checkPos))
                            continue;

                        blockTotal++;

                        if (level.getBlockEntity(checkPos) instanceof InkColorTileEntity && level.getBlockState(checkPos).getBlock() instanceof IColoredBlock)
                        {
                            InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(checkPos);
                            int color = te.getColor();

                            if (color >= 0 && level.getBlockState(checkPos).is(SplatcraftTags.Blocks.SCAN_TURF_SCORED))
                            {
                                if (scores.containsKey(color))
                                    scores.replace(color, scores.get(color) + 1);
                                else scores.put(color, 1);
                            }


                        }
                    }
                }
            }
        }

        Integer[] colors = new Integer[scores.size()];
        Float[] colorScores = new Float[scores.size()];

        int winner = -1;
        float winnerScore = -1;
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : scores.entrySet())
        {
            colors[i] = entry.getKey();
            colorScores[i] = entry.getValue() / (float) blockTotal * 100;

            if (winnerScore < entry.getValue())
            {
                winner = entry.getKey();
                winnerScore = entry.getValue();
            }

            i++;
        }


        for (Player player : targets == ALL_TARGETS ? outputWorld.players() : targets)
        {
            int color = ColorUtils.getPlayerColor(player);

            if(color == winner)
                player.awardStat(SplatcraftStats.TURF_WARS_WON);

            ScoreboardHandler.updatePlayerScore(ScoreboardHandler.TURF_WAR_SCORE, player, scores.getOrDefault(color, 0));

            if (!ScoreboardHandler.hasColorCriterion(color))
                continue;

            ObjectiveCriteria criterion = color == winner ? ScoreboardHandler.getColorWins(color) : ScoreboardHandler.getColorLosses(color);
            outputWorld.getScoreboard().forAllObjectives(criterion, player.getScoreboardName(), score -> score.add(1));
        }


        if (scores.isEmpty())
        {
            return createResult(false, new TranslatableComponent("status.scan_turf.no_ink"));
        } else
        {
            SendScanTurfResultsPacket packet = new SendScanTurfResultsPacket(colors, colorScores);
            if (targets == ALL_TARGETS)
                SplatcraftPacketHandler.sendToDim(packet, outputWorld.dimension());
            else for(ServerPlayer target : targets)
                SplatcraftPacketHandler.sendToPlayer(packet, target);

        }

        return createResult(true, new TranslatableComponent("commands.scanturf.success", blockTotal)).setIntResults(winner, (int) ((float) affectedBlockTotal / blockTotal * 15));
    }

    private static BlockPos getTopSolidOrLiquidBlock(BlockPos pos, Level level, int min)
    {
        LevelChunk chunk = level.getChunkAt(pos);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(pos.getX(), Math.min(chunk.getHighestSectionPosition() + 16, min), pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.below();
            BlockState state = chunk.getBlockState(blockpos1);

            if (state.is(SplatcraftTags.Blocks.SCAN_TURF_IGNORED) || !InkBlockUtils.canInkPassthrough(level, blockpos1) ||
                    state.getMaterial().blocksMotion())
            {
                break;
            }

        }

        return blockpos;
    }

    @Override
    public RemoteResult onRemoteUse(Level usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode, Collection<ServerPlayer> targets)
    {
        return scanTurf(getLevel(usedOnWorld, stack), usedOnWorld, posA, posB, mode, targets);
    }
}
