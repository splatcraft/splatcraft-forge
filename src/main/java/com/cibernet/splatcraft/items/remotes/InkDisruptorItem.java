package com.cibernet.splatcraft.items.remotes;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class InkDisruptorItem extends RemoteItem
{
    public InkDisruptorItem(String name)
    {
        super(name, new Properties().group(SplatcraftItemGroups.GROUP_GENERAL).maxStackSize(1));
    }

    @Override
    public RemoteResult onRemoteUse(World usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode)
    {
        return clearInk(getWorld(usedOnWorld, stack), posA, posB);
    }

    public static RemoteResult clearInk(World world, BlockPos posA, BlockPos posB)
    {
        BlockPos blockpos2 = new BlockPos(Math.min(posA.getX(), posB.getX()), Math.min(posB.getY(), posA.getY()), Math.min(posA.getZ(), posB.getZ()));
        BlockPos blockpos3 = new BlockPos(Math.max(posA.getX(), posB.getX()), Math.max(posB.getY(), posA.getY()), Math.max(posA.getZ(), posB.getZ()));

        if (!(blockpos2.getY() >= 0 && blockpos3.getY() < 256))
        {
            return createResult(false, new TranslationTextComponent("status.clear_ink.out_of_world"));
        }


        for (int j = blockpos2.getZ(); j <= blockpos3.getZ(); j += 16)
        {
            for (int k = blockpos2.getX(); k <= blockpos3.getX(); k += 16)
            {
                if (!world.isBlockLoaded(new BlockPos(k, blockpos3.getY() - blockpos2.getY(), j)))
                {
                    return createResult(false, new TranslationTextComponent("status.clear_ink.out_of_world"));
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
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof IColoredBlock)
                    {
                        if (((IColoredBlock) block).remoteInkClear(world, pos))
                        {
                            count++;
                        }
                    }
                    blockTotal++;
                }
            }
        }

        return createResult(true, new TranslationTextComponent("status.clear_ink." + (count > 0 ? "success" : "no_ink"), count)).setIntResults(count, count * 15 / blockTotal);
    }
}
