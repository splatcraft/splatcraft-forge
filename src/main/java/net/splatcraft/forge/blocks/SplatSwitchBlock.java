package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.BlockInkedResult;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

public class SplatSwitchBlock extends Block implements IColoredBlock, SimpleWaterloggedBlock, EntityBlock
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
        super((Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(POWERED, false));

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> containter) {
        containter.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
        return state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(InkedBlock.isTouchingLiquid(levelIn, currentPos) && levelIn instanceof Level)
        {
            stateIn = stateIn.setValue(POWERED, false);
            levelIn.setBlock(currentPos, stateIn, 3);
            playSound(levelIn, currentPos, stateIn);
            updateNeighbors(stateIn, (Level) levelIn, currentPos);
            return stateIn;
        }
        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(state.getValue(POWERED))
            updateNeighbors(state, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void updateNeighbors(BlockState state, Level level, BlockPos pos) {
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
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor) {
        return false;
    }

    @Override
    public int getColor(Level level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        return state.getValue(POWERED) && level.getBlockEntity(pos) instanceof InkColorTileEntity ?
                ((InkColorTileEntity) level.getBlockEntity(pos)).getColor() : -1;
    }

    @Override
    public BlockInkedResult inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if(!(level.getBlockState(pos).getBlock().equals(this)) || !(level.getBlockEntity(pos) instanceof InkColorTileEntity te))
            return BlockInkedResult.FAIL;

        BlockState state = level.getBlockState(pos);
        int switchColor = te.getColor();

        te.setColor(color);
        level.setBlock(pos, state.setValue(POWERED, true), 3);
        playSound(level, pos, state);
        updateNeighbors(state, level, pos);
        return color != switchColor ? BlockInkedResult.SUCCESS : BlockInkedResult.ALREADY_INKED;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), 3);
            playSound(level, pos, state);
            return true;
        }
        return false;
    }

    private void playSound(LevelAccessor level, BlockPos currentPos, BlockState stateIn) {
        level.playSound(null, currentPos, stateIn.getValue(POWERED) ? SplatcraftSounds.splatSwitchPoweredOn : SplatcraftSounds.splatSwitchPoweredOff, SoundSource.BLOCKS, 1f, 1f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftTileEntities.colorTileEntity.get().create(pos, state);
    }
}
