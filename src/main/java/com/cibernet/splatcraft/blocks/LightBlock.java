package com.cibernet.splatcraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LightBlock extends Block {
    private final int lightLevel;

    public LightBlock(int lightLevel, Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.lightLevel = lightLevel;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return lightLevel;
    }
}
