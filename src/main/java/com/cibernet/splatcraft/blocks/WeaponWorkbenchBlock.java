package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.container.WeaponWorkbenchContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class WeaponWorkbenchBlock extends HorizontalBlock implements IWaterLoggable {

    protected static final VoxelShape BOTTOM_LEFT = makeCuboidShape(2, 0, 0, 5, 4, 16);
    protected static final VoxelShape BOTTOM_RIGHT = makeCuboidShape(11, 0, 0, 14, 4, 16);
    protected static final VoxelShape BASE = makeCuboidShape(1, 1, 1, 15, 16, 15);
    protected static final VoxelShape DETAIL = makeCuboidShape(0, 8, 0, 16, 10, 16);
    protected static final VoxelShape HANDLE = makeCuboidShape(5, 11, 0, 11, 12, 1);

    public static final VoxelShape[] SHAPES = createVoxelShapes(BOTTOM_LEFT, BOTTOM_RIGHT, BASE, DETAIL, HANDLE);

    public static final EnumProperty<Direction> FACING = HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.weapon_workbench");

    public WeaponWorkbenchBlock(String name) {
        super(Properties.create(Material.ROCK).hardnessAndResistance(2.0f).harvestTool(ToolType.PICKAXE).setRequiresTool());
        setRegistryName(name);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote)
            return ActionResultType.SUCCESS;
        player.openContainer(getContainer(state, worldIn, pos));
        return ActionResultType.CONSUME;

    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, inventory, player) -> new WeaponWorkbenchContainer(inventory, IWorldPosCallable.of(worldIn, pos), id), CONTAINER_NAME);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(FACING).getHorizontalIndex()];
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape) {
        AxisAlignedBB bb = shape.getBoundingBox();

        switch (facing) {
            case EAST:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.minZ, bb.minY, 1 - bb.minX, 1 - bb.maxZ, bb.maxY, 1 - bb.maxX));
            case SOUTH:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxX, bb.minY, 1 - bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return VoxelShapes.create(new AxisAlignedBB(bb.minZ, bb.minY, bb.minX, bb.maxZ, bb.maxY, bb.maxX));
        }
        return shape;
    }

    public static VoxelShape[] createVoxelShapes(VoxelShape... shapes) {
        VoxelShape[] result = new VoxelShape[4];

        for (int i = 0; i < 4; i++) {
            result[i] = VoxelShapes.empty();
            for (VoxelShape shape : shapes) {
                result[i] = VoxelShapes.or(result[i], modifyShapeForDirection(Direction.byHorizontalIndex(i), shape));
            }

        }

        return result;
    }
}
