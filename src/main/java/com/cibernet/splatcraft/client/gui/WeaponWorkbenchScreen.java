package com.cibernet.splatcraft.client.gui;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.crafting.*;
import com.cibernet.splatcraft.tileentities.container.PlayerInventoryContainer;
import com.cibernet.splatcraft.tileentities.container.WeaponWorkbenchContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class WeaponWorkbenchScreen extends ContainerScreen<WeaponWorkbenchContainer>
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/weapon_crafting.png");
	
	private int tabPos = 0;
	private int sectionPos = 0;
	private int typePos = 0;
	private int subTypePos = 0;
	private int ingredientPos = 0;
	
	private int craftButtonStage = 0;
	
	PlayerEntity player;
	
	public WeaponWorkbenchScreen(WeaponWorkbenchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		
		ySize = 202;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = this.ySize - 98;
		this.player = inv.player;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		func_230459_a_(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1, 1, 1,1);
		minecraft.getTextureManager().bindTexture(TEXTURES);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		blit(matrixStack, x, y, 0, 0, xSize, ySize);
		
		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		
		font.drawString(matrixStack, title.getUnformattedComponentText(), (xSize/2 - font.getStringWidth(title.getUnformattedComponentText())/2), 21, 4210752);
		
		World world = player.world;
		List<WeaponWorkbenchTab> tabList = world.getRecipeManager().getRecipes(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, playerInventory, world);
		tabList.sort((WeaponWorkbenchTab::compareTo));
		List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(world);
		AbstractWeaponWorkbenchRecipe selectedRecipe = recipeList.get(typePos).getRecipeFromIndex(subTypePos);
		
		//Draw Tab Buttons
		for(int i = 0; i < tabList.size(); i++)
		{
			int x = width/2 - (tabList.size()-1)*11 + i*22;
			int ty = tabPos == i ? 216 : 236;
			
			minecraft.getTextureManager().bindTexture(TEXTURES);
			blit(matrixStack, x-10, guiTop-4, 34, ty, 20, 20);
			minecraft.getTextureManager().bindTexture(tabList.get(i).getTabIcon());
			blit(matrixStack, x-8, guiTop-2, 0,0, 256, 256, 16, 16, 256, 256);
		}
		
		minecraft.getTextureManager().bindTexture(TEXTURES);
	}
}
