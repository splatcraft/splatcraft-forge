package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.subs.AbstractSubWeaponEntity;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class SubWeaponItem extends WeaponBaseItem
{
    public float inkConsumption;
    public final EntityType<? extends AbstractSubWeaponEntity> entityType;

    public SubWeaponItem(String name, EntityType<? extends AbstractSubWeaponEntity> entityType, float inkConsumption)
    {
        super();
        setRegistryName(name);
        this.inkConsumption = inkConsumption;
        this.entityType = entityType;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {

        if(!(player.isActualySwimming() && !player.isInWater()))
        {
            if(getInkAmount(player, player.getHeldItem(hand)) >= inkConsumption)
                player.setActiveHand(hand);
            else sendNoInkMessage(player, SplatcraftSounds.noInkSub);
        }
        return onItemRightClickSuper(world, player, hand);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft)
    {
        super.onPlayerStoppedUsing(stack, world, entity, timeLeft);

        AbstractSubWeaponEntity proj = AbstractSubWeaponEntity.create(entityType, world, entity, stack);
        proj.shoot(entity, entity.rotationPitch, entity.rotationYaw, -30f, 0.5f, 0);
        world.addEntity(proj);
        world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SplatcraftSounds.subThrow, SoundCategory.PLAYERS, 0.7F, 1);
        reduceInk(entity, inkConsumption);
    }
}
