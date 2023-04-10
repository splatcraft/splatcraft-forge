package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CanvasBlock extends Block implements IColoredBlock, EntityBlock
{

    public static final BooleanProperty INKED = BooleanProperty.create("inked");

    public CanvasBlock(String name)
    {
        super(Properties.of(Material.WOOL).strength(0.8f).sound(SoundType.WOOL));
        SplatcraftBlocks.inkColoredBlocks.add(this);
        registerDefaultState(defaultBlockState().setValue(INKED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return super.getStateForPlacement(context).setValue(INKED, ColorUtils.getInkColor(context.getItemInHand()) != -1);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
    {
        InkColorTileEntity te = SplatcraftTileEntities.colorTileEntity.get().create(pos, state);
        if (te != null)
            te.setColor(-1);
        return te;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(INKED);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor levelIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos)
    {
        int color = getColor((Level) levelIn, currentPos);

        if (InkedBlock.isTouchingLiquid(levelIn, currentPos))
        {
            if (levelIn.getBlockEntity(currentPos) instanceof InkColorTileEntity tileEntity)
                tileEntity.setColor(-1);
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos).setValue(INKED, color != -1);
    }

    @Override
    public boolean inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (InkedBlock.isTouchingLiquid(level, pos))
            return false;

        if (color == getColor(level, pos))
            return false;

        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof InkColorTileEntity)
        {
            BlockState state = level.getBlockState(pos);
            ((InkColorTileEntity) tileEntity).setColor(color);
            level.setBlock(pos, state.setValue(INKED, true), 2);
            level.sendBlockUpdated(pos, state, state.setValue(INKED, true), 2);
            return true;
        }

        return false;
    }

    @Override
    public boolean canClimb()
    {
        return true;
    }

    @Override
    public boolean canSwim()
    {
        return true;
    }

    @Override
    public boolean canDamage()
    {
        return false;
    }

    @Override
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {
        return setColor(level, pos, newColor);
    }

    @Override
    public boolean setColor(Level level, BlockPos pos, int newColor)
    {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof InkColorTileEntity && ((InkColorTileEntity) tileEntity).getColor() != newColor)
        {
            ((InkColorTileEntity) tileEntity).setColor(newColor);

            BlockState state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, 3);
            state.updateNeighbourShapes(level, pos, 3);

            return true;
        }
        return false;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos)
    {
        return false;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getCloneItemStack(state, target, level, pos, player), getColor((Level) level, pos)), true);
    }
}
