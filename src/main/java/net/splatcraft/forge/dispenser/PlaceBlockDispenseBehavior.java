package net.splatcraft.forge.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockDispenseBehavior extends OptionalDispenseBehavior
{
    @Override
    protected ItemStack execute(IBlockSource source, ItemStack stack)
    {
        this.setSuccess(false);
        Item item = stack.getItem();
        if (item instanceof BlockItem)
        {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.getPos().relative(direction);
            Direction direction1 = source.getLevel().isEmptyBlock(blockpos.below()) ? direction : Direction.UP;
            this.setSuccess(((BlockItem) item).place(new DirectionalPlaceContext(source.getLevel(), blockpos, direction, stack, direction1)).consumesAction());
        }

        return stack;
    }
}
