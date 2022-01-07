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

    default int getColor(World level, BlockPos pos)
    {
        TileEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) tileEntity).getColor();
        }
        return -1;
    }

    default boolean canRemoteColorChange(World level, BlockPos pos, int color, int newColor)
    {
        return color != newColor;
    }

    boolean remoteColorChange(World level, BlockPos pos, int newColor);

    boolean remoteInkClear(World level, BlockPos pos);

    default boolean setColor(World level, BlockPos pos, int color)
    {
        return false;
    }

    default boolean inkBlock(World level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        return false;
    }
}
