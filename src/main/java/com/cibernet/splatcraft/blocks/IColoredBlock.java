package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IColoredBlock
{
    boolean canClimb();

    boolean canSwim();

    boolean canDamage();

    default int getColor(World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) tileEntity).getColor();
        }
        return -1;
    }

    boolean remoteColorChange(World world, BlockPos pos, int newColor);

    boolean remoteInkClear(World world, BlockPos pos);

    default boolean setColor(World world, BlockPos pos, int color)
    {
        return false;
    }

    default boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        return false;
    }
}
