package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkVatTileEntity;
import org.jetbrains.annotations.Nullable;

public class InkVatBlock extends BaseEntityBlock implements IColoredBlock
{

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public InkVatBlock(String name)
    {
        super(Properties.of(Material.METAL).strength(2.0f).requiresCorrectToolForDrops());
        SplatcraftBlocks.inkColoredBlocks.add(this);

        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, ACTIVE, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if (levelIn.isClientSide)
        {
            return InteractionResult.SUCCESS;
        }

        if (levelIn.getBlockEntity(pos) instanceof InkVatTileEntity)
        {
            NetworkHooks.openGui((ServerPlayer) player, (InkVatTileEntity) levelIn.getBlockEntity(pos), pos);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
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
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {
        return false;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos)
    {
        return false;
    }

    @Override
    public int getColor(Level level, BlockPos pos)
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
    public boolean setColor(Level level, BlockPos pos, int color)
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

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
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
    public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (stack.hasCustomHoverName())
        {
            BlockEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof InkVatTileEntity)
            {
                ((InkVatTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }

    }


    @Override
    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof InkVatTileEntity)
            {
                Containers.dropContents(levelIn, pos, (InkVatTileEntity) tileentity);
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
    public int getAnalogOutputSignal(BlockState blockState, Level levelIn, BlockPos pos)
    {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(levelIn.getBlockEntity(pos));
    }

    @Override
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftTileEntities.inkVatTileEntity.get().create(pos, state);
    }
}
