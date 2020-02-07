package com.cibernet.splatcraft.items;

import javax.annotation.Nullable;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRollerBase extends ItemWeaponBase
{
    public ItemRollerBase(String unlocName, String registryName)
    {
        super(unlocName, registryName);
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.addPropertyOverride(new ResourceLocation("unfolded"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
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
        return new ActionResult(EnumActionResult.SUCCESS, stack);

    }



    @Override
    public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
    {
        BlockPos pos = new BlockPos(playerIn.posX + 0.5, playerIn.posY, playerIn.posZ + 0.5);
        Vec3d fwd = Vec3d.fromPitchYawVector(new Vec2f(0, playerIn.rotationYaw));
        playerIn.getHorizontalFacing();

        double xOff = Math.floor((playerIn.posX + fwd.x) - Math.floor(playerIn.posX + fwd.x)) == 0 ? -1 : 1;
        double zOff = Math.floor((playerIn.posZ + fwd.z) - Math.floor(playerIn.posZ + fwd.z)) == 0 ? -1 : 1;

        if(playerIn.getHorizontalFacing().equals(EnumFacing.NORTH) || playerIn.getHorizontalFacing().equals(EnumFacing.SOUTH))
            zOff = 0;
        else xOff = 0;

        BlockPos inkPosA = pos.add(fwd.x * 2, -1, fwd.z * 2);
        BlockPos inkPosB = pos.add(fwd.x * 2 +xOff, -1, fwd.z * 2 +zOff);

        if(worldIn.getBlockState(inkPosA.up()).getBlock() != Blocks.AIR)
            inkPosA = inkPosA.up();
        if(worldIn.getBlockState(inkPosB.up()).getBlock() != Blocks.AIR)
            inkPosB = inkPosB.up();

        SplatCraftUtils.inkBlock(worldIn, inkPosA, ItemWeaponBase.getInkColor(stack));
        SplatCraftUtils.inkBlock(worldIn, inkPosB, ItemWeaponBase.getInkColor(stack));
    }

    @Override
    public float getUseWalkSpeed() {
        return 0.4f;
    }

    @Override
    public void onItemLeftClick(World worldIn, EntityPlayer playerIn, ItemStack stack)
    {
        if(playerIn.getCooledAttackStrength(0) >= 1f)
        {

            for(int i = -1; i <= 1; i++) {
                EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, getInkColor(stack));
                proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 20*i, 0.6f, 4f);
                proj.setProjectileSize(0.5f);
                worldIn.spawnEntity(proj);
            }
        }
    }
}