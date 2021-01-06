package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.client.renderer.InkSquidRenderer;
import com.cibernet.splatcraft.client.renderer.PlayerSquidRenderer;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RendererHandler
{

	public static final ArrayList<ResourceLocation> textures = new ArrayList<>();
	
	private static PlayerSquidRenderer squidRenderer = null;
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerRender(RenderPlayerEvent event)
	{
		PlayerEntity player = event.getPlayer();
		
		if(PlayerInfoCapability.isSquid(player))
		{
			event.setCanceled(true);
			if(squidRenderer == null)
				squidRenderer = new PlayerSquidRenderer(event.getRenderer().getRenderManager());
			if(!InkBlockUtils.canSquidHide(player))
			{

				//squidRenderer.getRenderManager().setRenderShadow(true);
				squidRenderer.render(player, player.rotationYawHead, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
			}
			//else player.setInvisible(true);
		}
		//else event.getRenderer().getRenderManager().setRenderShadow(true);
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(player != null && PlayerCooldown.hasPlayerCooldown(player))
			player.inventory.currentItem = PlayerCooldown.getPlayerCooldown(player).getSlotIndex();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerRenderPost(RenderPlayerEvent.Post event)
	{
	}
	
	private static float tickTime = 0;
	private static float oldCooldown = 0;
	
	@SubscribeEvent
	public static void renderHand(RenderHandEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(PlayerInfoCapability.isSquid(player))
		{
			event.setCanceled(true);
			return;
		}
		
		if(PlayerCooldown.hasPlayerCooldown(player))
		{
			PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
			float time = (float)cooldown.getTime();
			float maxTime = (float)cooldown.getMaxTime();
			if(time != oldCooldown)
			{
				oldCooldown = time;
				tickTime = 0;
			}
			tickTime = (tickTime+1) % 10;
			float yOff = -0.5f*((time/maxTime));// - (tickTime/20f));
			event.getMatrixStack().translate(0, yOff, 0);
		}
		else tickTime = 0;
	}

	@SubscribeEvent
	public static void onItemRenderGui(RenderItemEvent.Gui.Pre event)
	{
		if(event.getItem().getItem().equals(SplatcraftItems.powerEgg))
		{
			IBakedModel modelIn = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation(event.getItem().getItem().getRegistryName() + "#inventory"));
			renderItem(event.getItem(), event.getTransformType(), true, event.getMatrixStack(), event.getRenderTypeBuffer(), event.getLight(), event.getOverlay(), modelIn);
			event.setCanceled(true);
		}
	}


	protected static void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn)
	{
		if (!itemStackIn.isEmpty())
		{
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

			matrixStackIn.push();
			boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI || transformTypeIn == ItemCameraTransforms.TransformType.GROUND || transformTypeIn == ItemCameraTransforms.TransformType.FIXED;
			if (itemStackIn.getItem() == Items.TRIDENT && flag) {
				modelIn = itemRenderer.getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
			}

			modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, transformTypeIn, leftHand);
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			if (!modelIn.isBuiltInRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag)) {
				boolean flag1;
				if (transformTypeIn != ItemCameraTransforms.TransformType.GUI && !transformTypeIn.isFirstPerson() && itemStackIn.getItem() instanceof BlockItem) {
					Block block = ((BlockItem)itemStackIn.getItem()).getBlock();
					flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
				} else {
					flag1 = true;
				}
				if (modelIn.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(itemRenderer, modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, flag1); }
				else {
					RenderType rendertype = getItemEntityTranslucent(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
					IVertexBuilder ivertexbuilder;
					if (itemStackIn.getItem() == Items.COMPASS && itemStackIn.hasEffect()) {
						matrixStackIn.push();
						MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
						if (transformTypeIn == ItemCameraTransforms.TransformType.GUI) {
							matrixstack$entry.getMatrix().mul(0.5F);
						} else if (transformTypeIn.isFirstPerson()) {
							matrixstack$entry.getMatrix().mul(0.75F);
						}

						if (flag1) {
							ivertexbuilder = itemRenderer.getDirectGlintVertexBuilder(bufferIn, rendertype, matrixstack$entry);
						} else {
							ivertexbuilder = itemRenderer.getGlintVertexBuilder(bufferIn, rendertype, matrixstack$entry);
						}

						matrixStackIn.pop();
					} else if (flag1) {
						ivertexbuilder = itemRenderer.getEntityGlintVertexBuilder(bufferIn, rendertype, true, itemStackIn.hasEffect());
					} else {
						ivertexbuilder = itemRenderer.getBuffer(bufferIn, rendertype, true, itemStackIn.hasEffect());
					}

					itemRenderer.renderModel(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
				}
			} else {
				itemStackIn.getItem().getItemStackTileEntityRenderer().func_239207_a_(itemStackIn, transformTypeIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			}

			matrixStackIn.pop();
		}
	}


	@Deprecated
	public static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	protected static RenderType getItemEntityTranslucent(ResourceLocation locationIn)
	{
		RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(TRANSLUCENT_TRANSPARENCY)
				/*.target(field_241712_U_)*/.diffuseLighting(new RenderState.DiffuseLightingState(true)).alpha(new RenderState.AlphaState(0.003921569F)).lightmap(new RenderState.LightmapState(true))
				.overlay(new RenderState.OverlayState(true)).build(true);
		return RenderType.makeType("item_entity_translucent", DefaultVertexFormats.ENTITY, 7, 256, true, false, rendertype$state);
	}


	@SubscribeEvent
	public static void onChatMessage(ClientChatReceivedEvent event)
	{
		if(SplatcraftGameRules.getBooleanRuleValue(Minecraft.getInstance().world, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getMessage() instanceof TranslationTextComponent)
		{
			TranslationTextComponent component = (TranslationTextComponent) event.getMessage();
			TreeMap<String, AbstractClientPlayerEntity> players = Maps.newTreeMap();
			Minecraft.getInstance().world.getPlayers().forEach(player -> players.put(player.getDisplayName().getString(), player));
			
			for(Object obj : component.getFormatArgs())
			{
				if(!(obj instanceof TextComponent))
					continue;
				TextComponent msgChildren = (TextComponent) obj;
				String key = msgChildren.getString();
				
				if(players.containsKey(key))
					msgChildren.setStyle(Style.EMPTY.setColor(Color.fromInt(ColorUtils.getPlayerColor(players.get(key)))));
			}
		}
	}
	
	@SubscribeEvent
	public static void renderNameplate(RenderNameplateEvent event)
	{
		if(SplatcraftGameRules.getBooleanRuleValue(event.getEntity().world, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getEntity() instanceof LivingEntity)
		{
			int color = ColorUtils.getEntityColor((LivingEntity) event.getEntity());
			if(color != -1)
				event.setContent(((TextComponent)event.getContent()).setStyle(Style.EMPTY.setColor(Color.fromInt(color))));
		}
	}
	
	
}
