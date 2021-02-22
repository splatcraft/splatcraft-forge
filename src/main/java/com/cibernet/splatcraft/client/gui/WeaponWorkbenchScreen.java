package com.cibernet.splatcraft.client.gui;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.crafting.*;
import com.cibernet.splatcraft.items.weapons.WeaponBaseItem;
import com.cibernet.splatcraft.network.CraftWeaponPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.container.PlayerInventoryContainer;
import com.cibernet.splatcraft.tileentities.container.WeaponWorkbenchContainer;
import com.cibernet.splatcraft.util.ColorUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WeaponWorkbenchScreen extends ContainerScreen<WeaponWorkbenchContainer>
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/weapon_crafting.png");
	
	private int tabPos = 0;
	private int sectionPos = 0;
	private int typePos = 0;
	private int subTypePos = 0;
	private int ingredientPos = 0;

	private int tickTime = 0;
	private WeaponWorkbenchSubtypeRecipe selectedRecipe = null;
	private WeaponWorkbenchRecipe selectedWeapon = null;

	private int craftButtonState = -1;
	
	PlayerEntity player;
	
	public WeaponWorkbenchScreen(WeaponWorkbenchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		
		ySize = 226;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = this.ySize - 92;
		this.player = inv.player;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);

		tickTime++;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1, 1, 1,1);
		minecraft.getTextureManager().bindTexture(TEXTURES);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		blit(matrixStack, x, y, 0, 0, xSize, ySize);

		World world = player.world;
		List<WeaponWorkbenchTab> tabList = world.getRecipeManager().getRecipes(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, playerInventory, world);
		tabList.sort((WeaponWorkbenchTab::compareTo));
		List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(world);
		recipeList.sort(WeaponWorkbenchRecipe::compareTo);

		if(!recipeList.isEmpty())
		{
			if(recipeList.get(typePos).getTotalRecipes() == 1)
				drawRecipeStack(world, matrixStack, recipeList, x, y, 0);
			else for(int i = -1; i <= 1; i++)
				drawRecipeStack(world, matrixStack, recipeList, x, y, i);
		}
		
	}

	private void drawRecipeStack(World world, MatrixStack matrixStack, List<WeaponWorkbenchRecipe> recipeList, int x, int y, int i)
	{
		WeaponWorkbenchSubtypeRecipe selectedRecipe = recipeList.get(typePos).getRecipeFromIndex(subTypePos +i < 0 ? (recipeList.get(typePos).getTotalRecipes()-1) : (subTypePos +i) % recipeList.get(typePos).getTotalRecipes());
		ItemStack displayStack = selectedRecipe.getOutput().copy();
		ColorUtils.setInkColor(displayStack, PlayerInfoCapability.get(player).getColor());

		matrixStack.push();
		float scale = i == 0  ? -28F : -14F;
		RenderHelper.enableStandardItemLighting();
		MatrixStack displayStackMatrix = new MatrixStack();
		displayStackMatrix.translate(x + 88 + (i*26), y + 73, 100);
		displayStackMatrix.scale(scale, scale, scale);
		displayStackMatrix.rotate(new Quaternion(0, 1, 0, 0));
		IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		minecraft.getItemRenderer().renderItem(displayStack, ItemCameraTransforms.TransformType.GUI, false, displayStackMatrix,
				irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, minecraft.getItemRenderer().getItemModelWithOverrides(displayStack, world, player));
		irendertypebuffer$impl.finish();
		matrixStack.pop();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY)
	{
		font.drawString(matrixStack, title.getString(), xSize/2 - font.getStringWidth(title.getString())/2, 22, 4210752);
		this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);

		World world = player.world;
		List<WeaponWorkbenchTab> tabList = world.getRecipeManager().getRecipes(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, playerInventory, world);
		tabList.sort((WeaponWorkbenchTab::compareTo));
		List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(world);
		recipeList.sort(WeaponWorkbenchRecipe::compareTo);
		selectedWeapon = null;
		selectedRecipe = null;
		if(!recipeList.isEmpty())
		{
			selectedWeapon = recipeList.get(typePos);
			selectedRecipe = selectedWeapon.getRecipeFromIndex(subTypePos);
		}

		//Update Craft Button
		if(selectedRecipe != null)
		{
			boolean hasMaterial = true;
			for(int i = ingredientPos*8; i < selectedRecipe.getInput().size() && i < ingredientPos*8 + 8; i++)
			{
				Ingredient ingredient = selectedRecipe.getInput().get(i).getIngredient();
				int count = selectedRecipe.getInput().get(i).getCount();
				if(!SplatcraftRecipeTypes.getItem(player, ingredient, count, false))
				{
					hasMaterial = false;
					break;
				}
			}

			if(!hasMaterial)
				craftButtonState = -1;
			else if(craftButtonState == -1)
				craftButtonState = 0;
		}

		//Draw Tab Buttons
		for(int i = 0; i < tabList.size(); i++)
		{
			int ix = xSize/2 - (tabList.size()-1)*11 + i*22;
			int iy = -5;
			int ty = tabPos == i ? 8 : 28;

			minecraft.getTextureManager().bindTexture(TEXTURES);
			blit(matrixStack, ix-10, iy, 211, ty, 20, 20);

			ResourceLocation tabIcon = tabList.get(i).getTabIcon();
			Item itemIcon = Registry.ITEM.getOrDefault(tabIcon);

			if(itemIcon != null && !itemIcon.equals(Items.AIR))
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(itemIcon), ix-8, iy+2);
			else
			{
				minecraft.getTextureManager().bindTexture(tabIcon);
				blit(matrixStack, ix-8, iy+2, 16,16, 0, 0, 256, 256, 256, 256);
			}

		}
		minecraft.getTextureManager().bindTexture(TEXTURES);

		//Draw Weapon Selection
		for(int i = sectionPos*8; i < recipeList.size() && i < sectionPos*8 + 8; i++)
		{
			ItemStack displayStack = recipeList.get(i).getRecipeOutput();

			int j = i - sectionPos*8;
			int ix = 17 + j*18;
			int iy = 34;

			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(displayStack, ix, iy);
			if(isPointInRegion(ix, iy, 16, 16, mouseX, mouseY))
			{
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				int slotColor = -2130706433;
				this.fillGradient(matrixStack, ix, iy, ix + 16, iy + 16, slotColor, slotColor);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
			}
		}

		//Draw Ingredients
		if(selectedRecipe != null)
		for(int i = ingredientPos*8; i < selectedRecipe.getInput().size() && i < ingredientPos*8 + 8; i++)
		{
			Ingredient ingredient = selectedRecipe.getInput().get(i).getIngredient();
			int count = selectedRecipe.getInput().get(i).getCount();
			ItemStack displayStack = ingredient.getMatchingStacks()[(tickTime/20) % ingredient.getMatchingStacks().length];

			int j = i - ingredientPos*6;
			int ix = 17 + j*18;
			int iy = 108;


			IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

			boolean hasMaterial = SplatcraftRecipeTypes.getItem(player, ingredient, count, false);
			int color = hasMaterial ? 0xFFFFFF : 0xFF5555;
			String s = String.valueOf(count);

			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(displayStack, ix, iy);

			if(!hasMaterial)
			{
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				int slotColor = 0x40ff0000;
				this.fillGradient(matrixStack, ix, iy, ix + 16, iy + 16, slotColor, slotColor);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();
			}

			if(count != 1)
			{
				float zLevel = minecraft.getItemRenderer().zLevel;
				matrixStack.translate(0.0D, 0.0D, (double) (zLevel + 200.0F));
				font.renderString(s, (float) (ix + 19 - 2 - font.getStringWidth(s)), (float) (iy + 6 + 3), color, true, matrixStack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
				matrixStack.translate(0.0D, 0.0D, (double) -(zLevel + 200.0F));
				irendertypebuffer$impl.finish();
			}



		}
		minecraft.getTextureManager().bindTexture(TEXTURES);

		//Tab Arrows TODO

		//Weapon Arrows
		int maxSections = (int) Math.ceil(recipeList.size()/8f);
		if(maxSections > 1)
		{
			int ty = sectionPos + 1 < maxSections ? (isPointInRegion(162, 36, 7, 11, mouseX, mouseY) ? 24 : 12) : 36;
			blit(matrixStack, 162, 36, 231, ty, 7, 11);
			ty = sectionPos - 1 >= 0 ? (isPointInRegion(7, 36, 7, 11, mouseX, mouseY) ? 24 : 12) : 36;
			blit(matrixStack, 7, 36, 239, ty, 7, 11);
		}

		//Subtype Arrows
		boolean hasSubtypes = recipeList.isEmpty() ? false : recipeList.get(typePos).getTotalRecipes() > 1;
		int ty = hasSubtypes ? (isPointInRegion(126, 67, 7, 11, mouseX, mouseY) ? 24 : 12) : 36;
		blit(matrixStack, 126, 67, 231, ty, 7, 11);
		ty = hasSubtypes ? (isPointInRegion(43, 67, 7, 11, mouseX, mouseY) ? 24 : 12) : 36;
		blit(matrixStack, 43, 67, 239, ty, 7, 11);

		//Ingredient Arrows
		maxSections = selectedRecipe == null ? 0 : (int) Math.ceil(selectedRecipe.getInput().size()/8f);
		if(selectedRecipe != null && maxSections > 1)
		{
			ty = sectionPos + 1 <= maxSections ? (isPointInRegion(162, 110, 7, 11, mouseX, mouseY) ? 24 : 12) : 36;
			blit(matrixStack, 162, 110, 231, ty, 7, 11);
			ty = sectionPos - 1 >= 0 ? (isPointInRegion(7, 110, 7, 11, mouseX, mouseY) ? 24 : 12) : 36;
			blit(matrixStack, 7, 110, 239, ty, 7, 11);
		}

		//Craft Button
		ty = 0;
		if(craftButtonState > 0)
			ty = 12;
		else if(craftButtonState == 0)
			ty = isPointInRegion(71, 93, 34, 12, mouseX, mouseY) ? 24 : 36;

		blit(matrixStack, 71, 93,177, ty, 34, 12);
		String craftStr = new TranslationTextComponent("gui.weapon_workbench.craft").getString();

		font.drawString(matrixStack, craftStr, xSize/2 - font.getStringWidth(craftStr)/2, 95, ty == 0 ? 0x999999 : 0xEFEFEF);
		minecraft.getTextureManager().bindTexture(TEXTURES);

		//Selected Pointer
		int selectedPos = typePos - sectionPos*8;
		if(selectedRecipe != null && selectedPos < 8 &&  selectedPos >= 0)
		{
			float zLevel = minecraft.getItemRenderer().zLevel;
			//matrixStack.translate(0.0D, 0.0D, (minecraft.getItemRenderer().zLevel + 500.0F));
			blit(matrixStack, 13 + selectedPos * 18, 46, 246, 40, 8, 8);
		}

		//Tab Button Tooltips
		for(int i = 0; i < tabList.size(); i++)
		{
			int ix = xSize/2 - (tabList.size()-1)*11 + i*22;
			int iy = -5;
			//matrixStack.translate(0,0,500);
			if(isPointInRegion(ix-10, iy, 18, 18, mouseX, mouseY))
			{
				ArrayList<ITextComponent> tooltip = new ArrayList();
				tooltip.add(new TranslationTextComponent("weaponTab."+tabList.get(i).getId().toString()));
				func_243308_b(matrixStack, tooltip, mouseX-guiLeft, mouseY-guiTop);
			}
			//matrixStack.translate(0,0,-500);
		}


		//Draw Recipe Tooltips
		for(int i = sectionPos*8; i < recipeList.size() && i < sectionPos*8 + 8; i++)
		{
			ItemStack displayStack = recipeList.get(i).getRecipeOutput();
			displayStack.getOrCreateTag().putBoolean("IsPlural", true);

			int j = i - sectionPos*8;
			int ix = 17 + j*18;
			int iy = 34;

			if(isPointInRegion(ix, iy, 16, 16, mouseX, mouseY))
			{

				ArrayList<ITextComponent> tooltip = new ArrayList();
				TranslationTextComponent t = new TranslationTextComponent("weaponRecipe."+recipeList.get(i).getId().toString());
				if(t.getString().equals("weaponRecipe."+recipeList.get(i).getId().toString()))
					tooltip.add(displayStack.getDisplayName());
				else tooltip.add(t);

				//if(minecraft.gameSettings.advancedItemTooltips)
				//	tooltip.add(new StringTextComponent(recipeList.get(i).getId().toString()).mergeStyle(TextFormatting.DARK_GRAY));

				func_243308_b(matrixStack, tooltip, mouseX-guiLeft, mouseY-guiTop);
			}
		}


		if(selectedRecipe != null)
		{
			//Draw Ingredient Tooltips
			for(int i = ingredientPos*6; i < selectedRecipe.getInput().size() && i < ingredientPos*6 + 6; i++)
			{
				Ingredient ingredient = selectedRecipe.getInput().get(i).getIngredient();
				int count = selectedRecipe.getInput().get(i).getCount();
				ItemStack displayStack = ingredient.getMatchingStacks()[(tickTime/20) % ingredient.getMatchingStacks().length];

				int j = i - ingredientPos*6;
				int ix = 17 + j*18;
				int iy = 108;

				if(isPointInRegion(ix, iy, 16, 16, mouseX, mouseY))
					renderTooltip(matrixStack, displayStack, mouseX-guiLeft, mouseY-guiTop);
			}

			//Draw Selected Weapon Tooltip
			if(isPointInRegion(74, 59, 28, 28, mouseX, mouseY))
				renderTooltip(matrixStack, selectedRecipe.getOutput(), mouseX-guiLeft, mouseY-guiTop);


		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{

		if(craftButtonState == 1)
		{
			craftButtonState = 0;
			if (selectedRecipe != null && isPointInRegion(71, 93, 34, 12, mouseX, mouseY))
			{
				SplatcraftPacketHandler.sendToServer(new CraftWeaponPacket(selectedWeapon.getId(), subTypePos));
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		World world = player.world;
		List<WeaponWorkbenchTab> tabList = world.getRecipeManager().getRecipes(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, playerInventory, world);
		tabList.sort((WeaponWorkbenchTab::compareTo));
		List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(world);
		recipeList.sort(WeaponWorkbenchRecipe::compareTo);

		//Tab Buttons
		for(int i = 0; i < tabList.size(); i++)
		{
			int ix = xSize/2 - (tabList.size()-1)*11 + i*22;
			int iy = -4;
			int ty = tabPos == i ? 8 : 28;

			minecraft.getTextureManager().bindTexture(TEXTURES);
			if(tabPos != i && isPointInRegion(ix-10, iy, 20, 20, mouseX, mouseY))
			{
				tabPos = i;
				typePos = 0;
				sectionPos = 0;
				subTypePos = 0;
				ingredientPos = 0;
				playButtonSound();
			}
		}

		//Tab Button Arrows TODO

		//Weapon Selection
		for(int i = sectionPos*8; i < recipeList.size() && i < sectionPos*8 + 8; i++)
		{
			int j = i - sectionPos*8;
			int ix = 17 + j*18;
			int iy = 34;

			if(typePos != i && isPointInRegion(ix, iy, 16, 16, mouseX, mouseY))
			{
				typePos = i;
				subTypePos = 0;
				ingredientPos = 0;
				playButtonSound();
			}
		}

		//Weapon Selection Arrows
		int maxSections = (int) Math.ceil(recipeList.size()/8f);
		if(maxSections > 1)
		{
			if(sectionPos + 1 < maxSections && isPointInRegion(162, 36, 7, 11, mouseX, mouseY))
			{
				subTypePos = 0;
				sectionPos++;
				ingredientPos = 0;
				playButtonSound();
			}
			else if(sectionPos - 1 >= 0 && isPointInRegion(7, 36, 7, 11, mouseX, mouseY))
			{
				subTypePos = 0;
				sectionPos--;
				ingredientPos = 0;
				playButtonSound();
			}
		}

		//Subtype Arrows
		int totalSubtypes = recipeList.isEmpty() ? 0 : recipeList.get(typePos).getTotalRecipes();
		if(recipeList.isEmpty() ? false : totalSubtypes > 1)
		{
			if(isPointInRegion(126, 67, 7, 11, mouseX, mouseY) || isPointInRegion(107, 66, 14, 14, mouseX, mouseY))
			{
				ingredientPos = 0;
				subTypePos = (subTypePos+1) % totalSubtypes;


				playButtonSound();
			}
			else if(isPointInRegion(43, 67, 7, 11, mouseX, mouseY) || isPointInRegion(55, 66, 14, 14, mouseX, mouseY))
			{
				ingredientPos = 0;
				subTypePos--;
				if(subTypePos < 0)
					subTypePos = totalSubtypes-1;
				playButtonSound();
			}
		}

		//Ingredient Arrows TODO
		maxSections = selectedRecipe == null ? 0 : (int) Math.ceil(selectedRecipe.getInput().size()/8f);
		if(selectedRecipe != null && maxSections > 1)
		{
			if(sectionPos + 1 <= maxSections && isPointInRegion(162, 110, 7, 11, mouseX, mouseY))
			{
				ingredientPos++;
				playButtonSound();
			}
			else if(sectionPos - 1 >= 0 && isPointInRegion(7, 110, 7, 11, mouseX, mouseY))
			{
				ingredientPos--;
				playButtonSound();
			}
		}

		//Craft Button
		if(craftButtonState != -1 && isPointInRegion(71, 93, 34, 12, mouseX, mouseY))
		{
			craftButtonState = 1;
			playButtonSound();
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void playButtonSound()
	{
		minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
}
