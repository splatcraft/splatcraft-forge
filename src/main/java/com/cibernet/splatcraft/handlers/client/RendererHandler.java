package com.cibernet.splatcraft.handlers.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.client.model.InkSquidModel;
import com.cibernet.splatcraft.client.model.SquidBumperModel;
import com.cibernet.splatcraft.client.renderer.PlayerSquidRenderer;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.IInkOverlayInfo;
import com.cibernet.splatcraft.data.capabilities.inkoverlay.InkOverlayCapability;
import com.cibernet.splatcraft.data.capabilities.playerinfo.IPlayerInfo;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.items.InkTankItem;
import com.cibernet.splatcraft.items.weapons.IChargeableWeapon;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

//@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RendererHandler
{
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
				squidRenderer.render(player, player.rotationYawHead, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<LivingEntity, InkSquidModel>(player, squidRenderer, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight()));
			}
			//else player.setInvisible(true);
		}
		//else event.getRenderer().getRenderManager().setRenderShadow(true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void livingRenderer(RenderLivingEvent.Post event)
	{
		LivingEntity entity = event.getEntity();

		int overlay = 0;
		int color = ColorUtils.DEFAULT;

		if(InkOverlayCapability.hasCapability(entity))
		{
			IInkOverlayInfo info = InkOverlayCapability.get(entity);
			color = info.getColor();
			overlay = (int) (Math.min(info.getAmount()/(entity instanceof SquidBumperEntity ? SquidBumperEntity.maxInkHealth : entity.getMaxHealth()) * 4, 4)-1);
		}

		if(overlay <= -1)
			return;

		MatrixStack matrixStack = event.getMatrixStack();
		float f = MathHelper.interpolateAngle(event.getPartialRenderTick(), entity.prevRenderYawOffset, entity.renderYawOffset);

		matrixStack.push();

		try {
			ObfuscationReflectionHelper.findMethod(LivingRenderer.class, "func_225621_a_", LivingEntity.class, MatrixStack.class, float.class, float.class, float.class)
					.invoke(event.getRenderer(), entity, matrixStack,(float) entity.ticksExisted + event.getPartialRenderTick(), f, event.getPartialRenderTick());
			ObfuscationReflectionHelper.findMethod(LivingRenderer.class, "func_225620_a_", LivingEntity.class, MatrixStack.class, float.class)
					.invoke(event.getRenderer(), entity, matrixStack, event.getPartialRenderTick());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		if (entity.getPose() == Pose.SLEEPING) {
			Direction direction = entity.getBedDirection();
			if (direction != null) {
				float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				matrixStack.translate((double)((float)(-direction.getXOffset()) * f4), 0.0D, (double)((float)(-direction.getZOffset()) * f4));
			}
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0D, (double)-1.501F, 0.0D);

		EntityModel model = event.getRenderer().getEntityModel();
		RenderType rendertype = model.getRenderType(new ResourceLocation(Splatcraft.MODID, "textures/entity/ink_overlay_"+overlay+".png"));
		IVertexBuilder ivertexbuilder = event.getBuffers().getBuffer(rendertype);
		int i = LivingRenderer.getPackedOverlay(entity, 0);

		float[] rgb = ColorUtils.hexToRGB(color);
		model.render(matrixStack, ivertexbuilder, event.getLight(), i, rgb[0], rgb[1], rgb[2], 1);
		matrixStack.pop();
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
			float yOff = -0.5f*(((time-event.getPartialTicks())/maxTime));// - (tickTime/20f));
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
			//TreeMap<String, AbstractClientPlayerEntity> players = Maps.newTreeMap();
			//Minecraft.getInstance().world.getPlayers().forEach(player -> players.put(player.getDisplayName().getString(), player));


			List<String> players = new ArrayList<>();
			Minecraft.getInstance().getConnection().getPlayerInfoMap().forEach((info) ->
			{
				players.add(getDisplayName(info).getString());
			});

			for(Object obj : component.getFormatArgs())
			{
				if(!(obj instanceof TextComponent))
					continue;
				TextComponent msgChildren = (TextComponent) obj;
				String key = msgChildren.getString();

				if(players.contains(key))
					msgChildren.setStyle(Style.EMPTY.setColor(Color.fromInt(ClientUtils.getClientPlayerColor(key))));
			}
		}
	}
	
	@SubscribeEvent
	public static void renderNameplate(RenderNameplateEvent event)
	{
		if(SplatcraftGameRules.getBooleanRuleValue(event.getEntity().world, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getEntity() instanceof LivingEntity)
		{
			int color = ColorUtils.getEntityColor((LivingEntity) event.getEntity());
			if(SplatcraftConfig.Client.getColorLock())
				color = ColorUtils.getLockedColor(color);
			if(color != -1)
				event.setContent(((TextComponent)event.getContent()).setStyle(Style.EMPTY.setColor(Color.fromInt(color))));
		}
	}


	public static ITextComponent getDisplayName(NetworkPlayerInfo info) {
		return info.getDisplayName() != null ? info.getDisplayName().deepCopy() : ScorePlayerTeam.func_237500_a_(info.getPlayerTeam(), new StringTextComponent(info.getGameProfile().getName()));
	}
	private static int squidTime = 0;
	private static float prevInkPctg = 0;
	private static float inkFlash = 0;
	private static final ResourceLocation WIDGETS = new ResourceLocation(Splatcraft.MODID, "textures/gui/widgets.png");

	@SubscribeEvent
	public static void renderGui(RenderGameOverlayEvent event)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(player == null || !PlayerInfoCapability.hasCapability(player))
			return;
		IPlayerInfo info = PlayerInfoCapability.get(player);

		int width = Minecraft.getInstance().getMainWindow().getScaledWidth();
		int height = Minecraft.getInstance().getMainWindow().getScaledHeight();

		if(event instanceof RenderGameOverlayEvent.Pre && event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(player.getHeldItemMainhand().getItem() instanceof IChargeableWeapon || player.getHeldItemOffhand().getItem() instanceof IChargeableWeapon)
			{

				MatrixStack matrixStack = event.getMatrixStack();
				matrixStack.push();
				RenderSystem.enableBlend();
				Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS);

				AbstractGui.blit(matrixStack, width / 2 - 15, height / 2 + 14, 30, 9, 88, 0, 30, 9, 256, 256);
				if(PlayerInfoCapability.hasCapability(player) && PlayerInfoCapability.get(player).getPlayerCharge() != null)
				{
					float charge = PlayerInfoCapability.get(player).getPlayerCharge().charge;
					AbstractGui.blit(matrixStack, width / 2 - 15, height / 2 + 14, (int) (30*charge), 9, 88, 9, (int) (30*charge), 9, 256, 256);
				}

				matrixStack.pop();
			}
		}

		if(info.isSquid())
		{
			if(event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR))
			{
				squidTime++;
				float inkPctg = 0;
				boolean hasTank = player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof InkTankItem;
				if(hasTank)
				{
					ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
					inkPctg = InkTankItem.getInkAmount(stack) / ((InkTankItem) stack.getItem()).capacity;
				}

				if (SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.CROSSHAIR))
				{
					int heightAnim = Math.min(14, squidTime);
					int glowAnim = Math.max(0, Math.min(18, squidTime - 16));
					float[] rgb = ColorUtils.hexToRGB(info.getColor());

					boolean canUse = true;

					if (hasTank) {
						ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

						canUse = player.getActiveHand() != null ? ((InkTankItem) stack.getItem()).canUse(player.getHeldItem(player.getActiveHand()).getItem())
								: ((InkTankItem) stack.getItem()).canUse(player.getHeldItemMainhand().getItem()) || ((InkTankItem) stack.getItem()).canUse(player.getHeldItemOffhand().getItem());
					}

					MatrixStack matrixStack = event.getMatrixStack();
					matrixStack.push();
					RenderSystem.enableBlend();
					Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS);

					if(SplatcraftGameRules.getBooleanRuleValue(player.world, SplatcraftGameRules.REQUIRE_INK_TANK)) {
						AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9 + (14 - heightAnim), 18, 2, 0, 95, 18, 2, 256, 256);
						AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9 + (14 - heightAnim), 18, 4 + heightAnim, 0, 95, 18, 4 + heightAnim, 256, 256);

						if (inkPctg != prevInkPctg && inkPctg == 1)
							inkFlash = 0.1f;
						inkFlash = Math.max(0, inkFlash - 0.002f);

						float inkPctgLerp = lerp(prevInkPctg, inkPctg, 0.05f);
						float inkSize = ((1 - inkPctg) * 18);

						RenderSystem.color3f(rgb[0] + inkFlash, rgb[1] + inkFlash, rgb[2] + inkFlash);
						matrixStack.translate(0, inkSize - Math.floor(inkSize), 0);
						AbstractGui.blit(matrixStack, width / 2 + 9, (int) (height / 2 - 9 + (14 - heightAnim) + (1 - inkPctgLerp) * 18), 18, (int) ((4 + heightAnim) * inkPctgLerp), 18, 95 + inkSize, 18, (int) ((4 + heightAnim) * inkPctg), 256, 256);
						matrixStack.translate(0, -(inkSize - Math.floor(inkSize)), 0);

						if (SplatcraftConfig.Client.vanillaInkDurability.get()) {
							float[] durRgb = ColorUtils.hexToRGB(MathHelper.hsvToRGB(Math.max(0.0F, (inkPctgLerp)) / 3.0F, 1.0F, 1.0F));
							RenderSystem.color3f(durRgb[0], durRgb[1], durRgb[2]);
						} else RenderSystem.color3f(rgb[0], rgb[1], rgb[2]);

						AbstractGui.blit(matrixStack, width / 2 + 9 + (18 - glowAnim), height / 2 - 9, glowAnim, 18, 18 - glowAnim, 113, glowAnim, 18, 256, 256);

						RenderSystem.color3f(1, 1, 1);
						if (glowAnim >= 18 && (SplatcraftTags.Items.MATCH_ITEMS.contains(player.getHeldItemMainhand().getItem()) || SplatcraftTags.Items.MATCH_ITEMS.contains(player.getHeldItemOffhand().getItem()))) {
							if (!hasTank)
								AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9, 18, 112, 18, 18, 256, 256);
							else if (!canUse)
								AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9, 36, 112, 18, 18, 256, 256);
						}
					}
					else
					{
						AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9 + (14 - heightAnim), 18, 2, 0, 131, 18, 2, 256, 256);
						AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9 + (14 - heightAnim), 18, 4 + heightAnim, 0, 131, 18, 4 + heightAnim, 256, 256);

						RenderSystem.color3f(rgb[0], rgb[1], rgb[2]);

						AbstractGui.blit(matrixStack, width / 2 + 9, height / 2 - 9 + (14 - heightAnim), 18, 4 + heightAnim, 18, 131, 18, 4 + heightAnim, 256, 256);
						AbstractGui.blit(matrixStack, width / 2 + 9 + (18 - glowAnim), height / 2 - 9, glowAnim, 18, 18 - glowAnim, 149, glowAnim, 18, 256, 256);
					}
					matrixStack.pop();
				}
				prevInkPctg = inkPctg;
			}
		}
		else squidTime = 0;

	}

	private static float lerp (float a, float b, float f)
	{
		return a + f * (b - a);
	}
}
