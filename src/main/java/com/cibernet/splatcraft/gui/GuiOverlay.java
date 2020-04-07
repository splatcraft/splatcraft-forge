package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.items.ICharge;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOverlay extends Gui
{
	public static final GuiOverlay instance = new GuiOverlay();
	private final ResourceLocation CHARGER_OVERLAY = new ResourceLocation(SplatCraft.MODID, "textures/gui/charger_overlay.png");
	
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();
			EntityPlayer player = mc.player;
			ItemStack stack = player.getHeldItemMainhand();
			int charge = (int) Math.floor(SplatCraftPlayerData.getWeaponCharge(player, stack)*59);
			
			if(SplatCraftPlayerData.getIsSquid(player))
			{
				event.setCanceled(true);
				
				if(charge <= 0)
					return;
			}
			
			if(!(stack.getItem() instanceof ICharge))
				stack = player.getHeldItemOffhand();
			
			if(stack.getItem() instanceof ICharge )
			{
				mc.renderEngine.bindTexture(CHARGER_OVERLAY);
				int x = width / 2 - 9;
				int y = height / 2 - 9;
				int tx = (charge % 13) * 19;
				int ty = (charge/13) * 19;
				
				//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.enableAlpha();
				GlStateManager.disableBlend();
				GlStateManager.color(1,1,1,1);
				drawTexturedModalRect(x, y, tx, ty, 19, 19);
			}
			
		}
	}
}
