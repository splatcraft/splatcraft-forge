package com.cibernet.splatcraft.items.remotes;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import com.cibernet.splatcraft.network.SendScanTurfResultsPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.TreeMap;

public class TurfScannerItem extends RemoteItem
{
    public TurfScannerItem(String name)
    {
        super(name, new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1), 2);
    }

    public static RemoteResult scanTurf(World level, World outputWorld, BlockPos blockpos, BlockPos blockpos1, int mode, ServerPlayerEntity target)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos1.getY(), blockpos.getY()), Math.min(blockpos.getZ(), blockpos1.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos1.getY(), blockpos.getY()), Math.max(blockpos.getZ(), blockpos1.getZ()));


        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
        {
            return createResult(false, new TranslationTextComponent("status.scan_turf.out_of_level"));
        }

        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!level.isLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslationTextComponent("status.scan_turf.out_of_level"));
                }
            }
        }

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

                    if (!InkBlockUtils.canInk(level, checkPos))
                        continue;

                    if (!checkState.getMaterial().blocksMotion() || checkState.getMaterial().isLiquid() || !InkBlockUtils.canInk(level, checkPos))
                        continue;

                    blockTotal++;

                    if (level.getBlockEntity(checkPos) instanceof InkColorTileEntity && level.getBlockState(checkPos).getBlock() instanceof IColoredBlock)
                    {
                        InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(checkPos);
                        IColoredBlock block = (IColoredBlock) level.getBlockState(checkPos).getBlock();
                        int color = te.getColor();

                        if (color >= 0 && SplatcraftTags.Blocks.SCAN_TURF_SCORED.contains(level.getBlockState(checkPos).getBlock()))
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
                            if (World.isOutsideBuildHeight(checkPos.above(j)))
                                break;
                            if (!InkBlockUtils.canInkPassthrough(level, checkPos.above(j)))
                            {
                                isWall = true;
                                break;
                            }

                            if (j > blockpos3.getY())
                                break;
                        }

                        if (isWall || !InkBlockUtils.canInk(level, checkPos))
                            continue;

                        if (!checkState.getMaterial().blocksMotion() || checkState.getMaterial().isLiquid() || !InkBlockUtils.canInk(level, checkPos))
                            continue;

                        blockTotal++;

                        if (level.getBlockEntity(checkPos) instanceof InkColorTileEntity && level.getBlockState(checkPos).getBlock() instanceof IColoredBlock)
                        {
                            InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(checkPos);
                            IColoredBlock block = (IColoredBlock) level.getBlockState(checkPos).getBlock();
                            int color = te.getColor();

                            if (color >= 0 && SplatcraftTags.Blocks.SCAN_TURF_SCORED.contains(level.getBlockState(checkPos).getBlock()))
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


        for (PlayerEntity player : outputWorld.players())
        {
            int color = ColorUtils.getPlayerColor(player);
            if (!ScoreboardHandler.hasColorCriterion(color))
                continue;

            ScoreCriteria criterion = color == winner ? ScoreboardHandler.getColorWins(color) : ScoreboardHandler.getColorLosses(color);
            outputWorld.getScoreboard().forAllObjectives(criterion, player.getScoreboardName(), score -> score.add(1));
        }


        if (scores.isEmpty())
        {
            return createResult(false, new TranslationTextComponent("status.scan_turf.no_ink"));
        } else
        {
            SendScanTurfResultsPacket packet = new SendScanTurfResultsPacket(colors, colorScores);
            if (target == null)
                SplatcraftPacketHandler.sendToDim(packet, outputWorld);
            else SplatcraftPacketHandler.sendToPlayer(packet, target);

        }
        return createResult(true, null).setIntResults(winner, (int) ((float) affectedBlockTotal / blockTotal * 15));
    }

    private static BlockPos getTopSolidOrLiquidBlock(BlockPos pos, World level, int min)
    {
        Chunk chunk = level.getChunkAt(pos);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(pos.getX(), Math.min(chunk.getHighestSectionPosition() + 16, min), pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.below();
            BlockState state = chunk.getBlockState(blockpos1);

            if (SplatcraftTags.Blocks.SCAN_TURF_IGNORED.contains(state.getBlock()) || !InkBlockUtils.canInkPassthrough(level, blockpos1) ||
                    state.getMaterial().blocksMotion())
            {
                break;
            }

        }

        return blockpos;
    }

    @Override
    public RemoteResult onRemoteUse(World usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode)
    {
        return scanTurf(getLevel(usedOnWorld, stack), usedOnWorld, posA, posB, mode, null);
    }
}
