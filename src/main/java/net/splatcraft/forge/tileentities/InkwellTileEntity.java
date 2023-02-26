package net.splatcraft.forge.tileentities;

import net.splatcraft.forge.items.ColoredBlockItem;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.ColorUtils;
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
        super(SplatcraftTileEntities.inkwellTileEntity);
    }

    @Override
    public void tick()
    {
        AxisAlignedBB bb = new AxisAlignedBB(getBlockPos().above());

        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, bb))
        {
            ItemStack stack = entity.getItem();

            if (inkCoatingRecipes.containsKey(stack.getItem()))
                entity.setItem(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(inkCoatingRecipes.get(stack.getItem())), getColor()), true));
        }
    }
}
