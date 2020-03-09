package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.gui.SplatCraftGuiHandler;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityInkwellVat;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockInkwellVat extends Block
{
	public BlockInkwellVat()
	{
		super(Material.ROCK);
		setUnlocalizedName("inkwellVat");
		setRegistryName("inkwell_vat");
		setCreativeTab(TabSplatCraft.main);
	}
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
			return true;
		}
		else
		{
			if(!worldIn.isRemote)
				playerIn.openGui(SplatCraft.instance, SplatCraftGuiHandler.INKWELL_VAT_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
			//playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION);
			return true;
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntityInkwellVat te = (TileEntityInkwellVat)worldIn.getTileEntity(pos);
		if(te != null)
			te.dropInventoryItems();
		super.breakBlock(worldIn, pos, state);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityInkwellVat();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
}
