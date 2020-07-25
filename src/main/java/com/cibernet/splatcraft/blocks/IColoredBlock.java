package com.cibernet.splatcraft.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IColoredBlock
{
	boolean canClimb();
	boolean canSwim();
	boolean canDamage();
	int getColor(World world, BlockPos pos);
	
}
