package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CanvasBlock extends Block implements IColoredBlock
{

    public static final BooleanProperty INKED = BooleanProperty.create("inked");

    public CanvasBlock(String name)
    {
        super(Properties.of(Material.WOOL).strength(0.8f).sound(SoundType.WOOL));
        SplatcraftBlocks.inkColoredBlocks.add(this);
        setRegistryName(name);
        registerDefaultState(defaultBlockState().setValue(INKED, false));
    }

    @SuppressWarnings("unused")
    private static BlockState clearInk(IWorld level, BlockPos pos)
    {
        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
        if (te != null && te.hasSavedState())
        {
            level.setBlock(pos, te.getSavedState(), 3);

            if (te.hasSavedColor() && te.getSavedState().getBlock() instanceof IColoredBlock)
            {
                ((World) level).setBlockEntity(pos, te.getSavedState().getBlock().createTileEntity(te.getSavedState(), level));
                if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
                {
                    InkColorTileEntity newte = (InkColorTileEntity) level.getBlockEntity(pos);
                    if (newte != null)
                        newte.setColor(te.getSavedColor());
                }
            }

            return te.getSavedState();
        }

        return level.getBlockState(pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return super.getStateForPlacement(context).setValue(INKED, ColorUtils.getInkColor(context.getItemInHand()) != -1);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level)
    {
        InkColorTileEntity te = SplatcraftTileEntities.colorTileEntity.create();
        if (te != null)
            te.setColor(-1);
        return te;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(INKED);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull IWorld levelIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos)
    {
        int color = getColor((World) levelIn, currentPos);

        if (InkedBlock.isTouchingLiquid(levelIn, currentPos))
        {
            TileEntity tileEntity = levelIn.getBlockEntity(currentPos);
            if (tileEntity instanceof InkColorTileEntity)
                ((InkColorTileEntity) tileEntity).setColor(-1);
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos).setValue(INKED, color != -1);
    }

    @Override
    public boolean inkBlock(World level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (InkedBlock.isTouchingLiquid(level, pos))
            return false;

        if (color == getColor(level, pos))
            return false;

        TileEntity tileEntity = level.getBlockEntity(pos);
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
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {
        return setColor(level, pos, newColor);
    }

    @Override
    public boolean setColor(World level, BlockPos pos, int newColor)
    {
        TileEntity tileEntity = level.getBlockEntity(pos);
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
    public boolean remoteInkClear(World level, BlockPos pos)
    {
        return false;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, level, pos, player), getColor((World) level, pos)), true);
    }
}
