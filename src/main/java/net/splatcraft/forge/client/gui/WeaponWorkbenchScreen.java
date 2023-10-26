package net.splatcraft.forge.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.crafting.SplatcraftRecipeTypes;
import net.splatcraft.forge.crafting.WeaponWorkbenchRecipe;
import net.splatcraft.forge.crafting.WeaponWorkbenchSubtypeRecipe;
import net.splatcraft.forge.crafting.WeaponWorkbenchTab;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.c2s.CraftWeaponPacket;
import net.splatcraft.forge.tileentities.container.WeaponWorkbenchContainer;
import net.splatcraft.forge.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class WeaponWorkbenchScreen extends AbstractContainerScreen<WeaponWorkbenchContainer>
{
    private static final ResourceLocation TEXTURES = new ResourceLocation(Splatcraft.MODID, "textures/gui/weapon_crafting.png");
    Player player;
    private int tabPos = 0;
    private int sectionPos = 0;
    private int typePos = 0;
    private int subTypePos = 0;
    private int ingredientPos = 0;
    private int tickTime = 0;
    private WeaponWorkbenchSubtypeRecipe selectedRecipe = null;
    private WeaponWorkbenchRecipe selectedWeapon = null;
    private int craftButtonState = -1;
    private final Inventory inventory;

    public WeaponWorkbenchScreen(WeaponWorkbenchContainer screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn);

        imageHeight = 226;
        this.titleLabelX = 8;
        this.titleLabelY = this.imageHeight - 92;
        this.player = inv.player;
        this.inventory = inv;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);

        tickTime++;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURES);
        if (minecraft != null)
        {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

            Level level = player.level;
            List<WeaponWorkbenchTab> tabList = level.getRecipeManager().getRecipesFor(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, inventory, level);
            tabList.removeIf(tab -> tab.hidden && tab.getTabRecipes(level, player).isEmpty());
            tabList.sort(WeaponWorkbenchTab::compareTo);

            List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(level, player);
            recipeList.sort(WeaponWorkbenchRecipe::compareTo);

            if (!recipeList.isEmpty())
            {
                if (recipeList.get(typePos).getAvailableRecipesTotal(player) == 1)
                {
                    drawRecipeStack(level, matrixStack, recipeList, x, y, 0);
                } else
                {
                    for (int i = -1; i <= 1; i++)
                    {
                        drawRecipeStack(level, matrixStack, recipeList, x, y, i);
                    }
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void drawRecipeStack(Level level, PoseStack matrixStack, List<WeaponWorkbenchRecipe> recipeList, int x, int y, int i)
    {
        WeaponWorkbenchSubtypeRecipe selectedRecipe = recipeList.get(typePos).getRecipeFromIndex(player, subTypePos + i < 0 ? recipeList.get(typePos).getAvailableRecipesTotal(player) - 1 : (subTypePos + i) % recipeList.get(typePos).getAvailableRecipesTotal(player));
        ItemStack displayStack = selectedRecipe.getOutput().copy();
        ColorUtils.setInkColor(displayStack, PlayerInfoCapability.get(player).getColor());

        matrixStack.pushPose();
        float scale = i == 0 ? -28F : -14F;
        Lighting.setupFor3DItems();
        PoseStack displayStackMatrix = new PoseStack();
        displayStackMatrix.translate(x + 88 + i * 26, y + 73, 100);
        displayStackMatrix.scale(scale, scale, scale);
        displayStackMatrix.mulPose(new Quaternion(0, 1, 0, 0));



        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        int light = 15728880;
        //if(!MinecraftForge.EVENT_BUS.post(new RenderItemEvent(displayStack, ItemCameraTransforms.TransformType.GUI, displayStackMatrix, irendertypebuffer$impl, light, OverlayTexture.NO_OVERLAY, minecraft.getDeltaFrameTime())))
        {
            ItemRenderer itemRenderer = minecraft.getItemRenderer();
            if (itemRenderer != null)
            {
                minecraft.getItemRenderer().render(displayStack, ItemTransforms.TransformType.GUI, false, displayStackMatrix, irendertypebuffer$impl, light, OverlayTexture.NO_OVERLAY, minecraft.getItemRenderer().getModel(displayStack, level, player, 0));
            }
        }

        irendertypebuffer$impl.endBatch();
        matrixStack.popPose();
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY)
    {
        font.draw(matrixStack, title.getString(), (float) imageWidth / 2 - (float) font.width(title.getString()) / 2, 22, 4210752);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.titleLabelX, (float) this.titleLabelY, 4210752);

        Level level = player.level;
        List<WeaponWorkbenchTab> tabList = level.getRecipeManager().getRecipesFor(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, inventory, level);
        tabList.sort(WeaponWorkbenchTab::compareTo);
        tabList.removeIf(tab -> tab.hidden && tab.getTabRecipes(level, player).isEmpty());
        List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(level, player);
        recipeList.sort(WeaponWorkbenchRecipe::compareTo);

        selectedWeapon = null;
        selectedRecipe = null;
        if (!recipeList.isEmpty())
        {
            selectedWeapon = recipeList.get(typePos);
            selectedRecipe = selectedWeapon.getRecipeFromIndex(player, subTypePos);
        }

        //Update Craft Button
        if (selectedRecipe != null)
        {
            boolean hasMaterial = true;
            for (int i = ingredientPos * 8; i < selectedRecipe.getInput().size() && i < ingredientPos * 8 + 8; i++)
            {
                Ingredient ingredient = selectedRecipe.getInput().get(i).getIngredient();
                int count = selectedRecipe.getInput().get(i).getCount();
                if (!SplatcraftRecipeTypes.getItem(player, ingredient, count, false))
                {
                    hasMaterial = false;
                    break;
                }
            }

            if (!hasMaterial)
            {
                craftButtonState = -1;
            } else if (craftButtonState == -1)
            {
                craftButtonState = 0;
            }
        }

        TextureManager textureManager = minecraft.getTextureManager();
        if (textureManager != null)
        {
            //Draw Tab Buttons
            for (int i = 0; i < tabList.size(); i++)
            {
                int ix = imageWidth / 2 - (tabList.size() - 1) * 11 + i * 22;
                int iy = -5;
                int ty = tabPos == i ? 8 : 28;

                RenderSystem.setShaderTexture(0, TEXTURES);
                blit(matrixStack, ix - 10, iy, 211, ty, 20, 20);

                ResourceLocation tabIcon = tabList.get(i).getTabIcon();
                Item itemIcon = Registry.ITEM.get(tabIcon);

                if (!itemIcon.equals(Items.AIR))
                {
                    minecraft.getItemRenderer().renderGuiItem(new ItemStack(itemIcon), ix - 8, iy + 2);
                } else
                {
                    RenderSystem.setShaderTexture(0, tabIcon);
                    blit(matrixStack, ix - 8, iy + 2, 16, 16, 0, 0, 256, 256, 256, 256);
                }

            }
            RenderSystem.setShaderTexture(0, TEXTURES);
        }

        //Draw Weapon Selection
        for (int i = sectionPos * 8; i < recipeList.size() && i < sectionPos * 8 + 8; i++)
        {
            ItemStack displayStack = recipeList.get(i).getResultItem();

            int j = i - sectionPos * 8;
            int ix = 17 + j * 18;
            int iy = 34;

            minecraft.getItemRenderer().renderGuiItem(displayStack, ix, iy);
            if (isHovering(ix, iy, 16, 16, mouseX, mouseY))
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
        if (selectedRecipe != null)
        {
            for (int i = ingredientPos * 8; i < selectedRecipe.getInput().size() && i < ingredientPos * 8 + 8; i++)
            {
                Ingredient ingredient = selectedRecipe.getInput().get(i).getIngredient();
                int count = selectedRecipe.getInput().get(i).getCount();
                ItemStack displayStack = ingredient.getItems()[tickTime / 20 % ingredient.getItems().length];

                int j = i - ingredientPos * 6;
                int ix = 17 + j * 18;
                int iy = 108;


                MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                boolean hasMaterial = SplatcraftRecipeTypes.getItem(player, ingredient, count, false);
                int color = hasMaterial ? 0xFFFFFF : 0xFF5555;
                String s = String.valueOf(count);

                minecraft.getItemRenderer().renderGuiItem(displayStack, ix, iy);

                if (!hasMaterial)
                {
                    RenderSystem.disableDepthTest();
                    RenderSystem.colorMask(true, true, true, false);
                    int slotColor = 0x40ff0000;
                    this.fillGradient(matrixStack, ix, iy, ix + 16, iy + 16, slotColor, slotColor);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }

                if (count != 1)
                {
                    float zLevel = minecraft.getItemRenderer().blitOffset;
                    matrixStack.translate(0.0D, 0.0D, zLevel + 200.0F);
                    font.drawInBatch(s, (float) (ix + 19 - 2 - font.width(s)), (float) (iy + 6 + 3), color, true, matrixStack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
                    matrixStack.translate(0.0D, 0.0D, -(zLevel + 200.0F));
                    irendertypebuffer$impl.endBatch();
                }


            }
        }
        RenderSystem.setShaderTexture(0, TEXTURES);

        //Tab Arrows TODO

        //Weapon Arrows
        int maxSections = (int) Math.ceil(recipeList.size() / 8f);
        if (maxSections > 1)
        {
            int ty = sectionPos + 1 < maxSections ? isHovering(162, 36, 7, 11, mouseX, mouseY) ? 24 : 12 : 36;
            blit(matrixStack, 162, 36, 231, ty, 7, 11);
            ty = sectionPos - 1 >= 0 ? isHovering(7, 36, 7, 11, mouseX, mouseY) ? 24 : 12 : 36;
            blit(matrixStack, 7, 36, 239, ty, 7, 11);
        }

        //Subtype Arrows
        boolean hasSubtypes = !recipeList.isEmpty() && recipeList.get(typePos).getAvailableRecipesTotal(player) > 1;
        int ty = hasSubtypes ? isHovering(126, 67, 7, 11, mouseX, mouseY) ? 24 : 12 : 36;
        blit(matrixStack, 126, 67, 231, ty, 7, 11);
        ty = hasSubtypes ? isHovering(43, 67, 7, 11, mouseX, mouseY) ? 24 : 12 : 36;
        blit(matrixStack, 43, 67, 239, ty, 7, 11);

        //Ingredient Arrows
        maxSections = selectedRecipe == null ? 0 : (int) Math.ceil(selectedRecipe.getInput().size() / 8f);
        if (selectedRecipe != null && maxSections > 1)
        {
            ty = sectionPos + 1 <= maxSections ? isHovering(162, 110, 7, 11, mouseX, mouseY) ? 24 : 12 : 36;
            blit(matrixStack, 162, 110, 231, ty, 7, 11);
            ty = sectionPos - 1 >= 0 ? isHovering(7, 110, 7, 11, mouseX, mouseY) ? 24 : 12 : 36;
            blit(matrixStack, 7, 110, 239, ty, 7, 11);
        }

        //Craft Button
        ty = 0;
        if (craftButtonState > 0)
        {
            ty = 12;
        } else if (craftButtonState == 0)
        {
            ty = isHovering(71, 93, 34, 12, mouseX, mouseY) ? 24 : 36;
        }

        blit(matrixStack, 71, 93, 177, ty, 34, 12);
        String craftStr = new TranslatableComponent("gui.ammo_knights_workbench.craft").getString();

        font.draw(matrixStack, craftStr, (float) imageWidth / 2 - (float) font.width(craftStr) / 2, 95, ty == 0 ? 0x999999 : 0xEFEFEF);
        RenderSystem.setShaderTexture(0, TEXTURES);

        //Selected Pointer
        int selectedPos = typePos - sectionPos * 8;
        if (selectedRecipe != null && selectedPos < 8 && selectedPos >= 0)
        {
            //matrixStack.translate(0.0D, 0.0D, (minecraft.getItemRenderer().zLevel + 500.0F));
            blit(matrixStack, 13 + selectedPos * 18, 46, 246, 40, 8, 8);
        }

        //Tab Button Tooltips
        for (int i = 0; i < tabList.size(); i++)
        {
            int ix = imageWidth / 2 - (tabList.size() - 1) * 11 + i * 22;
            int iy = -5;
            //matrixStack.translate(0,0,500);
            if (isHovering(ix - 10, iy, 18, 18, mouseX, mouseY))
            {
                ArrayList<Component> tooltip = new ArrayList<>();
                tooltip.add(new TranslatableComponent("weaponTab." + tabList.get(i).getId().toString()));
                renderComponentTooltip(matrixStack, tooltip, mouseX - leftPos, mouseY - topPos, font);
            }
            //matrixStack.translate(0,0,-500);
        }


        //Draw Recipe Tooltips
        for (int i = sectionPos * 8; i < recipeList.size() && i < sectionPos * 8 + 8; i++)
        {
            ItemStack displayStack = recipeList.get(i).getResultItem();
            displayStack.getOrCreateTag().putBoolean("IsPlural", true);

            int j = i - sectionPos * 8;
            int ix = 17 + j * 18;
            int iy = 34;

            if (isHovering(ix, iy, 16, 16, mouseX, mouseY))
            {

                ArrayList<Component> tooltip = new ArrayList<>();
                TranslatableComponent t = new TranslatableComponent("weaponRecipe." + recipeList.get(i).getId().toString());
                if (t.getString().equals("weaponRecipe." + recipeList.get(i).getId().toString()))
                {
                    tooltip.add(getDisplayName(displayStack));
                } else
                {
                    tooltip.add(t);
                }

                //if(minecraft.options.advancedItemTooltips)
                //    tooltip.add(new StringTextComponent(recipeList.get(i).getId().toString()).mergeStyle(ChatFormatting.DARK_GRAY));

                renderComponentTooltip(matrixStack, tooltip, mouseX - leftPos, mouseY - topPos, font);
            }
        }


        if (selectedRecipe != null)
        {
            //Draw Ingredient Tooltips
            for (int i = ingredientPos * 8; i < selectedRecipe.getInput().size() && i < ingredientPos * 8 + 8; i++)
            {
                Ingredient ingredient = selectedRecipe.getInput().get(i).getIngredient();
                ItemStack displayStack = ingredient.getItems()[tickTime / 20 % ingredient.getItems().length];

                int j = i - ingredientPos * 6;
                int ix = 17 + j * 18;
                int iy = 108;

                if (isHovering(ix, iy, 16, 16, mouseX, mouseY))
                {
                    renderTooltip(matrixStack, displayStack, mouseX - leftPos, mouseY - topPos);
                }
            }

            //Draw Selected Weapon Tooltip
            if (isHovering(74, 59, 28, 28, mouseX, mouseY))
            {
                renderTooltip(matrixStack, selectedRecipe.getOutput(), mouseX - leftPos, mouseY - topPos);
            }


        }
    }

    protected static Component getDisplayName(ItemStack stack) {
        MutableComponent iformattabletextcomponent = (new TextComponent("")).append(stack.getHoverName());
        if (stack.hasCustomHoverName())
            iformattabletextcomponent.withStyle(ChatFormatting.ITALIC);

        iformattabletextcomponent.withStyle(stack.getRarity().color).withStyle((style) ->
                style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(stack))));

        return iformattabletextcomponent;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {

        if (craftButtonState == 1)
        {
            craftButtonState = 0;
            if (selectedRecipe != null && isHovering(71, 93, 34, 12, mouseX, mouseY))
            {
                SplatcraftPacketHandler.sendToServer(new CraftWeaponPacket(selectedWeapon.getId(), subTypePos));
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        Level level = player.level;
        List<WeaponWorkbenchTab> tabList = level.getRecipeManager().getRecipesFor(SplatcraftRecipeTypes.WEAPON_STATION_TAB_TYPE, inventory, level);
        tabList.sort(WeaponWorkbenchTab::compareTo);
        tabList.removeIf(tab -> tab.hidden && tab.getTabRecipes(level, player).isEmpty());

        List<WeaponWorkbenchRecipe> recipeList = tabList.get(tabPos).getTabRecipes(level, player);
        recipeList.sort(WeaponWorkbenchRecipe::compareTo);

        //Tab Buttons
        for (int i = 0; i < tabList.size(); i++)
        {
            int ix = imageWidth / 2 - (tabList.size() - 1) * 11 + i * 22;
            int iy = -4;

            TextureManager textureManager = minecraft.getTextureManager();
            if (textureManager != null)
            {
                RenderSystem.setShaderTexture(0, TEXTURES);
                if (tabPos != i && isHovering(ix - 10, iy, 20, 20, mouseX, mouseY))
                {
                    tabPos = i;
                    typePos = 0;
                    sectionPos = 0;
                    subTypePos = 0;
                    ingredientPos = 0;
                    playButtonSound();
                }
            }
        }

        //Tab Button Arrows TODO

        //Weapon Selection
        for (int i = sectionPos * 8; i < recipeList.size() && i < sectionPos * 8 + 8; i++)
        {
            int j = i - sectionPos * 8;
            int ix = 17 + j * 18;
            int iy = 34;

            if (typePos != i && isHovering(ix, iy, 16, 16, mouseX, mouseY))
            {
                typePos = i;
                subTypePos = 0;
                ingredientPos = 0;
                playButtonSound();
            }
        }

        //Weapon Selection Arrows
        int maxSections = (int) Math.ceil(recipeList.size() / 8f);
        if (maxSections > 1)
        {
            if (sectionPos + 1 < maxSections && isHovering(162, 36, 7, 11, mouseX, mouseY))
            {
                subTypePos = 0;
                sectionPos++;
                ingredientPos = 0;
                playButtonSound();
            } else if (sectionPos - 1 >= 0 && isHovering(7, 36, 7, 11, mouseX, mouseY))
            {
                subTypePos = 0;
                sectionPos--;
                ingredientPos = 0;
                playButtonSound();
            }
        }

        //Subtype Arrows
        int totalSubtypes = recipeList.isEmpty() ? 0 : recipeList.get(typePos).getAvailableRecipesTotal(player);
        if (!recipeList.isEmpty() && totalSubtypes > 1)
        {
            if (isHovering(126, 67, 7, 11, mouseX, mouseY) || isHovering(107, 66, 14, 14, mouseX, mouseY))
            {
                ingredientPos = 0;
                subTypePos = (subTypePos + 1) % totalSubtypes;


                playButtonSound();
            } else if (isHovering(43, 67, 7, 11, mouseX, mouseY) || isHovering(55, 66, 14, 14, mouseX, mouseY))
            {
                ingredientPos = 0;
                subTypePos--;
                if (subTypePos < 0)
                {
                    subTypePos = totalSubtypes - 1;
                }
                playButtonSound();
            }
        }

        //Ingredient Arrows
        maxSections = selectedRecipe == null ? 0 : (int) Math.ceil(selectedRecipe.getInput().size() / 8f);
        if (selectedRecipe != null && maxSections > 1)
        {
            if (sectionPos + 1 <= maxSections && isHovering(162, 110, 7, 11, mouseX, mouseY))
            {
                ingredientPos++;
                playButtonSound();
            } else if (sectionPos - 1 >= 0 && isHovering(7, 110, 7, 11, mouseX, mouseY))
            {
                ingredientPos--;
                playButtonSound();
            }
        }

        //Craft Button
        if (craftButtonState != -1 && isHovering(71, 93, 34, 12, mouseX, mouseY))
        {
            craftButtonState = 1;
            playButtonSound();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @SuppressWarnings("ConstantConditions")
    private void playButtonSound()
    {
        SoundManager soundHandler = minecraft.getSoundManager();
        if (soundHandler != null)
        {
            soundHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
