package net.splatcraft.forge.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.IColoredItem;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.RemotePedestalTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.CommonUtils;
import org.jetbrains.annotations.Nullable;

public class RemotePedestalBlock extends Block implements IColoredBlock, EntityBlock
{

    private static final VoxelShape SHAPE = Shapes.or(
      box(3, 0, 3, 13, 2, 13),
      box(4, 2, 4, 12, 3, 12),
      box(5, 3, 5, 11, 11, 11),
      box(4, 11, 4, 12, 13, 12)
    );

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RemotePedestalBlock()
    {
        super(Properties.of(Material.METAL).strength(2.0f).requiresCorrectToolForDrops());
        SplatcraftBlocks.inkColoredBlocks.add(this);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getCloneItemStack(state, target, level, pos, player), getColor((Level) level, pos)), true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> container) {
        container.add(POWERED);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTrace)
    {

        if(level.getBlockEntity(pos) instanceof RemotePedestalTileEntity)
        {
            RemotePedestalTileEntity te = (RemotePedestalTileEntity) level.getBlockEntity(pos);
            if(te.isEmpty() && player.getItemInHand(hand).is(SplatcraftTags.Items.REMOTES))
            {
                te.setItem(0, player.getItemInHand(hand).copy());
                player.getItemInHand(hand).setCount(0);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(!te.isEmpty())
            {
                ItemStack remote = te.removeItemNoUpdate(0);
                if(!player.addItem(remote))
                    CommonUtils.spawnItem(level, pos.above(), remote);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.use(state, level, pos, player, hand, rayTrace);
    }


    @Override
    public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        boolean isPowered = levelIn.hasNeighborSignal(pos);

        if (isPowered != state.getValue(POWERED))
        {
            if (isPowered && levelIn.getBlockEntity(pos) instanceof RemotePedestalTileEntity)
            {
                RemotePedestalTileEntity tileEntity = (RemotePedestalTileEntity) levelIn.getBlockEntity(pos);
                if (tileEntity != null)
                    tileEntity.onPowered();
            }

            levelIn.setBlock(pos, state.setValue(POWERED, isPowered), 3);
            updateColor(levelIn, pos, pos.below());
        }

    }

    @Override
    public void onPlace(BlockState p_220082_1_, Level level, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_)
    {
        super.onPlace(p_220082_1_, level, pos, p_220082_4_, p_220082_5_);
        updateColor(level, pos, pos.below());
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos)
    {

        if(facing.equals(Direction.DOWN) && levelIn instanceof Level)
            updateColor((Level) levelIn, currentPos, facingPos);
        return stateIn;
    }

    public void updateColor(Level levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(levelIn.getBlockState(facingPos).getBlock() instanceof InkwellBlock)
            setColor(levelIn, currentPos, ((InkwellBlock) levelIn.getBlockState(facingPos).getBlock()).getColor(levelIn, facingPos));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        if(state.getValue(POWERED) && level.getBlockEntity(pos) instanceof RemotePedestalTileEntity)
            return ((RemotePedestalTileEntity) level.getBlockEntity(pos)).getSignal();

        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction side) {
        return true;
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
        RemotePedestalTileEntity te = (RemotePedestalTileEntity) level.getBlockEntity(pos);
        if(!te.isEmpty() && te.getItem(0).getItem() instanceof IColoredItem)
            return ColorUtils.getInkColor(te.getItem(0)) != newColor;
        return false;
    }

    @Override
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {
        if(level.getBlockEntity(pos) instanceof RemotePedestalTileEntity)
        {
            RemotePedestalTileEntity te = (RemotePedestalTileEntity) level.getBlockEntity(pos);
            if(!te.isEmpty() && te.getItem(0).getItem() instanceof IColoredItem)
            {
                ItemStack stack = te.getItem(0);
                ColorUtils.setColorLocked(stack, true);
                if (ColorUtils.getInkColor(stack) != newColor) {
                    ColorUtils.setInkColor(stack, newColor);
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean setColor(Level level, BlockPos pos, int color)
    {
        if (!(level.getBlockEntity(pos) instanceof RemotePedestalTileEntity))
            return false;
        RemotePedestalTileEntity tileEntity = (RemotePedestalTileEntity) level.getBlockEntity(pos);
        if (tileEntity != null)
            tileEntity.setColor(color);
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 2);
        return true;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos) {
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            if (levelIn.getBlockEntity(pos) instanceof RemotePedestalTileEntity pedestal)
            {
                Containers.dropContents(levelIn, pos, pedestal);
                levelIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, levelIn, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftTileEntities.remotePedestalTileEntity.get().create(pos, state);
    }
}
