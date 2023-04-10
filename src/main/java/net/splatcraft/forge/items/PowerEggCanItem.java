package net.splatcraft.forge.items;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;

public class PowerEggCanItem extends Item
{
    public PowerEggCanItem()
    {
        super(new Properties().stacksTo(16).tab(SplatcraftItemGroups.GROUP_GENERAL));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        levelIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SplatcraftSounds.powerEggCanOpen, SoundSource.PLAYERS, 0.5F, 0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!levelIn.isClientSide)
        {
            double d0 = playerIn.getEyeY() - (double) 0.3F;
            ItemEntity itementity = new ItemEntity(levelIn, playerIn.getX(), d0, playerIn.getZ(), new ItemStack(SplatcraftItems.powerEgg.get(), (levelIn.random.nextInt(4) + 1) * 10));
            itementity.setNoPickUpDelay();
            itementity.setThrower(playerIn.getUUID());

            float f = levelIn.random.nextFloat() * 0.5F;
            float f1 = levelIn.random.nextFloat() * ((float) Math.PI * 2F);
            itementity.setDeltaMovement(-Math.sin(f1) * f, 0.2F, Math.cos(f1) * f);

            levelIn.addFreshEntity(itementity);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.isCreative())
        {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, levelIn.isClientSide());
    }
}
