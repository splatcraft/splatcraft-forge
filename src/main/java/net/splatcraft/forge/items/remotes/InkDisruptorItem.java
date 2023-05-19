package net.splatcraft.forge.items.remotes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.registries.SplatcraftItemGroups;

import java.util.Collection;

public class InkDisruptorItem extends RemoteItem
{
    public InkDisruptorItem()
    {
        super(new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL).stacksTo(1));
    }

    @Override
    public RemoteResult onRemoteUse(Level usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode, Collection<ServerPlayer> targets)
    {
        return clearInk(getLevel(usedOnWorld, stack), posA, posB);
    }

    public static RemoteResult clearInk(Level level, BlockPos posA, BlockPos posB)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(posA.getX(), posB.getX()), Math.min(posB.getY(), posA.getY()), Math.min(posA.getZ(), posB.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(posA.getX(), posB.getX()), Math.max(posB.getY(), posA.getY()), Math.max(posA.getZ(), posB.getZ()));

        if (!level.isInWorldBounds(blockpos2) || !level.isInWorldBounds(blockpos3))
            return createResult(false, new TranslatableComponent("status.clear_ink.out_of_world"));

        /*
        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!level.isLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslatableComponent("status.clear_ink.out_of_world"));
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
                    if (block instanceof IColoredBlock)
                    {
                        if (((IColoredBlock) block).remoteInkClear(level, pos))
                        {
                            count++;
                        }
                    }
                    blockTotal++;
                }
            }
        }

        return createResult(true, new TranslatableComponent("status.clear_ink." + (count > 0 ? "success" : "no_ink"), count)).setIntResults(count, count * 15 / blockTotal);
    }
}
