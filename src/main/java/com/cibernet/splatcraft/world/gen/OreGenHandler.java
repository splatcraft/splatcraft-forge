package com.cibernet.splatcraft.world.gen;

import static com.cibernet.splatcraft.registries.SplatCraftBlocks.*;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class OreGenHandler implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if(world.provider.isSurfaceWorld())
        {
            addOreSpawn(oreSardinium.getDefaultState(), world, random, chunkX*16, chunkZ*16, 16, 16,
                    4 + random.nextInt(6), 16, 0, 60);
        }
    }

    public void addOreSpawn(IBlockState block, World world, Random random, int blockXPos, int blockZPos, int maxX, int maxZ, int maxVeinSize, int chancesToSpawn, int minY, int maxY)
    {
        int diffBtwnMinMaxY = maxY - minY;
        IBlockState groundType = Blocks.STONE.getDefaultState();
        for(int x = 0; x < chancesToSpawn; x++)
        {
            int posX = blockXPos + random.nextInt(maxX);
            int posY = minY + random.nextInt(diffBtwnMinMaxY);
            int posZ = blockZPos + random.nextInt(maxZ);
            (new WorldGenMinable(block, maxVeinSize, new BlockStatePredicate(groundType))).generate(world, random, new BlockPos(posX, posY, posZ));
        }
    }

    public static class BlockStatePredicate implements Predicate
    {
        IBlockState[] states;
        public BlockStatePredicate(IBlockState... blockStates)
        {
            states = blockStates;
        }
        @Override
        public boolean apply(Object input)
        {
            for(IBlockState state : states)
                if(state.equals(input))
                    return true;
            return false;
        }
    }
}
