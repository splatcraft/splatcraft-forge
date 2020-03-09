package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.blocks.BlockInkwell;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemInkwell extends ItemBlock
{
    public ItemInkwell() {
        super(SplatCraftBlocks.inkwell);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.COMBAT);
        setRegistryName(SplatCraftBlocks.inkwell.getRegistryName());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        if(world.getTileEntity(pos) instanceof TileEntityColor)
        {
            TileEntityColor te = (TileEntityColor) world.getTileEntity(pos);
            te.setColor(BlockInkwell.getInkColor(stack));
        }

        return true;
    }
}
