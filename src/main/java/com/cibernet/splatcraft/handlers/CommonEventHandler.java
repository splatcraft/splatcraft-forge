package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler
{

	private static final AttributeModifier SPRINTING_SPEED_BOOST = (new AttributeModifier( "Sprinting speed boost", 2D, 2)).setSaved(false);


	public static final CommonEventHandler instance = new CommonEventHandler();
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event)
	{
		float speed = 0.1f;
		EntityPlayer player = event.player;
		
		//TODO move this to clientside
		if(SplatCraftKeyHandler.squidKey.isKeyDown())
		{
			SplatCraftUtils.setEntitySize(player, 0.6f, 0.6f);
			
			float f = event.player.rotationYaw * 0.017453292F;
			
			speed = player.getEntityWorld().getBlockState(new BlockPos(player.posX, player.posY-1,player.posZ)).getBlock().equals(SplatCraftBlocks.inkedBlock) ? 0.2f : 0.05f;
			
			
		}
		
		//System.out.println();
		
		
		if(player.getItemInUseCount() > 0)
		{
			ItemStack weapon = player.getActiveItemStack();
			
			BlockPos pos = new BlockPos(player.posX + 0.5, player.posY, player.posZ + 0.5);
			
			
			Vec3d fwd = Vec3d.fromPitchYawVector(new Vec2f(0, player.rotationYaw));
			
			
			if(weapon.getItem().equals(SplatCraftItems.splatRoller))
			{
				player.getHorizontalFacing();
				
				double xOff = Math.floor((player.posX + fwd.x) - Math.floor(player.posX + fwd.x)) == 0 ? -1 : 1;
				double zOff = Math.floor((player.posZ + fwd.z) - Math.floor(player.posZ + fwd.z)) == 0 ? -1 : 1;
				
				if(player.getHorizontalFacing().equals(EnumFacing.NORTH) || player.getHorizontalFacing().equals(EnumFacing.SOUTH))
					zOff = 0;
				else xOff = 0;
				
				BlockPos inkPosA = pos.add(fwd.x * 2, -1, fwd.z * 2);
				BlockPos inkPosB = pos.add(fwd.x * 2 +xOff, -1, fwd.z * 2 +zOff);
				
				if(player.world.getBlockState(inkPosA.up()).getBlock() != Blocks.AIR)
					inkPosA = inkPosA.up();
				if(player.world.getBlockState(inkPosB.up()).getBlock() != Blocks.AIR)
					inkPosB = inkPosB.up();
				
				speed = 0.4f;
				
				SplatCraftUtils.inkBlock(player.world, inkPosA, ItemWeaponBase.getInkColor(weapon));
				SplatCraftUtils.inkBlock(player.world, inkPosB, ItemWeaponBase.getInkColor(weapon));
			}
		}
		
		//if(player.world.isRemote)
			player.capabilities.setPlayerWalkSpeed(speed);
	}
	
	
	
}
