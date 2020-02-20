package com.cibernet.splatcraft.items;

import javax.annotation.Nullable;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import net.minecraft.inventory.EntityEquipmentSlot;
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
    protected double weaponSpeed;
    protected float flingSpeed;
    protected boolean isBrush;
    protected int rollRadius;
    protected float rollSpeed;

    public ItemRollerBase(String unlocName, String registryName, double weaponSpeed, float flingSpeed, float rollSpeed, int rollRadius, boolean isBrush)
    {
        super(unlocName, registryName);
        
        this.weaponSpeed = weaponSpeed;
        this.flingSpeed = flingSpeed;
        this.rollRadius = rollRadius;
        this.isBrush = isBrush;
        this.rollSpeed = rollSpeed;
        
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
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if(slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", getWeaponSpeed(stack), 0));
        }
        return multimap;
    }
    
    public double getWeaponSpeed(ItemStack stack)
    {
        return weaponSpeed;
    }
    
    @Override
    public void onItemTickUse(World worldIn, EntityPlayer playerIn, ItemStack stack, int useTime)
    {
        BlockPos pos = new BlockPos(playerIn.posX + 0.5, playerIn.posY, playerIn.posZ + 0.5);
        Vec3d fwd = Vec3d.fromPitchYawVector(new Vec2f(0, playerIn.rotationYaw));
        playerIn.getHorizontalFacing();

        for(int i = 0; i < rollRadius; i++) {
            double xOff = i == 0 ? 0 : (Math.ceil((playerIn.posX + fwd.x) - Math.ceil(playerIn.posX + fwd.x)) == 0 ? 1 : -1) * Math.floor(i/2);
            double zOff = i == 0 ? 0 : (Math.ceil((playerIn.posZ + fwd.z) - Math.ceil(playerIn.posZ + fwd.z)) == 0 ? -1 : 1) * Math.floor(i/2);

            if(i % 2 == 0)
            {
                xOff *= -1;
                zOff *= -1;
            }

            if (playerIn.getHorizontalFacing().equals(EnumFacing.NORTH) || playerIn.getHorizontalFacing().equals(EnumFacing.SOUTH))
                zOff = 0;
            else xOff = 0;

            BlockPos inkPos = pos.add(fwd.x * 2 + xOff, -1, fwd.z * 2 + zOff);

            if (worldIn.getBlockState(inkPos.up()).getBlock() != Blocks.AIR)
                inkPos = inkPos.up();

            SplatCraftUtils.inkBlock(worldIn, inkPos, ItemWeaponBase.getInkColor(stack));

            System.out.print(Math.ceil(i/2) + " ");
        }
        System.out.println();
    }

    @Override
    public float getUseWalkSpeed() {
        return rollSpeed;
    }

    @Override
    public void onItemLeftClick(World worldIn, EntityPlayer playerIn, ItemStack stack)
    {
        if(playerIn.getCooledAttackStrength(0) >= 1f)
        {

            for(int i = -1; i <= 1; i++)
            {
                EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, getInkColor(stack));
                proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw + ((!isBrush) ? 0 : 20*i), isBrush ? 0 : 20*i, flingSpeed, 4f);
                proj.setProjectileSize(0.5f);
                worldIn.spawnEntity(proj);
            }
        }
    }
}