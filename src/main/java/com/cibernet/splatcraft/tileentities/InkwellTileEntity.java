package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class InkwellTileEntity extends InkColorTileEntity implements ITickableTileEntity {

    public InkwellTileEntity() {
        super(SplatcraftTileEntitites.inkwellTileEntity);
    }

    @Override
    public void tick() {
        AxisAlignedBB bb = new AxisAlignedBB(getPos().up());

        for (ItemEntity entity : world.getEntitiesWithinAABB(ItemEntity.class, bb)) {
            ItemStack stack = entity.getItem();
            if (stack.getItem().equals(Items.WHITE_WOOL) || stack.getItem().equals(SplatcraftItems.inkedWool) && ColorUtils.getInkColor(stack) != getColor())
                entity.setItem(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkedWool, stack.getCount(), stack.getTag()), getColor()));
        }
    }
}
