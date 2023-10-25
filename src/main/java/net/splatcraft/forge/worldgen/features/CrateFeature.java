package net.splatcraft.forge.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.splatcraft.forge.blocks.CrateBlock;
import net.splatcraft.forge.blocks.DebrisBlock;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.tileentities.CrateTileEntity;

import java.util.Random;

public class CrateFeature extends Feature<CountConfiguration>
{
	public CrateFeature(Codec<CountConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<CountConfiguration> context)
	{
		int i = 0;
		Random random = context.random();
		WorldGenLevel worldgenlevel = context.level();
		BlockPos blockpos = context.origin();
		int j = context.config().count().sample(random);

		int area = 8;

		for(int k = 0; k < j; ++k)
		{
			int l = random.nextInt(area) - random.nextInt(area);
			int i1 = random.nextInt(area) - random.nextInt(area);
			int j1 = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockpos.getX() + l, blockpos.getZ() + i1);
			BlockPos blockpos1 = new BlockPos(blockpos.getX() + l, j1, blockpos.getZ() + i1);

			boolean isSunken = random.nextFloat() <= 0.05f;

			BlockState state = isSunken ? SplatcraftBlocks.sunkenCrate.get().defaultBlockState() : SplatcraftBlocks.crate.get().defaultBlockState();
			worldgenlevel.setBlock(blockpos1, state, 2);

			if(!isSunken)
			{
				if(worldgenlevel.getBlockEntity(blockpos1) instanceof CrateTileEntity crate)
					crate.setLootTable(CrateBlock.STORAGE_EGG_CRATE);
			}

			++i;
		}

		if(random.nextFloat() <= 0.0125f * j)
		{
			int l = random.nextInt(area) - random.nextInt(area);
			int i1 = random.nextInt(area) - random.nextInt(area);
			int j1 = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockpos.getX() + l, blockpos.getZ() + i1);
			BlockPos blockpos1 = new BlockPos(blockpos.getX() + l, j1, blockpos.getZ() + i1);
			BlockState state = SplatcraftBlocks.ammoKnightsDebris.get().defaultBlockState().setValue(DebrisBlock.DIRECTION, Direction.from2DDataValue(random.nextInt(4)));

			if(state.canSurvive(worldgenlevel, blockpos1))
			{
				worldgenlevel.setBlock(blockpos1, state.setValue(DebrisBlock.WATERLOGGED, worldgenlevel.getFluidState(blockpos1).is(Fluids.WATER)), 2);
				++i;
			}
		}

		return i > 0;
	}
}
