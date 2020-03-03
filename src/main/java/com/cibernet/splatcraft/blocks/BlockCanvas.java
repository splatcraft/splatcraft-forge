package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
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
        setHardness(0.8f);
        setSoundType(SoundType.CLOTH);
        defaultColor = 0xFFFFFF;
    }


    protected boolean tryTouchWater(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean touchingWater = false;

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (enumfacing != EnumFacing.DOWN)
            {
                BlockPos blockpos = pos.offset(enumfacing);

                if (worldIn.getBlockState(blockpos).getMaterial() == Material.WATER)
                {
                    touchingWater = true;
                    break;
                }
            }
        }

        if (touchingWater)
        {
            if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
            {
                TileEntityColor te = (TileEntityColor) worldIn.getTileEntity(pos);
                te.setColor(defaultColor);
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
        }

        return touchingWater;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if(worldIn.getBlockState(fromPos).getMaterial().equals(Material.WATER))
            if(worldIn.getTileEntity(pos) instanceof TileEntityColor)
            {
                TileEntityColor te = (TileEntityColor) worldIn.getTileEntity(pos);
                te.setColor(defaultColor);
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.tryTouchWater(worldIn, pos, state))
        {
            super.onBlockAdded(worldIn, pos, state);
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

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
    {
        if(player.getHeldItemMainhand().getItem() instanceof ItemShears)
            return 0.95f;
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }
}
