package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.InkBlockUtils;

public interface IColoredBlock
{
    boolean canClimb();

    boolean canSwim();

    boolean canDamage();

    default int getColor(Level level, BlockPos pos)
    {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) tileEntity).getColor();
        }
        return -1;
    }

    default boolean canRemoteColorChange(Level level, BlockPos pos, int color, int newColor)
    {
        return color != newColor;
    }

    boolean remoteColorChange(Level level, BlockPos pos, int newColor);

    boolean remoteInkClear(Level level, BlockPos pos);

    default boolean setColor(Level level, BlockPos pos, int color)
    {
        return false;
    }

    default boolean inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        return false;
    }
}
