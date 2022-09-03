package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.IColoredItem;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.RemotePedestalTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.CommonUtils;
import org.jetbrains.annotations.Nullable;

public class RemotePedestalBlock extends Block implements IColoredBlock
{

    private static final VoxelShape SHAPE = VoxelShapes.or(
      box(3, 0, 3, 13, 2, 13),
      box(4, 2, 4, 12, 3, 12),
      box(5, 3, 5, 11, 11, 11),
      box(4, 11, 4, 12, 13, 12)
    );

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RemotePedestalBlock()
    {
        super(Properties.of(Material.METAL).strength(2.0f).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
        SplatcraftBlocks.inkColoredBlocks.add(this);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, level, pos, player), getColor((World) level, pos)), true);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) {
        container.add(POWERED);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace)
    {

        if(level.getBlockEntity(pos) instanceof RemotePedestalTileEntity)
        {
            RemotePedestalTileEntity te = (RemotePedestalTileEntity) level.getBlockEntity(pos);
            if(te.isEmpty() && player.getItemInHand(hand).getItem().is(SplatcraftTags.Items.REMOTES))
            {
                te.setItem(0, player.getItemInHand(hand).copy());
                player.getItemInHand(hand).setCount(0);
                return ActionResultType.sidedSuccess(level.isClientSide);
            }
            else if(!te.isEmpty())
            {
                ItemStack remote = te.removeItemNoUpdate(0);
                if(!player.addItem(remote))
                    CommonUtils.spawnItem(level, pos.above(), remote);
                return ActionResultType.sidedSuccess(level.isClientSide);
            }
        }

        return super.use(state, level, pos, player, hand, rayTrace);
    }


    @Override
    public void neighborChanged(BlockState state, World levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
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
    public void onPlace(BlockState p_220082_1_, World level, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_)
    {
        super.onPlace(p_220082_1_, level, pos, p_220082_4_, p_220082_5_);
        updateColor(level, pos, pos.below());
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
    {

        if(facing.equals(Direction.DOWN) && levelIn instanceof World)
            updateColor((World) levelIn, currentPos, facingPos);
        return stateIn;
    }

    public void updateColor(World levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(levelIn.getBlockState(facingPos).getBlock() instanceof InkwellBlock)
            setColor(levelIn, currentPos, ((InkwellBlock) levelIn.getBlockState(facingPos).getBlock()).getColor(levelIn, facingPos));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos)
    {
        if(state.getValue(POWERED) && level.getBlockEntity(pos) instanceof RemotePedestalTileEntity)
            return ((RemotePedestalTileEntity) level.getBlockEntity(pos)).getSignal();

        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader level, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return SplatcraftTileEntitites.remotePedestalTileEntity.create();
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
    public boolean canRemoteColorChange(World level, BlockPos pos, int color, int newColor)
    {
        RemotePedestalTileEntity te = (RemotePedestalTileEntity) level.getBlockEntity(pos);
        if(!te.isEmpty() && te.getItem(0).getItem() instanceof IColoredItem)
            return ColorUtils.getInkColor(te.getItem(0)) != newColor;
        return false;
    }

    @Override
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
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
    public boolean setColor(World level, BlockPos pos, int color)
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
    public boolean remoteInkClear(World level, BlockPos pos) {
        return false;
    }

    @Override
    public void onRemove(BlockState state, World levelIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            TileEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof RemotePedestalTileEntity)
            {
                InventoryHelper.dropContents(levelIn, pos, (RemotePedestalTileEntity) tileentity);
                levelIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, levelIn, pos, newState, isMoving);
        }
    }
}
