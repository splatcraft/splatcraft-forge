package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.TileEntitySunkenCrate;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockSunkenCrate extends BlockInkColor
{

    public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 4);

    public BlockSunkenCrate() {
        super(Material.WOOD);
        setHardness(2.0F);
        setResistance(5.0F);
        setSoundType(SoundType.WOOD);

        setUnlocalizedName("sunkenCrate");
        setRegistryName("sunken_crate");
        setCreativeTab(TabSplatCraft.main);

        setDefaultState(getDefaultState().withProperty(STATE, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STATE, meta % 4);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if(worldIn.getTileEntity(pos) instanceof TileEntitySunkenCrate)
        {
            TileEntitySunkenCrate te = (TileEntitySunkenCrate) worldIn.getTileEntity(pos);
            int hp = te.getState();
            return getDefaultState().withProperty(STATE, hp);
        }

        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySunkenCrate();
    }


    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}
