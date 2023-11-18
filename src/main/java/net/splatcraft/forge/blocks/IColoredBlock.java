package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.BlockInkedResult;
import net.splatcraft.forge.util.InkBlockUtils;

public interface IColoredBlock
{
    boolean canClimb();

    boolean canSwim();

    boolean canDamage();

    default boolean isInverted(Level level, BlockPos pos)
    {
        return (level.getBlockEntity(pos) instanceof InkColorTileEntity colorTileEntity) && colorTileEntity.isInverted();
    }

    default void setInverted(Level level, BlockPos pos, boolean inverted)
    {
        if(level.getBlockEntity(pos) instanceof InkColorTileEntity colorTileEntity)
            colorTileEntity.setInverted(inverted);
    }

    default int getColor(Level level, BlockPos pos)
    {
        return (level.getBlockEntity(pos) instanceof InkColorTileEntity colorTileEntity) ? colorTileEntity.getColor() : -1;
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

    default BlockInkedResult inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        return BlockInkedResult.PASS;
    }
}
