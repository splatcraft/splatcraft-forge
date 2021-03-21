package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import net.minecraft.block.Block;

public class BlockItem extends net.minecraft.item.BlockItem {
    public BlockItem(Block block) {
        super(block, new Properties().group(SplatcraftItemGroups.GROUP_GENERAL));
    }

    public BlockItem(Block block, Properties properties) {
        super(block, properties);
    }
}
