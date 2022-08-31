package net.splatcraft.forge.items;

import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.InkBlockUtils;
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
        super(new Properties().durability(256).tab(SplatcraftItemGroups.GROUP_GENERAL));
    }

    public void onBlockStartBreak(ItemStack itemstack, BlockPos pos, World level)
    {
        if(level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
            te.setPermanentColor(-1);

            level.globalLevelEvent(2001, pos, Block.getId(level.getBlockState(pos)));

            if(level.getBlockState(pos).getBlock() instanceof IColoredBlock)
                ((IColoredBlock) level.getBlockState(pos).getBlock()).remoteInkClear(level, pos);
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof InkedBlockTileEntity)
        {
            InkedBlockTileEntity te = (InkedBlockTileEntity) context.getLevel().getBlockEntity(context.getClickedPos());

            if(te.getPermanentColor() != te.getColor())
            {
                te.setPermanentColor(te.getColor());
                te.setPermanentInkType(InkBlockUtils.getInkType(context.getLevel().getBlockState(context.getClickedPos())));

                context.getLevel().globalLevelEvent(2005, context.getClickedPos(), 0);
                if(context.getPlayer() instanceof ServerPlayerEntity && !context.getPlayer().isCreative())
                    context.getItemInHand().hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
                return ActionResultType.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public boolean canAttackBlock(BlockState state, World levelIn, BlockPos pos, PlayerEntity player) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem().equals(Items.HONEYCOMB) || super.isValidRepairItem(toRepair, repair);
    }
}
