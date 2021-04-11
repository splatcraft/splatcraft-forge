package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.items.IColoredItem;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.RemotePedestalTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
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

import javax.annotation.Nullable;

public class RemotePedestalBlock extends Block implements IColoredBlock
{

    private static final VoxelShape SHAPE = VoxelShapes.or(
      makeCuboidShape(3, 0, 3, 13, 2, 13),
      makeCuboidShape(4, 2, 4, 12, 3, 12),
      makeCuboidShape(5, 3, 5, 11, 11, 11),
      makeCuboidShape(4, 11, 4, 12, 13, 12)
    );

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RemotePedestalBlock()
    {
        super(Properties.create(Material.IRON).hardnessAndResistance(2.0f).harvestTool(ToolType.PICKAXE).setRequiresTool());
        SplatcraftBlocks.inkColoredBlocks.add(this);
        setDefaultState(getDefaultState().with(POWERED, false));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, world, pos, player), getColor((World) world, pos)), true);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container) {
        container.add(POWERED);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace)
    {

        if(world.getTileEntity(pos) instanceof RemotePedestalTileEntity)
        {
            RemotePedestalTileEntity te = (RemotePedestalTileEntity) world.getTileEntity(pos);
            if(te.isEmpty() && player.getHeldItem(hand).getItem().isIn(SplatcraftTags.Items.REMOTES))
            {
                te.setInventorySlotContents(0, player.getHeldItem(hand).copy());
                player.getHeldItem(hand).setCount(0);
                return ActionResultType.func_233537_a_(world.isRemote);
            }
            else if(!te.isEmpty())
            {
                ItemStack remote = te.removeStackFromSlot(0);
                if(!player.addItemStackToInventory(remote))
                    spawnAsEntity(world, pos.up(), remote);
                return ActionResultType.func_233537_a_(world.isRemote);
            }
        }

        return super.onBlockActivated(state, world, pos, player, hand, rayTrace);
    }


    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        boolean isPowered = worldIn.isBlockPowered(pos);

        if (isPowered != state.get(POWERED))
        {
            if (isPowered && worldIn.getTileEntity(pos) instanceof RemotePedestalTileEntity)
            {
                RemotePedestalTileEntity tileEntity = (RemotePedestalTileEntity) worldIn.getTileEntity(pos);
                if (tileEntity != null)
                    tileEntity.onPowered();
            }

            worldIn.setBlockState(pos, state.with(POWERED, isPowered), 3);
            updateColor(worldIn, pos, pos.down());
        }

    }

    @Override
    public void onBlockAdded(BlockState p_220082_1_, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_)
    {
        super.onBlockAdded(p_220082_1_, world, pos, p_220082_4_, p_220082_5_);
        updateColor(world, pos, pos.down());
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {

        if(facing.equals(Direction.DOWN) && worldIn instanceof World)
            updateColor((World) worldIn, currentPos, facingPos);
        return stateIn;
    }

    public void updateColor(World worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if(worldIn.getBlockState(facingPos).getBlock() instanceof InkwellBlock)
            setColor(worldIn, currentPos, ((InkwellBlock) worldIn.getBlockState(facingPos).getBlock()).getColor(worldIn, facingPos));
    }
    @Override
    public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
    {
        if(state.get(POWERED) && world.getTileEntity(pos) instanceof RemotePedestalTileEntity)
            return ((RemotePedestalTileEntity) world.getTileEntity(pos)).getSignal();

        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
    public boolean remoteColorChange(World world, BlockPos pos, int newColor)
    {
        if(world.getTileEntity(pos) instanceof RemotePedestalTileEntity)
        {
            RemotePedestalTileEntity te = (RemotePedestalTileEntity) world.getTileEntity(pos);
            if(!te.isEmpty() && te.getStackInSlot(0).getItem() instanceof IColoredItem)
            {
                ItemStack stack = te.getStackInSlot(0);
                ColorUtils.setColorLocked(stack, true);
                if (ColorUtils.getInkColor(stack) != newColor) {
                    ColorUtils.setInkColor(stack, newColor);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean setColor(World world, BlockPos pos, int color)
    {
        if (!(world.getTileEntity(pos) instanceof RemotePedestalTileEntity))
            return false;
        RemotePedestalTileEntity tileEntity = (RemotePedestalTileEntity) world.getTileEntity(pos);
        if (tileEntity != null)
            tileEntity.setColor(color);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        return true;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos) {
        return false;
    }


    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.isIn(newState.getBlock()))
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof RemotePedestalTileEntity)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (RemotePedestalTileEntity) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }
}
