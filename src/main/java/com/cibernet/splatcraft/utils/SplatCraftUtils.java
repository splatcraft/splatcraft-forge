package com.cibernet.splatcraft.utils;

import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SplatCraftUtils
{

    public static boolean inkBlock(World worldIn, BlockPos pos, int color)
    {

        IBlockState state = worldIn.getBlockState(pos);

        if(!state.isFullBlock() || state.isTranslucent() || state.getBlockHardness(worldIn, pos) == -1)
            return false;

        if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
        {
            TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
            te.setColor(color);
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return true;
        }

        if(!(worldIn.getTileEntity(pos) == null))
            return false;

        worldIn.setBlockState(pos, SplatCraftBlocks.inkedBlock.getDefaultState());
        TileEntityInkedBlock te = (TileEntityInkedBlock) SplatCraftBlocks.inkedBlock.createTileEntity(worldIn, SplatCraftBlocks.inkedBlock.getDefaultState());

        worldIn.setTileEntity(pos, te);

        te.setColor(color);
        te.setSavedState(state);

        return true;
    }
}
