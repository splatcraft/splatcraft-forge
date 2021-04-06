package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InkCoatedBlock extends Block implements IColoredBlock
{
    public InkCoatedBlock(String name, Properties properties)
    {
        super(properties);
        SplatcraftBlocks.inkColoredBlocks.add(this);
        setRegistryName(name);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return ColorUtils.setColorLocked(ColorUtils.setInkColor(super.getPickBlock(state, target, world, pos, player), getColor((World) world, pos)), true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack)
    {
        if (!world.isRemote && stack.getTag() != null && world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            ColorUtils.setInkColor(world.getTileEntity(pos), ColorUtils.getInkColor(stack));
        }
        super.onBlockPlacedBy(world, pos, state, entity, stack);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return SplatcraftTileEntitites.colorTileEntity.create();
    }

    @Override
    public ItemStack getItem(IBlockReader reader, BlockPos pos, BlockState state)
    {
        ItemStack stack = super.getItem(reader, pos, state);

        if (reader.getTileEntity(pos) instanceof InkColorTileEntity)
            ColorUtils.setColorLocked(ColorUtils.setInkColor(stack, ColorUtils.getInkColor(reader.getTileEntity(pos))), true);

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
    public int getColor(World world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(World world, BlockPos pos, int newColor)
    {
        BlockState state = world.getBlockState(pos);
        if (world.getTileEntity(pos) instanceof InkColorTileEntity && ((InkColorTileEntity) world.getTileEntity(pos)).getColor() != newColor)
        {
            ((InkColorTileEntity) world.getTileEntity(pos)).setColor(newColor);
            world.notifyBlockUpdate(pos, state, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (InkedBlock.isTouchingLiquid(world, pos))
        {
            return false;
        }

        int woolColor = -1;

        if (world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            woolColor = ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
        }

        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, SplatcraftBlocks.inkedBlock.getDefaultState(), 3);
        world.setTileEntity(pos, SplatcraftBlocks.inkedBlock.createTileEntity(SplatcraftBlocks.inkedBlock.getDefaultState(), world));
        InkedBlockTileEntity inkte = (InkedBlockTileEntity) world.getTileEntity(pos);
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
    public boolean remoteInkClear(World world, BlockPos pos)
    {
        return false;
    }
}
