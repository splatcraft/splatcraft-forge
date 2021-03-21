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
        super(name, new Properties().group(SplatcraftItemGroups.GROUP_GENERAL).maxStackSize(1), 2);
    }

    public static RemoteResult scanTurf(World world, BlockPos blockpos, BlockPos blockpos1, int mode, ServerPlayerEntity target)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos1.getY(), blockpos.getY()), Math.min(blockpos.getZ(), blockpos1.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos1.getY(), blockpos.getY()), Math.max(blockpos.getZ(), blockpos1.getZ()));


        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
        {
            return createResult(false, new TranslationTextComponent("status.scan_turf.out_of_world"));
        }

        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslationTextComponent("status.scan_turf.out_of_world"));
                }
            }
        }

        if (world.isRemote)
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
                    int y = getTopSolidOrLiquidBlock(new BlockPos(x, 1, z), world, Math.min(blockpos3.getY() + 2, 255)).down().getY();


                    if (y > blockpos3.getY() || y < blockpos2.getY())
                    {
                        continue;
                    }

                    BlockPos checkPos = new BlockPos(x, y, z);
                    BlockState checkState = world.getBlockState(checkPos);


                    if (!InkBlockUtils.canInk(world, checkPos))
                    {
                        continue;
                    }

                    if (!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !InkBlockUtils.canInk(world, checkPos))
                    {
                        continue;
                    }

                    blockTotal++;

                    if (world.getTileEntity(checkPos) instanceof InkColorTileEntity && world.getBlockState(checkPos).getBlock() instanceof IColoredBlock)
                    {
                        InkColorTileEntity te = (InkColorTileEntity) world.getTileEntity(checkPos);
                        IColoredBlock block = (IColoredBlock) world.getBlockState(checkPos).getBlock();
                        int color = te.getColor();

                        if (block.countsTowardsTurf(world, checkPos))
                        {
                            if (scores.containsKey(color))
                            {
                                scores.replace(color, scores.get(color) + 1);
                            } else
                            {
                                scores.put(color, 1);
                            }
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
                        BlockState checkState = world.getBlockState(checkPos);
                        boolean isWall = false;

                        for (int j = 1; j <= 2; j++)
                        {
                            if (World.isOutsideBuildHeight(checkPos.up(j)))
                            {
                                break;
                            }
                            if (!InkBlockUtils.canInkPassthrough(world, checkPos.up(j)))
                            {
                                isWall = true;
                                break;
                            }

                            if (j > blockpos3.getY())
                            {
                                break;
                            }
                        }

                        if (isWall || !InkBlockUtils.canInk(world, checkPos))
                        {
                            continue;
                        }

                        if (!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !InkBlockUtils.canInk(world, checkPos))
                        {
                            continue;
                        }

                        blockTotal++;

                        if (world.getTileEntity(checkPos) instanceof InkColorTileEntity && world.getBlockState(checkPos).getBlock() instanceof IColoredBlock)
                        {
                            InkColorTileEntity te = (InkColorTileEntity) world.getTileEntity(checkPos);
                            IColoredBlock block = (IColoredBlock) world.getBlockState(checkPos).getBlock();
                            int color = te.getColor();

                            if (block.countsTowardsTurf(world, checkPos))
                            {
                                if (scores.containsKey(color))
                                {
                                    scores.replace(color, scores.get(color) + 1);
                                } else
                                {
                                    scores.put(color, 1);
                                }
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
            //world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentTranslation("commands.turfWar.score", SplatCraftUtils.getColorName(entry.getKey()), String.format("%.1f",(entry.getValue()/(float)blockTotal)*100)));
            colors[i] = entry.getKey();
            colorScores[i] = entry.getValue() / (float) blockTotal * 100;

            if (winnerScore < entry.getValue())
            {
                winner = entry.getKey();
                winnerScore = entry.getValue();
            }

            i++;
        }


        for (PlayerEntity player : world.getPlayers())
        {
            int color = ColorUtils.getPlayerColor(player);
            if (!ScoreboardHandler.hasColorCriterion(color))
            {
                continue;
            }

            ScoreCriteria criterion = color == winner ? ScoreboardHandler.getColorWins(color) : ScoreboardHandler.getColorLosses(color);
            world.getScoreboard().forAllObjectives(criterion, player.getScoreboardName(), score -> score.increaseScore(1));
        }

            /*
            if(SplatcraftScoreboardHandler.hasGoal(entry.getKey()))
            {
                Iterator<ScoreObjective> iter;
                if(entry.getKey() == winner)
                    iter = world.getScoreboard().getObjectivesFromCriteria(SplatcraftScoreboardHandler.getColorWins(entry.getKey())).iterator();
                else
                    iter = world.getScoreboard().getObjectivesFromCriteria(SplatcraftScoreboardHandler.getColorLosses(entry.getKey())).iterator();
                while(iter.hasNext())
                {
                    Iterator<Score> scoreIter = world.getScoreboard().getSortedScores(iter.next()).iterator();

                    while(scoreIter.hasNext())
                        scoreIter.next().increaseScore(1);

                }
            }
            */

        if (scores.isEmpty())
        {
            return createResult(false, new TranslationTextComponent("status.scan_turf.no_ink"));
        } else
        {
            SendScanTurfResultsPacket packet = new SendScanTurfResultsPacket(colors, colorScores);
            if (target == null)
            {
                SplatcraftPacketHandler.sendToDim(packet, world);
            } else
            {
                SplatcraftPacketHandler.sendToPlayer(packet, target);
            }

        }
        return createResult(true, null).setIntResults(winner, (int) ((float) affectedBlockTotal / blockTotal * 15));
    }

    private static BlockPos getTopSolidOrLiquidBlock(BlockPos pos, World world, int min)
    {
        Chunk chunk = world.getChunkAt(pos);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(pos.getX(), Math.min(chunk.getTopFilledSegment() + 16, min), pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.down();
            BlockState state = chunk.getBlockState(blockpos1);

            if (SplatcraftTags.Blocks.BLOCKS_TURF.contains(state.getBlock()) || !InkBlockUtils.canInkPassthrough(world, blockpos1) ||
                    state.getMaterial().blocksMovement())
            {
                break;
            }

        }

        return blockpos;
    }

    @Override
    public RemoteResult onRemoteUse(World world, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode)
    {
        return scanTurf(world, posA, posB, mode, null);
    }
}
