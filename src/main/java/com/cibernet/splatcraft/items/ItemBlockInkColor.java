package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.blocks.BlockInkwell;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockInkColor extends ItemBlock
{
    public static final List<Item> itemList = new ArrayList<>();
    
    public ItemBlockInkColor(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
        itemList.add(this);
    }
    
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        
        if(entityIn instanceof EntityPlayer && !ColorItemUtils.hasInkColor(stack))
            ColorItemUtils.setInkColor(stack, SplatCraftPlayerData.getInkColor((EntityPlayer) entityIn));
    }
    
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState))
            return false;

        if(world.getTileEntity(pos) instanceof TileEntityColor)
        {
            TileEntityColor te = (TileEntityColor) world.getTileEntity(pos);
            te.setColor(ColorItemUtils.getInkColor(stack));
        }

        return true;
    }
}
