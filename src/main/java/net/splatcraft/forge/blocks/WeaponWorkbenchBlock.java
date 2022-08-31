package net.splatcraft.forge.blocks;

import net.splatcraft.forge.tileentities.container.WeaponWorkbenchContainer;
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

public class WeaponWorkbenchBlock extends HorizontalBlock implements IWaterLoggable
{

    public static final EnumProperty<Direction> FACING = HorizontalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape BOTTOM_LEFT = box(2, 0, 0, 5, 4, 16);
    protected static final VoxelShape BOTTOM_RIGHT = box(11, 0, 0, 14, 4, 16);
    protected static final VoxelShape BASE = box(1, 1, 1, 15, 16, 15);
    protected static final VoxelShape DETAIL = box(0, 8, 0, 16, 10, 16);
    protected static final VoxelShape HANDLE = box(5, 11, 0, 11, 12, 1);
    public static final VoxelShape[] SHAPES = createVoxelShapes(BOTTOM_LEFT, BOTTOM_RIGHT, BASE, DETAIL, HANDLE);
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.ammo_knights_workbench");

    public WeaponWorkbenchBlock(String name)
    {
        super(Properties.of(Material.STONE).strength(2.0f).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
        setRegistryName(name);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    protected static VoxelShape modifyShapeForDirection(Direction facing, VoxelShape shape)
    {
        AxisAlignedBB bb = shape.bounds();

        switch (facing)
        {
            case EAST:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.minZ, bb.minY, 1 - bb.minX, 1 - bb.maxZ, bb.maxY, 1 - bb.maxX));
            case SOUTH:
                return VoxelShapes.create(new AxisAlignedBB(1 - bb.maxX, bb.minY, 1 - bb.maxZ, 1 - bb.minX, bb.maxY, 1 - bb.minZ));
            case WEST:
                return VoxelShapes.create(new AxisAlignedBB(bb.minZ, bb.minY, bb.minX, bb.maxZ, bb.maxY, bb.maxX));
        }
        return shape;
    }

    public static VoxelShape[] createVoxelShapes(VoxelShape... shapes)
    {
        VoxelShape[] result = new VoxelShape[4];

        for (int i = 0; i < 4; i++)
        {
            result[i] = VoxelShapes.empty();
            for (VoxelShape shape : shapes)
            {
                result[i] = VoxelShapes.or(result[i], modifyShapeForDirection(Direction.from2DDataValue(i), shape));
            }

        }

        return result;
    }

    @Override
    public ActionResultType use(BlockState state, World levelIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (levelIn.isClientSide)
        {
            return ActionResultType.SUCCESS;
        }
        player.openMenu(getMenuProvider(state, levelIn, pos));
        return ActionResultType.CONSUME;

    }

    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World levelIn, BlockPos pos)
    {
        return new SimpleNamedContainerProvider((id, inventory, player) -> new WeaponWorkbenchContainer(inventory, IWorldPosCallable.create(levelIn, pos), id), CONTAINER_NAME);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }
}
