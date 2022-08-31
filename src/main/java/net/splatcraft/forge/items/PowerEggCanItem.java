package net.splatcraft.forge.items;

import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PowerEggCanItem extends Item
{
    public PowerEggCanItem(String name)
    {
        super(new Properties().stacksTo(16).tab(SplatcraftItemGroups.GROUP_GENERAL));
        setRegistryName(name);
    }

    @Override
    public ActionResult<ItemStack> use(World levelIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        levelIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SplatcraftSounds.powerEggCanOpen, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!levelIn.isClientSide)
        {
            double d0 = playerIn.getEyeY() - (double) 0.3F;
            ItemEntity itementity = new ItemEntity(levelIn, playerIn.getX(), d0, playerIn.getZ(), new ItemStack(SplatcraftItems.powerEgg, (levelIn.random.nextInt(4) + 1) * 10));
            itementity.setNoPickUpDelay();
            itementity.setThrower(playerIn.getUUID());

            float f = levelIn.random.nextFloat() * 0.5F;
            float f1 = levelIn.random.nextFloat() * ((float) Math.PI * 2F);
            itementity.setDeltaMovement(-MathHelper.sin(f1) * f, 0.2F, MathHelper.cos(f1) * f);

            levelIn.addFreshEntity(itementity);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.isCreative())
        {
            itemstack.shrink(1);
        }

        return ActionResult.sidedSuccess(itemstack, levelIn.isClientSide());
    }
}
