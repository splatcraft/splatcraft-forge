package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
            EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, getInkColor(stack), damage);
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

        if(!worldIn.isRemote && useTime % firingSpeed == 0 && automatic) {
            EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, getInkColor(stack), damage);
            proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, projectileSpeed, inaccuracy);
            proj.setProjectileSize(projectileSize);
            worldIn.spawnEntity(proj);
        }
    }
    
    @Override
    public ModelPlayerOverride.EnumAnimType getAnimType()
    {
        return ModelPlayerOverride.EnumAnimType.SHOOTER;
    }
}
