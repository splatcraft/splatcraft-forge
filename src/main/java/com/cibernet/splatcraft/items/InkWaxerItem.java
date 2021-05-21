package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InkWaxerItem extends Item
{
    public InkWaxerItem()
    {
        super(new Properties().maxDamage(256).group(SplatcraftItemGroups.GROUP_GENERAL));
    }

    public void onBlockStartBreak(ItemStack itemstack, BlockPos pos, World world)
    {
        if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
        {
            InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
            te.setPermanentColor(-1);

            world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));

            if(world.getBlockState(pos).getBlock() instanceof IColoredBlock)
                ((IColoredBlock) world.getBlockState(pos).getBlock()).remoteInkClear(world, pos);
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        if(context.getWorld().getTileEntity(context.getPos()) instanceof InkedBlockTileEntity)
        {
            InkedBlockTileEntity te = (InkedBlockTileEntity) context.getWorld().getTileEntity(context.getPos());

            if(te.getPermanentColor() != te.getColor())
            {
                te.setPermanentColor(te.getColor());
                te.setPermanentInkType(InkBlockUtils.getInkType(context.getWorld().getBlockState(context.getPos())));

                context.getWorld().playEvent(2005, context.getPos(), 0);
                if(context.getPlayer() instanceof ServerPlayerEntity && !context.getPlayer().isCreative())
                    context.getItem().attemptDamageItem(1, context.getWorld().rand, (ServerPlayerEntity) context.getPlayer());
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUse(context);
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem().equals(Items.HONEYCOMB) || super.getIsRepairable(toRepair, repair);
    }
}
