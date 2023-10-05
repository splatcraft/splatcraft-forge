package net.splatcraft.forge.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;
import net.splatcraft.forge.registries.SplatcraftBlocks;

import java.util.Random;

public class SardiniumDepositFeature extends Feature<NoneFeatureConfiguration>
{
	public SardiniumDepositFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
	{
		Random random = context.random();
		BlockPos centerPos = context.origin();

		WorldGenLevel worldgenlevel = context.level();
		centerPos = new BlockPos(centerPos.getX(), worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, centerPos.getX(), centerPos.getZ()), centerPos.getZ());
		boolean flag = random.nextDouble() > 0.7D;
		double d0 = random.nextDouble() * 2.0D * Math.PI;
		int i = 11 - random.nextInt(5);
		int j = 3 + random.nextInt(3);
		boolean flag1 = random.nextDouble() > 0.7D;
		int k = 11;
		int l = flag1 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;
		if (!flag1 && random.nextDouble() > 0.9D) {
			l += random.nextInt(19) + 7;
		}

		int i1 = Math.min(l + random.nextInt(11), 18);
		int j1 = Math.min(l + random.nextInt(7) - random.nextInt(5), 11);
		int k1 = flag1 ? i : 11;

		int radius = 8;

		for(int l1 = -k1; l1 < k1; ++l1) {
			for(int i2 = -k1; i2 < k1; ++i2) {
				for(int j2 = 0; j2 < l; ++j2) {
					int k2 = flag1 ? this.heightDependentRadiusEllipse(j2, l, j1) : this.heightDependentRadiusRound(random, j2, l, j1);
					if (flag1 || l1 < k2) {
						this.generateIcebergBlock(worldgenlevel, random, centerPos, l, l1, j2, i2, k2, k1, flag1, j, d0, random.nextFloat() < 0.2f ? SplatcraftBlocks.sardiniumOre.get().defaultBlockState() : SplatcraftBlocks.coralite.get().defaultBlockState());
					}
				}
			}
		}

		for(int i3 = -k1; i3 < k1; ++i3) {
			for(int j3 = -k1; j3 < k1; ++j3) {
				for(int k3 = -1; k3 > -i1; --k3) {
					int l3 = flag1 ? Mth.ceil((float)k1 * (1.0F - (float)Math.pow((double)k3, 2.0D) / ((float)i1 * 8.0F))) : k1;
					int l2 = this.heightDependentRadiusSteep(random, -k3, i1, j1);
					if (i3 < l2) {
						this.generateIcebergBlock(context.level(), random, centerPos, i1, i3, k3, j3, l2, l3, flag1, j, d0,
								random.nextFloat() < 0.05f ? SplatcraftBlocks.rawSardiniumBlock.get().defaultBlockState() : random.nextFloat() < 0.3f ? SplatcraftBlocks.sardiniumOre.get().defaultBlockState() : SplatcraftBlocks.coralite.get().defaultBlockState());
					}
				}
			}
		}

