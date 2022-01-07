package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.items.ColoredBlockItem;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.HashMap;

public class InkwellTileEntity extends InkColorTileEntity implements ITickableTileEntity
{
    public static final HashMap<Item, ColoredBlockItem> inkCoatingRecipes = new HashMap<>();

    public InkwellTileEntity()
    {
        super(SplatcraftTileEntitites.inkwellTileEntity);
    }

    @Override
    public void tick()
    {
        AxisAlignedBB bb = new AxisAlignedBB(getBlockPos().above());

        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, bb))
        {
            ItemStack stack = entity.getItem();

            if(inkCoatingRecipes.keySet().contains(stack.getItem()))
                entity.setItem(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(inkCoatingRecipes.get(stack.getItem())), getColor()), true));
        }
    }
}
