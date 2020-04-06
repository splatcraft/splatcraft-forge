package com.cibernet.splatcraft.items;

import javax.annotation.Nullable;

import com.cibernet.splatcraft.entities.classes.EntityInkProjectile;
import com.cibernet.splatcraft.entities.models.ModelPlayerOverride;
import com.cibernet.splatcraft.utils.SplatCraftDamageSource;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
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
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemRollerBase extends ItemWeaponBase
{
    private final AttributeModifier SPEED_MODIFIER;

    protected double weaponSpeed;
    protected float flingSpeed;
    protected boolean isBrush;
    protected int rollRadius;
    protected float rollDamage;
    protected float flingDamage;

    public ItemRollerBase(String unlocName, String registryName, double weaponSpeed, float flingSpeed, float flingDamage, double rollSpeed, int rollRadius, float rollDamage, boolean isBrush)
    {
        super(unlocName, registryName);
        
        this.weaponSpeed = weaponSpeed;
        this.flingSpeed = flingSpeed;
        this.rollRadius = rollRadius;
        this.rollDamage = rollDamage;
        this.flingDamage = flingDamage;
        this.isBrush = isBrush;

        SPEED_MODIFIER = (new AttributeModifier( "Rolling speed boost", rollSpeed-1d, 2)).setSaved(false);
        
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
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        return false;
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
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        System.out.println(entityLiving.world.isRemote);
        return super.onEntitySwing(entityLiving, stack);
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
    
        if(worldIn.isRemote)
            return;
        
        BlockPos pos = new BlockPos(playerIn.posX + 0.5, playerIn.posY, playerIn.posZ + 0.5);
        Vec3d fwd = getFwd(0, playerIn.rotationYaw);
        playerIn.getHorizontalFacing();

        for(int i = 0; i < rollRadius; i++) {
            double xOff = i == 0 ? 0 : (Math.floor((playerIn.posX + fwd.x) - Math.floor(playerIn.posX + fwd.x)) == 0 ? 1 : -1) * Math.ceil(i/2f);
            double zOff = i == 0 ? 0 : (Math.floor((playerIn.posZ + fwd.z) - Math.floor(playerIn.posZ + fwd.z)) == 0 ? -1 : 1) * Math.ceil(i/2f);

            if(i % 2 == 0)
            {
                xOff *= -1;
                zOff *= -1;
            }
            
            if (playerIn.getHorizontalFacing().equals(EnumFacing.NORTH) || playerIn.getHorizontalFacing().equals(EnumFacing.SOUTH))
                zOff = 0;
            else xOff = 0;

            BlockPos checkPos = pos.add(fwd.x * 1 + xOff, 0, fwd.z * 1 + zOff);
            BlockPos inkPos = pos.add(fwd.x * 2 + xOff, -1, fwd.z * 2 + zOff);
            boolean canInk = true;
            
            
            if(!SplatCraftUtils.canInkPassthrough(worldIn, checkPos))
                inkPos = checkPos;
            else if (!SplatCraftUtils.canInkPassthrough(worldIn, inkPos.up()))
                inkPos = inkPos.up();
            canInk = SplatCraftUtils.canInk(worldIn, inkPos);
            
            if(canInk)
                SplatCraftUtils.inkBlock(worldIn, inkPos, ItemWeaponBase.getInkColor(stack), rollDamage);
            
            List<EntityPlayer> inkedPlayers = worldIn.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(inkPos.up()));
            for(EntityPlayer target : inkedPlayers)
            {
                if(SplatCraftPlayerData.getInkColor(target) != getInkColor(stack))
                    target.attackEntityFrom(new SplatCraftDamageSource("roll", playerIn, playerIn), rollDamage);
            }
            
        }
    }
    
    @Override
    public AttributeModifier getSpeedModifier() {
        return SPEED_MODIFIER;
    }

    @Override
    public void onItemLeftClick(World worldIn, EntityPlayer playerIn, ItemStack stack)
    {
        if(playerIn.getCooledAttackStrength(0) >= 0.95f)
        {

            for(int i = -1; i <= 1; i++)
            {
                EntityInkProjectile proj = new EntityInkProjectile(worldIn, playerIn, getInkColor(stack), flingDamage);
                proj.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw + ((!isBrush) ? 0 : 20*i), isBrush ? 0 : 20*i, flingSpeed, 4f);
                proj.setProjectileSize(0.5f);
                worldIn.spawnEntity(proj);
            }
        }
    }
    
    @Override
    public ModelPlayerOverride.EnumAnimType getAnimType()
    {
        return ModelPlayerOverride.EnumAnimType.ROLLER;
    }
    
    private Vec3d getFwd(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
}