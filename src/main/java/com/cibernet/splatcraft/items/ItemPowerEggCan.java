package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatCraftItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Random;

public class ItemPowerEggCan extends Item
{
    public ItemPowerEggCan()
    {
        setUnlocalizedName("powerEggCan");
        setRegistryName("power_egg_can");
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        Random rand = new Random();
        ItemStack eggs = new ItemStack(SplatCraftItems.powerEgg, (rand.nextInt(3)+10)*10);
        if(worldIn.isRemote)
        {
            if(playerIn.addItemStackToInventory(eggs))
            {
                EntityItem entity = playerIn.dropItem(eggs, false);
                if(entity != null)
                    entity.setNoPickupDelay();
            }
            stack.shrink(1);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
