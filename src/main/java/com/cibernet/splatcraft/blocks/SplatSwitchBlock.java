package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SplatSwitchBlock extends Block implements IColoredBlock, IWaterLoggable
{
    private static final VoxelShape[] SHAPES = new VoxelShape[]
            {
            makeCuboidShape(1, 14, 1, 15, 16, 15),
            makeCuboidShape(1, 0, 1, 15, 2, 15),
                    makeCuboidShape(1, 1, 14, 15, 15, 16),
            makeCuboidShape(1, 1, 0, 15, 15, 2),
                    makeCuboidShape(14, 1, 1, 16, 15, 15),
            makeCuboidShape(0, 1, 1, 2, 15, 15)
    };

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SplatSwitchBlock()
    {
        super((AbstractBlock.Properties.create(Material.IRON).setRequiresTool().hardnessAndResistance(5.0F).sound(SoundType.METAL).notSolid()));
        setDefaultState(getDefaultState().with(FACING, Direction.UP).with(POWERED, false));

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> containter) {
        containter.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(FACING).ordinal()];
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return SplatcraftTileEntitites.colorTileEntity.create();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context).with(FACING, context.getFace());
        return state.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(InkedBlock.isTouchingLiquid(worldIn, currentPos) && worldIn instanceof World)
        {
            stateIn = stateIn.with(POWERED, false);
            worldIn.setBlockState(currentPos, stateIn, 3);
            updateNeighbors(stateIn, (World) worldIn, currentPos);
            return stateIn;
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(state.get(POWERED))
            updateNeighbors(state, world, pos);
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        world.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), this);
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
    public boolean remoteColorChange(World world, BlockPos pos, int newColor) {
        return false;
    }

    @Override
    public int getColor(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        return state.get(POWERED) && world.getTileEntity(pos) instanceof InkColorTileEntity ?
                ((InkColorTileEntity) world.getTileEntity(pos)).getColor() : -1;
    }

    @Override
    public boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if(!(world.getBlockState(pos).getBlock().equals(this)) || !(world.getTileEntity(pos) instanceof InkColorTileEntity))
            return false;

        BlockState state = world.getBlockState(pos);
        InkColorTileEntity te = (InkColorTileEntity) world.getTileEntity(pos);
        int switchColor = te.getColor();

        te.setColor(color);
        world.setBlockState(pos, state.with(POWERED, true), 3);
        updateNeighbors(state, world, pos);
        return color != switchColor;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        if(state.get(POWERED))
        {
            world.setBlockState(pos, state.with(POWERED, false), 3);
            return true;
        }
        return false;
    }
}