		/*
		for(int xx = -radius; xx <= radius; xx++)
			for(int yy = -radius; yy <= radius; yy++)
				for(int zz = -radius; zz <= radius; zz++)
				{
					BlockPos pos = centerPos.offset(xx,yy,zz);
					int i = heightDependentRadiusRound(random, xx,yy,zz);
					if (Math.abs(i) <= 2)
					{
						System.out.println(i);
					}
				}
		*/
		return true;
	}



	private void generateIcebergBlock(LevelAccessor level, Random p_66060_, BlockPos p_66061_, int p_66062_, int p_66063_, int p_66064_, int p_66065_, int p_66066_, int p_66067_, boolean p_66068_, int p_66069_, double p_66070_, BlockState state) {
		double d0 = p_66068_ ? this.signedDistanceEllipse(p_66063_, p_66065_, BlockPos.ZERO, p_66067_, this.getEllipseC(p_66064_, p_66062_, p_66069_), p_66070_) : this.signedDistanceCircle(p_66063_, p_66065_, BlockPos.ZERO, p_66066_, p_66060_);
		if (d0 < 0.0D) {
			BlockPos blockpos = p_66061_.offset(p_66063_, p_66064_, p_66065_);
			double d1 = p_66068_ ? -0.5D : (double)(-6 - p_66060_.nextInt(3));
			if (d0 > d1 && p_66060_.nextDouble() > 0.9D) {
				return;
			}

			level.setBlock(blockpos, state, 2);
		}

	}


	private int getEllipseC(int p_66019_, int p_66020_, int p_66021_) {
		int i = p_66021_;
		if (p_66019_ > 0 && p_66020_ - p_66019_ <= 3) {
			i = p_66021_ - (4 - (p_66020_ - p_66019_));
		}

		return i;
	}

	private double signedDistanceCircle(int p_66030_, int p_66031_, BlockPos p_66032_, int p_66033_, Random p_66034_) {
		float f = 10.0F * Mth.clamp(p_66034_.nextFloat(), 0.2F, 0.8F) / (float)p_66033_;
		return (double)f + Math.pow((double)(p_66030_ - p_66032_.getX()), 2.0D) + Math.pow((double)(p_66031_ - p_66032_.getZ()), 2.0D) - Math.pow((double)p_66033_, 2.0D);
	}

	private double signedDistanceEllipse(int p_66023_, int p_66024_, BlockPos p_66025_, int p_66026_, int p_66027_, double p_66028_) {
		return Math.pow(((double)(p_66023_ - p_66025_.getX()) * Math.cos(p_66028_) - (double)(p_66024_ - p_66025_.getZ()) * Math.sin(p_66028_)) / (double)p_66026_, 2.0D) + Math.pow(((double)(p_66023_ - p_66025_.getX()) * Math.sin(p_66028_) + (double)(p_66024_ - p_66025_.getZ()) * Math.cos(p_66028_)) / (double)p_66027_, 2.0D) - 1.0D;
	}

	private int heightDependentRadiusSteep(Random p_66114_, int p_66115_, int p_66116_, int p_66117_) {
		float f = 1.0F + p_66114_.nextFloat() / 2.0F;
		float f1 = (1.0F - (float)p_66115_ / ((float)p_66116_ * f)) * (float)p_66117_;
		return Mth.ceil(f1 / 2.0F);
	}

	private int heightDependentRadiusRound(Random p_66095_, int p_66096_, int p_66097_, int p_66098_) {
		float f = 3.5F - p_66095_.nextFloat();
		float f1 = (1.0F - (float)Math.pow((double)p_66096_, 2.0D) / ((float)p_66097_ * f)) * (float)p_66098_;
		if (p_66097_ > 15 + p_66095_.nextInt(5)) {
			int i = p_66096_ < 3 + p_66095_.nextInt(6) ? p_66096_ / 2 : p_66096_;
			f1 = (1.0F - (float)i / ((float)p_66097_ * f * 0.4F)) * (float)p_66098_;
		}

		return Mth.ceil(f1 / 2.0F);
	}

	private int heightDependentRadiusEllipse(int p_66110_, int p_66111_, int p_66112_) {
		float f = 1.0F;
		float f1 = (1.0F - (float)Math.pow((double)p_66110_, 2.0D) / ((float)p_66111_ * 1.0F)) * (float)p_66112_;
		return Mth.ceil(f1 / 2.0F);
	}

	private void smooth(LevelAccessor p_66052_, BlockPos p_66053_, int p_66054_, int p_66055_, boolean p_66056_, int p_66057_) {
		int i = p_66056_ ? p_66057_ : p_66054_ / 2;

		for(int j = -i; j <= i; ++j) {
			for(int k = -i; k <= i; ++k) {
				for(int l = 0; l <= p_66055_; ++l) {
					BlockPos blockpos = p_66053_.offset(j, l, k);
					BlockState blockstate = p_66052_.getBlockState(blockpos);
					if (isIcebergState(blockstate) || blockstate.is(Blocks.SNOW)) {
						if (this.belowIsAir(p_66052_, blockpos)) {
							this.setBlock(p_66052_, blockpos, Blocks.AIR.defaultBlockState());
							this.setBlock(p_66052_, blockpos.above(), Blocks.AIR.defaultBlockState());
						} else if (isIcebergState(blockstate)) {
							BlockState[] ablockstate = new BlockState[]{p_66052_.getBlockState(blockpos.west()), p_66052_.getBlockState(blockpos.east()), p_66052_.getBlockState(blockpos.north()), p_66052_.getBlockState(blockpos.south())};
							int i1 = 0;

							for(BlockState blockstate1 : ablockstate) {
								if (!isIcebergState(blockstate1)) {
									++i1;
								}
							}

							if (i1 >= 3) {
								this.setBlock(p_66052_, blockpos, Blocks.AIR.defaultBlockState());
							}
						}
					}
				}
			}
		}

	}

	private static boolean isIcebergState(BlockState p_159886_) {
		return p_159886_.is(SplatcraftBlocks.coralite.get()) || p_159886_.is(SplatcraftBlocks.sardiniumOre.get()) || p_159886_.is(SplatcraftBlocks.rawSardiniumBlock.get());
	}

	private boolean belowIsAir(BlockGetter p_66046_, BlockPos p_66047_) {
		return p_66046_.getBlockState(p_66047_.below()).getMaterial() == Material.AIR;
	}
}
