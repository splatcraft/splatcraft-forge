package net.splatcraft.forge.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

public class SplatSwitchBlock extends Block implements IColoredBlock, IWaterLoggable
{
    private static final VoxelShape[] SHAPES = new VoxelShape[]
            {
            box(1, 14, 1, 15, 16, 15),
            box(1, 0, 1, 15, 2, 15),
                    box(1, 1, 14, 15, 15, 16),
            box(1, 1, 0, 15, 15, 2),
                    box(14, 1, 1, 16, 15, 15),
            box(0, 1, 1, 2, 15, 15)
    };

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SplatSwitchBlock()
    {
        super((AbstractBlock.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(POWERED, false));

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> containter) {
        containter.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader level, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction face) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction face) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return SplatcraftTileEntitites.colorTileEntity.create();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
        return state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(InkedBlock.isTouchingLiquid(levelIn, currentPos) && levelIn instanceof World)
        {
            stateIn = stateIn.setValue(POWERED, false);
            levelIn.setBlock(currentPos, stateIn, 3);
            playSound(levelIn, currentPos, stateIn);
            updateNeighbors(stateIn, (World) levelIn, currentPos);
            return stateIn;
        }
        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(state.getValue(POWERED))
            updateNeighbors(state, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void updateNeighbors(BlockState state, World level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
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
    public boolean remoteColorChange(World level, BlockPos pos, int newColor) {
        return false;
    }

    @Override
    public int getColor(World level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        return state.getValue(POWERED) && level.getBlockEntity(pos) instanceof InkColorTileEntity ?
                ((InkColorTileEntity) level.getBlockEntity(pos)).getColor() : -1;
    }

    @Override
    public boolean inkBlock(World level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if(!(level.getBlockState(pos).getBlock().equals(this)) || !(level.getBlockEntity(pos) instanceof InkColorTileEntity))
            return false;

        BlockState state = level.getBlockState(pos);
        InkColorTileEntity te = (InkColorTileEntity) level.getBlockEntity(pos);
        int switchColor = te.getColor();

        te.setColor(color);
        level.setBlock(pos, state.setValue(POWERED, true), 3);
        playSound(level, pos, state);
        updateNeighbors(state, level, pos);
        return color != switchColor;
    }

    @Override
    public boolean remoteInkClear(World level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            playSound(level, pos, state);
            return true;
        }
        return false;
    }

    private void playSound(IWorld level, BlockPos currentPos, BlockState stateIn) {
        level.playSound(null, currentPos, stateIn.getValue(POWERED) ? SplatcraftSounds.splatSwitchPoweredOn : SplatcraftSounds.splatSwitchPoweredOff, SoundCategory.BLOCKS, 1f, 1f);
    }
}
