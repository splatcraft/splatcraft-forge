package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCanvas extends BlockInkColor {
    public BlockCanvas()
    {
        super(Material.CLOTH);
        setUnlocalizedName("canvas");
        setRegistryName("canvas");
        setCreativeTab(TabSplatCraft.main);

        defaultColor = 0xFFFFFF;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if(worldIn.getBlockState(fromPos).getMaterial().equals(Material.WATER))
            if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
            {
                TileEntityColor te = (TileEntityColor) worldIn.getTileEntity(pos);
                te.setColor(this.defaultColor);
            }
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
            return super.getMapColor(state, worldIn, pos);

        TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
        InkColors color = InkColors.getByColor(te.getColor());
        if(color == null)
            return super.getMapColor(state, worldIn, pos);
        else return color.getMapColor();

    }

}
