package net.splatcraft.forge.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.ColoredBarrierTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class ColoredBarrierBlock extends StageBarrierBlock implements IColoredBlock
{
    public final boolean blocksColor;
    public ColoredBarrierBlock(String name, boolean blocksColor)
    {
        super(name, false);
        this.blocksColor = blocksColor;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level)
    {
        return SplatcraftTileEntities.colorBarrierTileEntity.create();
    }

    @Override
    public boolean setColor(World level, BlockPos pos, int color)
    {
        BlockState state = level.getBlockState(pos);
        if(level.getBlockEntity(pos) instanceof ColoredBarrierTileEntity)
        {
            ((ColoredBarrierTileEntity) level.getBlockEntity(pos)).setColor(color);
            level.sendBlockUpdated(pos, state, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public int getColor(World level, BlockPos pos)
    {
        if(level.getBlockEntity(pos) instanceof ColoredBarrierTileEntity)
            return ((ColoredBarrierTileEntity) level.getBlockEntity(pos)).getColor();
        return -1;
    }


    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, level, pos, player), getColor((World) level, pos)), true);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        if(context.getEntity() == null)
            return super.getCollisionShape(state, levelIn, pos, context);

        if (ColorUtils.getEntityColor(context.getEntity()) > -1)
            return !canAllowThrough(pos, context.getEntity()) ? super.getCollisionShape(state, levelIn, pos, context) : VoxelShapes.empty();
        return blocksColor ? super.getCollisionShape(state, levelIn, pos, context) : VoxelShapes.empty();
    }

    public boolean canAllowThrough(BlockPos pos, Entity entity)
    {
        return blocksColor != ColorUtils.colorEquals(entity, entity.level.getBlockEntity(pos));
    }

    @Override
    public boolean canClimb() {
        return false;
    }

    @Override
    public boolean canSwim() {
        return false;
    }

    @Override
    public boolean canDamage() {
        return false;
    }

    @Override
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {
        return setColor(level, pos, newColor);
    }


    @Override
    public boolean remoteInkClear(World level, BlockPos pos) {
        return false;
    }
}
