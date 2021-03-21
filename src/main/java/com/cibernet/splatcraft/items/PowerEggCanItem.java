package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PowerEggCanItem extends Item {
    public PowerEggCanItem(String name) {
        super(new Properties().maxStackSize(16).group(SplatcraftItemGroups.GROUP_GENERAL));
        setRegistryName(name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            double d0 = playerIn.getPosYEye() - (double) 0.3F;
            ItemEntity itementity = new ItemEntity(worldIn, playerIn.getPosX(), d0, playerIn.getPosZ(), new ItemStack(SplatcraftItems.powerEgg, (worldIn.rand.nextInt(4) + 1) * 10));
            itementity.setPickupDelay(0);
            itementity.setThrowerId(playerIn.getUniqueID());

            float f = worldIn.rand.nextFloat() * 0.5F;
            float f1 = worldIn.rand.nextFloat() * ((float) Math.PI * 2F);
            itementity.setMotion(-MathHelper.sin(f1) * f, 0.2F, MathHelper.cos(f1) * f);

            worldIn.addEntity(itementity);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
}
