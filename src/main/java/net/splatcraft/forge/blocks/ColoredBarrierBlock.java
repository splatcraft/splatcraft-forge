package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.entities.IColoredEntity;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.ColoredBarrierTileEntity;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class ColoredBarrierBlock extends StageBarrierBlock implements IColoredBlock
{
    public final boolean blocksColor;
    public ColoredBarrierBlock(boolean blocksColor)
    {
        super(false);
        this.blocksColor = blocksColor;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return SplatcraftTileEntities.colorBarrierTileEntity.get().create(pos, state);
    }

    @Override
    public boolean setColor(Level level, BlockPos pos, int color)
    {
        BlockState state = level.getBlockState(pos);
        if(level.getBlockEntity(pos) instanceof ColoredBarrierTileEntity)
        {
            ((ColoredBarrierTileEntity) level.getBlockEntity(pos)).setColor(color);
            level.sendBlockUpdated(pos, state, state, 3);
            state.updateNeighbourShapes(level, pos, 3);
            return true;
        }
        return false;
    }

    @Override
    public int getColor(Level level, BlockPos pos)
    {
        if(level.getBlockEntity(pos) instanceof ColoredBarrierTileEntity colorTileEntity)
            return colorTileEntity.getColor();
        return -1;
    }

    @Override
    public boolean isInverted(Level level, BlockPos pos)
    {
        return (level.getBlockEntity(pos) instanceof ColoredBarrierTileEntity colorTileEntity) && colorTileEntity.isInverted();
    }

    @Override
    public void setInverted(Level level, BlockPos pos, boolean inverted)
    {
        if(level.getBlockEntity(pos) instanceof ColoredBarrierTileEntity colorTileEntity)
            colorTileEntity.setInverted(inverted);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getCloneItemStack(state, target, level, pos, player), getColor((Level) level, pos)), true);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        if(!(context instanceof EntityCollisionContext entityContext))
            return super.getCollisionShape(state, levelIn, pos, context);

        if (ColorUtils.getEntityColor(entityContext.getEntity()) > -1)
            return !canAllowThrough(pos, entityContext.getEntity()) ? super.getCollisionShape(state, levelIn, pos, context) : Shapes.empty();
        return entityContext.getEntity() == null || blocksColor ? super.getCollisionShape(state, levelIn, pos, context) : Shapes.empty();

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
    public boolean canRemoteColorChange(Level level, BlockPos pos, int color, int newColor)
    {
        return IColoredBlock.super.canRemoteColorChange(level, pos, color, newColor);
    }

    @Override
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {
        return setColor(level, pos, newColor);
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos) {
        return false;
    }
}
