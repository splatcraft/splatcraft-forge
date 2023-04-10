package net.splatcraft.forge.items;

import net.minecraft.world.level.block.Block;
import net.splatcraft.forge.registries.SplatcraftItemGroups;

public class BlockItem extends net.minecraft.world.item.BlockItem
{
    public BlockItem(Block block)
    {
        super(block, new Properties().tab(SplatcraftItemGroups.GROUP_GENERAL));
    }

    public BlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }
}
