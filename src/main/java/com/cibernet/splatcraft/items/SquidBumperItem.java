package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.registries.SplatcraftEntities;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class SquidBumperItem extends Item
{
	public SquidBumperItem(String name)
	{
		super(new Properties().maxStackSize(16).group(SplatcraftItemGroups.GROUP_GENERAL));
		SplatcraftItems.inkColoredItems.add(this);
		setRegistryName(name);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		
		if(ColorUtils.isColorLocked(stack))
			tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		super.inventoryTick(stack, world, entity, itemSlot, isSelected);
		
		if(entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != ColorUtils.getPlayerColor((PlayerEntity) entity)
				&& PlayerInfoCapability.hasCapability((LivingEntity) entity))
			ColorUtils.setInkColor(stack, ColorUtils.getPlayerColor((PlayerEntity) entity));
	}
	
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		BlockPos pos = entity.getPosition().down();
		
		if(entity.world.getBlockState(pos).getBlock() instanceof InkwellBlock)
		{
			InkColorTileEntity te = (InkColorTileEntity) entity.world.getTileEntity(pos);
			
			if(ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
			{
				ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
				ColorUtils.setColorLocked(entity.getItem(), true);
			}
		}
		
		return false;
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		if(context.getFace() == Direction.DOWN)
			return ActionResultType.FAIL;
		
		World world = context.getWorld();
		BlockPos pos = new BlockItemUseContext(context).getPos();
		ItemStack stack = context.getItem();
		
		SquidBumperEntity bumper = SplatcraftEntities.SQUID_BUMPER.create((ServerWorld) world, stack.getTag(), (ITextComponent)null, context.getPlayer(), pos, SpawnReason.SPAWN_EGG, true, true);
		bumper.setColor(ColorUtils.getInkColor(stack));
		
		if(world.hasNoCollisions(bumper) && world.getEntitiesWithinAABBExcludingEntity(bumper, bumper.getBoundingBox()).isEmpty())
		{
			if(!world.isRemote)
			{
				float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlacementYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
				bumper.setPosition(bumper.getPosX()+0.5, bumper.getPosY(), bumper.getPosZ()+0.5);
				bumper.setRotationYawHead(f);
				
				world.addEntity(bumper);
				world.playSound(null, bumper.getPosX(), bumper.getPosY(), bumper.getPosZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
			}
			stack.shrink(1);
			return ActionResultType.func_233537_a_(world.isRemote);
		}
		
		return ActionResultType.FAIL;
	}
}
