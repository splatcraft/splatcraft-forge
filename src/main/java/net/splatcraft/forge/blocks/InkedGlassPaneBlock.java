package net.splatcraft.forge.blocks;

import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InkedGlassPaneBlock extends PaneBlock  implements IColoredBlock, IWaterLoggable
{

    public InkedGlassPaneBlock(String name)
    {
        super(AbstractBlock.Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
        SplatcraftBlocks.inkColoredBlocks.add(this);
        setRegistryName(name);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(BlockState state, IWorldReader level, BlockPos pos, BlockPos beaconPos)
    {
        return ColorUtils.hexToRGB(getColor((World) level, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, level, pos, player), getColor((World) level, pos)), true);
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        if (!level.isClientSide && stack.getTag() != null && level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            ColorUtils.setInkColor(level.getBlockEntity(pos), ColorUtils.getInkColor(stack));
        }
        super.setPlacedBy(level, pos, state, entity, stack);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return super.getStateForPlacement(context).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
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
        return SplatcraftTileEntitites.colorTileEntity.create();
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state)
    {
        ItemStack stack = super.getCloneItemStack(reader, pos, state);

        if (reader.getBlockEntity(pos) instanceof InkColorTileEntity)
            ColorUtils.setColorLocked(ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getBlockEntity(pos))), true);

        return stack;
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
    public int getColor(World level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {
        BlockState state = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof InkColorTileEntity && ((InkColorTileEntity) level.getBlockEntity(pos)).getColor() != newColor)
        {
            ((InkColorTileEntity) level.getBlockEntity(pos)).setColor(newColor);
            level.sendBlockUpdated(pos, state, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean inkBlock(World level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (InkedBlock.isTouchingLiquid(level, pos))
        {
            return false;
        }

        int woolColor = -1;

        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            woolColor = ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }
        BlockState state = level.getBlockState(pos);
        BlockState inkState = InkBlockUtils.getInkState(inkType, level, pos);
        level.setBlock(pos, inkState, 3);
        level.setBlockEntity(pos, SplatcraftBlocks.inkedBlock.createTileEntity(inkState, level));
        InkedBlockTileEntity inkte = (InkedBlockTileEntity) level.getBlockEntity(pos);
        if (inkte == null)
        {
            return false;
        }
        inkte.setColor(color);
        inkte.setSavedState(state);
        inkte.setSavedColor(woolColor);

        return true;
    }

    @Override
    public boolean remoteInkClear(World level, BlockPos pos)
    {
        return false;
    }
}
