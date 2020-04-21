package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.registries.SplatCraftSounds;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemShooterBase extends ItemWeaponBase
{

    public float projectileSize;
    public float inaccuracy;
    public float projectileSpeed;
    public int firingSpeed;
    public boolean automatic;
    public float damage;

    public ItemShooterBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage, boolean automatic)
    {
        super(unlocName, registryName);
        this.inaccuracy = inaccuracy;
        this.projectileSize = projectileSize;
        this.projectileSpeed = projectileSpeed;
        this.firingSpeed = firingSpeed;
        this.automatic = automatic;
        this.damage = damage;
    }

    public ItemShooterBase(String unlocName, String registryName, float projectileSize, float projectileSpeed, float inaccuracy, int firingSpeed, float damage)
    {
        this(unlocName, registryName, projectileSize, projectileSpeed, inaccuracy, firingSpeed, damage, true);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.PASS;
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if(playerIn.isSneaking())
            return super.onItemRightClick(worldIn, playerIn, handIn);

        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);

        if(!automatic && !worldIn.isRemote)
        {
            EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, ColorItemUtils.getInkColor(stack), damage);
            proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, inaccuracy);
            proj.setProjectileSize(projectileSize);
            worldIn.spawnEntity(proj);
            playerIn.getCooldownTracker().setCooldown(this, firingSpeed);
        }

        return new ActionResult(EnumActionResult.SUCCESS, stack);

    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return automatic ? super.getMaxItemUseDuration(stack) : 0;
    }

    @Override
    public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
    {
        if((getMaxItemUseDuration(stack)-useTime) % firingSpeed == 1 && automatic)
        {
            if(!worldIn.isRemote)
            {
                EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, ColorItemUtils.getInkColor(stack), damage);
                proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, inaccuracy);
                proj.setProjectileSize(projectileSize);
                worldIn.spawnEntity(proj);
            }
            //else worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SplatCraftSounds.shooterShot, SoundCategory.PLAYERS, 0.8F, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.1F + 1.0F) * 0.95F);
        }
    }
    
    @Override
    public ModelPlayerOverride.EnumAnimType getAnimType()
    {
        return ModelPlayerOverride.EnumAnimType.SHOOTER;
    }
}
