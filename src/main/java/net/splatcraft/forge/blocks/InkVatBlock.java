package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.InkVatTileEntity;
import org.jetbrains.annotations.Nullable;

public class InkVatBlock extends ContainerBlock implements IColoredBlock
{

    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public InkVatBlock(String name)
    {
        super(Properties.of(Material.METAL).strength(2.0f).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
        setRegistryName(name);
        SplatcraftBlocks.inkColoredBlocks.add(this);

        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, ACTIVE, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public ActionResultType use(BlockState state, World levelIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (levelIn.isClientSide)
        {
            return ActionResultType.SUCCESS;
        }

        if (levelIn.getBlockEntity(pos) instanceof InkVatTileEntity)
        {
            NetworkHooks.openGui((ServerPlayerEntity) player, (InkVatTileEntity) levelIn.getBlockEntity(pos), pos);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
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
        return SplatcraftTileEntitites.inkVatTileEntity.create();
    }

    @Override
    public boolean canClimb()
    {
        return false;
    }

    @Override
    public boolean canSwim()
    {
        return false;
    }

    @Override
    public boolean canDamage()
    {
        return false;
    }

    @Override
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {
        return false;
    }

    @Override
    public boolean remoteInkClear(World level, BlockPos pos)
    {
        return false;
    }

    @Override
    public int getColor(World level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof InkVatTileEntity)
        {
            InkVatTileEntity tileEntity = (InkVatTileEntity) level.getBlockEntity(pos);
            if (tileEntity != null)
            {
                return tileEntity.getColor();
            }
        }
        return -1;
    }

    @Override
    public boolean setColor(World level, BlockPos pos, int color)
    {
        if (!(level.getBlockEntity(pos) instanceof InkVatTileEntity))
        {
            return false;
        }
        InkVatTileEntity tileEntity = (InkVatTileEntity) level.getBlockEntity(pos);
        if (tileEntity != null)
        {
            tileEntity.setColor(color);
        }
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 2);
        return true;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader levelIn)
    {
        return new InkVatTileEntity();
    }



    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(World levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (stack.hasCustomHoverName())
        {
            TileEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof InkVatTileEntity)
            {
                ((InkVatTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }

    }

    @Override
    public void onRemove(BlockState state, World levelIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            TileEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof InkVatTileEntity)
            {
                InventoryHelper.dropContents(levelIn, pos, (InkVatTileEntity) tileentity);
                levelIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, levelIn, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World levelIn, BlockPos pos)
    {
        return Container.getRedstoneSignalFromBlockEntity(levelIn.getBlockEntity(pos));
    }

    @Override
    public void neighborChanged(BlockState state, World levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        boolean isPowered = levelIn.hasNeighborSignal(pos);
        if (isPowered != state.getValue(POWERED))
        {
            if (isPowered && levelIn.getBlockEntity(pos) instanceof InkVatTileEntity)
            {
                InkVatTileEntity tileEntity = (InkVatTileEntity) levelIn.getBlockEntity(pos);
                if (tileEntity != null)
                {
                    tileEntity.onRedstonePulse();
                }
            }

            levelIn.setBlock(pos, state.setValue(POWERED, isPowered), 3);
        }

    }
}
