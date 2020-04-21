package com.cibernet.splatcraft.gui;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.gui.container.ContainerPlayerInv;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.PacketCraftWeapon;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.recipes.RecipeSubtype;
import com.cibernet.splatcraft.recipes.RecipeType;
import com.cibernet.splatcraft.recipes.RecipesWeaponStation;
import com.cibernet.splatcraft.recipes.WeaponStationTabs;
import com.cibernet.splatcraft.utils.ColorItemUtils;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.List;

public class GuiWeaponStation extends GuiContainer
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(SplatCraft.MODID, "textures/gui/weapon_crafting.png");
	
	private int tabPos = 0;
	private int sectionPos = 0;
	private int typePos = 0;
	private int subTypePos = 0;
	private int ingredientPos = 0;
	
	private int craftButtonStage = 0;
	
	EntityPlayer player;
	public GuiWeaponStation(EntityPlayer player, BlockPos pos)
	{
		super(new ContainerPlayerInv(player.inventory, pos, 8, 120));
		this.player = player;
		
		ySize = 202;
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		if(craftButtonStage == 1)
		{
			WeaponStationTabs tab = WeaponStationTabs.values()[tabPos];
			List<RecipeType> typeList = RecipesWeaponStation.recipeTabs.get(tab);
			List<RecipeSubtype> subtypes = typeList.get(typePos).getSubtypes();
			RecipeSubtype recipe = subtypes.get(subTypePos);
			
			boolean check = true;
			
			for(int i = 0; i < recipe.getIngredients().size(); i++)
			{
				if(!RecipesWeaponStation.getItem(player, recipe.getIngredients().get(i), false))
				{
					check = false;
					break;
				}
				
			}
			
			if(check)
				SplatCraftPacketHandler.instance.sendToServer(new PacketCraftWeapon(recipe));
			
			craftButtonStage = 2;
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURES);
		drawTexturedModalRect(guiLeft, guiTop, 0 ,0, xSize, ySize);
		
		WeaponStationTabs tab = WeaponStationTabs.values()[tabPos];
		List<RecipeType> section = RecipesWeaponStation.recipeTabs.get(tab);
		RecipeSubtype recipe = section.get(typePos).getSubtypes().get(subTypePos);
		
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(guiLeft + 34, guiTop + 81, 100);
			
			GlStateManager.rotate(player.ticksExisted - partialTicks, 0, 1, 0);
			
			float transitionPercent = 1;
			float scale = 38F * transitionPercent;
			GlStateManager.scale(scale, -scale, scale);
			RenderHelper.enableStandardItemLighting();
			Minecraft.getMinecraft().getRenderItem().renderItem(ColorItemUtils.setInkColor(recipe.getOutput().copy(), SplatCraftPlayerData.getInkColor(player)), ItemCameraTransforms.TransformType.GUI);
		}
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String displayName = getDisplayName().getUnformattedComponentText();
		fontRenderer.drawString(displayName, (xSize/2 - fontRenderer.getStringWidth(displayName)/2), 21, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1,1,1,1);
		
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		WeaponStationTabs tab = WeaponStationTabs.values()[tabPos];
		List<RecipeType> typeList = RecipesWeaponStation.recipeTabs.get(tab);
		List<RecipeSubtype> subtypes = typeList.get(typePos).getSubtypes();
		RecipeSubtype recipe = subtypes.get(subTypePos);
		String subName = I18n.format(recipe.getName());
		
		this.fontRenderer.drawString(subName, 34+guiLeft - fontRenderer.getStringWidth(subName)/2, 54+guiTop, 4210752);
		
		//draw ingredients
		for(int i = ingredientPos*6; i < recipe.getIngredients().size() && i < ingredientPos*6 + 6; i++)
		{
			ItemStack input = recipe.getIngredients().get(i);
			
			int j = i - ingredientPos;
			int xPos = 107 + (j % 3)*18;
			int yPos = 64 + (j/3)*18;
			
			String color = RecipesWeaponStation.getItem(player, input, false) ? "" : TextFormatting.RED + "";
			drawRecipeItemStack(input, xPos + guiLeft, yPos + guiTop, color + input.getCount());
			
		}
		
		//draw recipe selection
		for(int i = sectionPos*8; i < typeList.size() && i < sectionPos*8 + 8; i++)
		{
			ItemStack stack = typeList.get(i).getDisplayStack();
			
			int j = i - sectionPos*8;
			int xPos = 17 + j*18;
			int yPos = 34;
			
			drawRecipeItemStack(stack, xPos + guiLeft, yPos + guiTop, "");
			
			if(this.isPointInRegion(xPos, yPos, 16, 16, mouseX, mouseY))
			{
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = xPos + guiLeft;
				int k1 = yPos + guiTop;
				GlStateManager.colorMask(true, true, true, false);
				this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
		
		RenderHelper.disableStandardItemLighting();
		
		//Craft Button
		mc.getTextureManager().bindTexture(TEXTURES);
		
		int ty = 220;
		if(craftButtonStage <= 0)
			ty = isPointInRegion(66, 75, 34, 12, mouseX, mouseY) ? 232 : 244;
		
		String btnText = I18n.format("gui.weaponStation.craft");
		drawTexturedModalRect(66+guiLeft, 75+guiTop, 0, ty, 34, 12);
		drawString(fontRenderer, btnText, guiLeft+83 - fontRenderer.getStringWidth(btnText)/2, 77+guiTop, 0xEFEFEF);
		
		//Tab Buttons
		
		WeaponStationTabs[] tabs = WeaponStationTabs.values();
		for(int i = 0; i < tabs.length; i++)
		{
			int x = width/2 - (tabs.length-1)/2*22 + i*22;
			ty = tabPos == i ? 216 : 236;
			mc.getTextureManager().bindTexture(TEXTURES);
			drawTexturedModalRect(x-10, guiTop-4, 34, ty, 20, 20);
			
			mc.getTextureManager().bindTexture(tabs[i].getIconLocation());
			drawScaledCustomSizeModalRect(x-8, guiTop-2, 0,0, 256, 256, 16, 16, 256, 256);
			
		}
		mc.getTextureManager().bindTexture(TEXTURES);
		
		//Subtype arrows
		ty = subtypes.size() > 1 ? (isPointInRegion(55, 75, 7, 11, mouseX, mouseY) ? 232 : 220) : 244;
		drawTexturedModalRect(guiLeft+55, guiTop+75, 54, ty, 7, 11);
		ty = subtypes.size() > 1 ? (isPointInRegion(6, 75, 7, 11, mouseX, mouseY) ? 232 : 220) : 244;
		drawTexturedModalRect(guiLeft+6, guiTop+75, 62, ty, 7, 11);
		
		int maxSections = (int) Math.ceil(typeList.size()/8);
		
		//Section arrows
		
		if(maxSections > 0)
		{
			ty = sectionPos + 1 <= maxSections ? (isPointInRegion(162, 36, 7, 11, mouseX, mouseY) ? 232 : 220) : 244;
			drawTexturedModalRect(guiLeft + 162, guiTop + 36, 54, ty, 7, 11);
			ty = sectionPos - 1 >= 0 ? (isPointInRegion(7, 36, 7, 11, mouseX, mouseY) ? 232 : 220) : 244;
			drawTexturedModalRect(guiLeft + 7, guiTop + 36, 62, ty, 7, 11);
		}
		//Selected Pointer
		int selectedPos = typePos - sectionPos*8;
		if(selectedPos < 8 &&  selectedPos - sectionPos*8 >= 0)
		{
			zLevel = 500;
			drawTexturedModalRect(13 + guiLeft + selectedPos * 18, 46 + guiTop, 69, 248, 8, 8);
			zLevel = 0;
		}
		
		//Tooltips
		renderHoveredToolTip(mouseX, mouseY);
		
		for(int i = sectionPos*8; i < typeList.size() && i < sectionPos*8 + 8; i++)
		{
			int j = i - sectionPos * 8;
			int xPos = 17 + j * 18;
			int yPos = 34;
			if(this.isPointInRegion(xPos, yPos, 16, 16, mouseX, mouseY))
			{
				drawHoveringText(I18n.format(typeList.get(i).getDisplayName()), mouseX, mouseY);
			}
		}
		
		for(int i = ingredientPos*6; i < recipe.getIngredients().size() && i < ingredientPos*6 + 6; i++)
		{
			ItemStack input = recipe.getIngredients().get(i);
			
			int j = i - ingredientPos;
			int xPos = 107 + (j % 3)*18;
			int yPos = 64 + (j/3)*18;
			
			if(j >= 6)
				break;
				
			if(this.isPointInRegion(xPos, yPos, 16, 16, mouseX, mouseY))
				renderToolTip(input, mouseX, mouseY);
		}
		
		for(int i = 0; i < tabs.length; i++)
		{
			int x = width / 2 - (tabs.length - 1) / 2 * 22 + i * 22;
			
			if(isPointInRegion(x-10-guiLeft, -4, 20, 20, mouseX, mouseY))
				drawHoveringText(I18n.format(tabs[i].getUnlocalizedName()), mouseX, mouseY);
		}
		
		if(isPointInRegion(17,64, 34, 34, mouseX, mouseY))
			renderToolTip(recipe.getOutput(), mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(mouseButton == 0)
		{
			WeaponStationTabs tab = WeaponStationTabs.values()[tabPos];
			List<RecipeType> typeList = RecipesWeaponStation.recipeTabs.get(tab);
			List<RecipeSubtype> subtypes = typeList.get(typePos).getSubtypes();
			RecipeSubtype recipe = subtypes.get(subTypePos);
			int maxSections = (int) Math.ceil(typeList.size() / 8);
			int maxSubtype = subtypes.size() - 1;
			
			//Tab Buttons
			WeaponStationTabs[] tabs = WeaponStationTabs.values();
			for(int i = 0; i < tabs.length; i++)
			{
				int x = width / 2 - (tabs.length - 1) / 2 * 22 + i * 22;
				if(isPointInRegion(x - 10 - guiLeft, -4, 20, 20, mouseX, mouseY))
				{
					tabPos = i;
					sectionPos = 0;
					typePos = 0;
					subTypePos = 0;
					playButtonSound();
				}
			}
			
			//Recipe Buttons
			for(int i = sectionPos * 8; i < typeList.size() && i < sectionPos * 8 + 8; i++)
			{
				int j = i - sectionPos * 8;
				int xPos = 17 + j * 18;
				int yPos = 34;
				
				if(this.isPointInRegion(xPos, yPos, 16, 16, mouseX, mouseY))
				{
					typePos = i;
					subTypePos = 0;
					playButtonSound();
				}
			}
			
			
			//Section arrows
			
			if(maxSections > 0)
			{
				if(isPointInRegion(162, 36, 7, 11, mouseX, mouseY))
				{
					sectionPos = Math.min(sectionPos + 1, maxSections);
					playButtonSound();
				}
				if(isPointInRegion(7, 36, 7, 11, mouseX, mouseY))
				{
					sectionPos = Math.max(0, sectionPos - 1);
					playButtonSound();
				}
			}
			
			//Subtype arrows
			if(maxSubtype > 0)
			{
				if(isPointInRegion(55, 75, 7, 11, mouseX, mouseY))
				{
					subTypePos = (subTypePos + 1) % (maxSubtype + 1);
					playButtonSound();
				}
				if(isPointInRegion(6, 75, 7, 11, mouseX, mouseY))
				{
					subTypePos = subTypePos - 1 < 0 ? maxSubtype : subTypePos - 1;
					playButtonSound();
				}
			}
			
			//Craft Button
			if(isPointInRegion(66, 75, 34, 12, mouseX, mouseY))
			{
				craftButtonStage = 1;
				playButtonSound();
			}
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		
		if(state == 0)
			craftButtonStage = 0;
	}
	
	public static ITextComponent getDisplayName()
	{
		return new TextComponentTranslation("container.weaponStation", new Object[0]);
	}
	
	private void drawRecipeItemStack(ItemStack stack, int x, int y, String altText)
	{
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
		
		
		
		if (font == null) font = fontRenderer;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (0), altText);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
		GlStateManager.translate(0.0F, 0.0F, -32.0F);
	}
	
	private void playButtonSound()
	{
		this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
}
