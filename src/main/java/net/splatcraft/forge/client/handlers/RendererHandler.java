package net.splatcraft.forge.client.handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.layer.InkAccessoryLayer;
import net.splatcraft.forge.client.layer.InkOverlayLayer;
import net.splatcraft.forge.client.renderer.InkSquidRenderer;
import net.splatcraft.forge.client.renderer.SquidFormRenderer;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.SquidFormPlayer;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.items.InkTankItem;
import net.splatcraft.forge.items.weapons.ChargerItem;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import net.splatcraft.forge.util.PlayerCooldown;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.splatcraft.forge.items.weapons.WeaponBaseItem.enoughInk;

//@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class RendererHandler
{
    /*
    @Deprecated
    public static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () ->
    {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () ->
    {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    */
    private static final ResourceLocation WIDGETS = new ResourceLocation(Splatcraft.MODID, "textures/gui/widgets.png");
    private static SquidFormRenderer squidRenderer = null;
    private static float tickTime = 0;
    private static float oldCooldown = 0;
    private static int squidTime = 0;
    private static float prevInkPctg = 0;
    private static float inkFlash = 0;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerRender(RenderPlayerEvent event)
    {
        Player player = event.getPlayer();
        if (player.isSpectator()) return;

        if (PlayerInfoCapability.isSquid(player))
        {
            event.setCanceled(true);
            if (squidRenderer == null)
                squidRenderer = new SquidFormRenderer(InkSquidRenderer.getRenderManager());
            if (!InkBlockUtils.canSquidHide(player))
            {
                squidRenderer.render(player, player.yHeadRot, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
                //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<>(player, squidRenderer, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
            }
            //else player.setInvisible(true);
        }

        //event.getRenderer().getDispatcher().setRenderShadow(false);
    }

    /*
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingRenderer(RenderLivingEvent.Pre<?, ?> event)
    {
        if(!hasCustomLayers.contains(event.getRenderer()))
        {
            if(event.getRenderer() instanceof PlayerRenderer)
                ((PlayerRenderer)event.getRenderer()).addLayer(new InkAccessoryLayer((PlayerRenderer)event.getRenderer()));
            event.getRenderer().addLayer(new InkOverlayLayer(event.getRenderer()));

            hasCustomLayers.add(event.getRenderer());
        }
    }
    */

    private static Field field_EntityRenderersEvent$AddLayers_renderers;

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void addRenderLayers(EntityRenderersEvent.AddLayers event)
    {
        //code from https://github.com/AlexModGuy/Rats/blob/d95ba97546663088be6804b6400226d18a9b0273/src/main/java/com/github/alexthe666/rats/client/events/ModClientEvents.java#L309
        if (field_EntityRenderersEvent$AddLayers_renderers == null) {
            try {
                field_EntityRenderersEvent$AddLayers_renderers = EntityRenderersEvent.AddLayers.class.getDeclaredField("renderers");
                field_EntityRenderersEvent$AddLayers_renderers.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (field_EntityRenderersEvent$AddLayers_renderers != null) {
            event.getSkins().forEach(renderer -> {
                LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> skin = event.getSkin(renderer);
                attachInkOverlay(Objects.requireNonNull(skin));

                skin.addLayer(new InkAccessoryLayer(skin, new HumanoidModel(event.getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
            });
            try {
                ((Map<EntityType<?>, EntityRenderer<?>>) field_EntityRenderersEvent$AddLayers_renderers.get(event))
                        .values().stream()
                        .filter(LivingEntityRenderer.class::isInstance)
                        .map(LivingEntityRenderer.class::cast)
                        .forEach(RendererHandler::attachInkOverlay);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void attachInkOverlay(LivingEntityRenderer<T, M> renderer)
    {
        renderer.addLayer(new InkOverlayLayer(renderer));
    }


    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if (PlayerCooldown.hasPlayerCooldown(player) && !player.isSpectator()) {
            player.getInventory().selected = PlayerCooldown.getPlayerCooldown(player).getSlotIndex();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerRenderPost(RenderPlayerEvent.Post event)
    {
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if (PlayerInfoCapability.isSquid(player))
        {
            event.setCanceled(true);
            return;
        }



        if (PlayerCooldown.hasPlayerCooldown(player) && PlayerCooldown.getPlayerCooldown(player).getHand().equals(event.getHand()))
        {
            PlayerCooldown cooldown = PlayerCooldown.getPlayerCooldown(player);
            float time = (float) cooldown.getTime();
            float maxTime = (float) cooldown.getMaxTime();
            if (time != oldCooldown)
            {
                oldCooldown = time;
                tickTime = 0;
            }
            tickTime = (tickTime + 1) % 10;
            float yOff = -0.5f * ((time - event.getPartialTicks()) / maxTime);// - (tickTime/20f));

            if (player != null && player.getItemInHand(event.getHand()).getItem() instanceof WeaponBaseItem)
            {
                switch (((WeaponBaseItem) player.getItemInHand(event.getHand()).getItem()).getPose())
                {
                    case ROLL:
                        yOff = -((time - event.getPartialTicks()) / maxTime) + 0.5f;
                        break;
                    case BRUSH:
                        event.getPoseStack().mulPose(Vector3f.YN.rotation(yOff * ((player.getMainArm() == HumanoidArm.RIGHT ? event.getHand().equals(InteractionHand.MAIN_HAND) : event.getHand().equals(InteractionHand.OFF_HAND)) ? 1 : -1)));
                        yOff = 0;
                        break;
                }
            }

            event.getPoseStack().translate(0, yOff, 0);
        } else
        {
            tickTime = 0;
        }
    }

    /* TODO
    @SubscribeEvent
    public static void onItemRender(RenderItemEvent event)
    {
        if (event instanceof RenderItemEvent.Gui.Pre && event.getItem().getItem().equals(SplatcraftItems.powerEgg))
        {
            IBakedModel modelIn = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(event.getItem().getItem().getRegistryName() + "#inventory"));
            renderItem(event.getItem(), event.getTransformType(), true, event.getMatrixStack(), event.getRenderTypeBuffer(), event.getLight(), event.getOverlay(), modelIn);
            event.setCanceled(true);
        }
        else if(event.getItem().getItem() instanceof SubWeaponItem)
        {
            PoseStack matrixStack = event.getMatrixStack();
            AbstractSubWeaponEntity sub = ((SubWeaponItem)event.getItem().getItem()).entityType.create(Minecraft.getInstance().player.level);
            sub.setColor(ColorUtils.getInkColor(event.getItem()));
            sub.setItem(event.getItem());
            sub.readItemData(event.getItem().getOrCreateTag().getCompound("EntityData"));

            sub.isItem = true;

            Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(event.getItem().getItem().getRegistryName() + "#inventory"))
                    .handlePerspective(event.get(), matrixStack);
            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(sub).render(sub, 0, event.getPartialTicks(), matrixStack, event.getRenderTypeBuffer(), event.getLight());
            if(!matrixStack.clear())
                matrixStack.popPose();
            event.setCanceled(true);
        }
    }
    */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemRenderHand(RenderHandEvent event)
    {
        PoseStack matrixStack = event.getPoseStack();

        matrixStack.pushPose();
        if(event.getItemStack().getItem() instanceof SubWeaponItem)
        {
            HumanoidArm handside = event.getHand() == InteractionHand.MAIN_HAND ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
            AbstractSubWeaponEntity sub = ((SubWeaponItem)event.getItemStack().getItem()).entityType.get().create(Minecraft.getInstance().player.level);
            sub.setColor(ColorUtils.getInkColor(event.getItemStack()));
            sub.setItem(event.getItemStack());
            sub.readItemData(event.getItemStack().getOrCreateTag().getCompound("EntityData"));

            sub.isItem = true;

            float p_228405_5_ = event.getSwingProgress();
            float p_228405_7_ = event.getEquipProgress();

            float f5 = -0.4F * Mth.sin(Mth.sqrt(p_228405_5_) * (float)Math.PI);
            float f6 = 0.2F * Mth.sin(Mth.sqrt(p_228405_5_) * ((float)Math.PI * 2F));
            float f10 = -0.2F * Mth.sin(p_228405_5_ * (float)Math.PI);
            int l = handside == HumanoidArm.RIGHT ? 1 : -1;
            matrixStack.translate((float) l * f5, f6, f10);
            applyItemArmTransform(matrixStack, handside, p_228405_7_);
            applyItemArmAttackTransform(matrixStack, handside, p_228405_5_);

            Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(event.getItemStack().getItem().getRegistryName() + "#inventory"))
                    .handlePerspective(handside == HumanoidArm.RIGHT ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, matrixStack);

            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(sub).render(sub, 0, event.getPartialTicks(), matrixStack, event.getMultiBufferSource(), event.getPackedLight());
            if(!matrixStack.clear())
                matrixStack.popPose();
            event.setCanceled(true);
        }
         matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private static void applyItemArmTransform(PoseStack p_228406_1_, HumanoidArm p_228406_2_, float p_228406_3_) {
        int i = p_228406_2_ == HumanoidArm.RIGHT ? 1 : -1;
        p_228406_1_.translate((float) i * 0.56F, -0.52F + p_228406_3_ * -0.6F, -0.72F);
    }

    @OnlyIn(Dist.CLIENT)
    private static void applyItemArmAttackTransform(PoseStack p_228399_1_, HumanoidArm p_228399_2_, float p_228399_3_) {
        int i = p_228399_2_ == HumanoidArm.RIGHT ? 1 : -1;
        float f = Mth.sin(p_228399_3_ * p_228399_3_ * (float)Math.PI);
        p_228399_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
        float f1 = Mth.sin(Mth.sqrt(p_228399_3_) * (float)Math.PI);
        p_228399_1_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
        p_228399_1_.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
        p_228399_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));
    }

    /*
    protected static void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, PoseStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn)
    {
        if (!itemStackIn.isEmpty())
        {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            matrixStackIn.pushPose();
            boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI || transformTypeIn == ItemCameraTransforms.TransformType.GROUND || transformTypeIn == ItemCameraTransforms.TransformType.FIXED;
            if (itemStackIn.getItem() == Items.TRIDENT && flag)
            {
                modelIn = itemRenderer.getItemModelShaper().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            }

            modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, transformTypeIn, leftHand);
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            if (!modelIn.isCustomRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag))
            {
                boolean flag1;
                if (transformTypeIn != ItemCameraTransforms.TransformType.GUI && !transformTypeIn.firstPerson() && itemStackIn.getItem() instanceof BlockItem)
                {
                    Block block = ((BlockItem) itemStackIn.getItem()).getBlock();
                    flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
                } else
                {
                    flag1 = true;
                }
                if (modelIn.isLayered())
                {
                    net.minecraftforge.client.ForgeHooksClient.drawItemLayered(itemRenderer, modelIn, itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, flag1);
                } else
                {
                    RenderType rendertype = getItemEntityTranslucent(PlayerContainer.BLOCK_ATLAS);
                    IVertexBuilder ivertexbuilder;
                    if (itemStackIn.getItem() == Items.COMPASS && itemStackIn.hasFoil())
                    {
                        matrixStackIn.pushPose();
                        PoseStack.Entry matrixstack$entry = matrixStackIn.last();
                        if (transformTypeIn == ItemCameraTransforms.TransformType.GUI)
                        {
                            matrixstack$entry.pose().multiply(0.5F);
                        } else if (transformTypeIn.firstPerson())
                        {
                            matrixstack$entry.pose().multiply(0.75F);
                        }

                        if (flag1)
                        {
                            ivertexbuilder = ItemRenderer.getCompassFoilBufferDirect(bufferIn, rendertype, matrixstack$entry);
                        } else
                        {
                            ivertexbuilder = ItemRenderer.getCompassFoilBuffer(bufferIn, rendertype, matrixstack$entry);
                        }

                        matrixStackIn.popPose();
                    } else if (flag1)
                    {
                        ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, rendertype, true, itemStackIn.hasFoil());
                    } else
                    {
                        ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, rendertype, true, itemStackIn.hasFoil());
                    }

                    itemRenderer.renderModelLists(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
                }
            } else
            {
                itemStackIn.getItem().getItemStackTileEntityRenderer().renderByItem(itemStackIn, transformTypeIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }

            matrixStackIn.popPose();
        }
    }

    protected static RenderType getItemEntityTranslucent(ResourceLocation locationIn)
    {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                /*.target(field_241712_U_)*\/.setDiffuseLightingState(new RenderState.DiffuseLightingState(true)).setAlphaState(new RenderState.AlphaState(0.003921569F)).setLightmapState(new RenderState.LightmapState(true))
                .setOverlayState(new RenderState.OverlayState(true)).createCompositeState(true);
        return RenderType.create("item_entity_translucent", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, rendertype$state);
    }
    */

    @SubscribeEvent
    public static void onChatMessage(ClientChatReceivedEvent event)
    {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && SplatcraftGameRules.getBooleanRuleValue(level, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getMessage() instanceof TranslatableComponent)
        {
            TranslatableComponent component = (TranslatableComponent) event.getMessage();
            //TreeMap<String, AbstractClientPlayer> players = Maps.newTreeMap();
            //Minecraft.getInstance().level.getPlayers().forEach(player -> players.put(player.getDisplayName().getString(), player));


            List<String> players = new ArrayList<>();
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null)
                connection.getOnlinePlayers().forEach(info -> players.add(getDisplayName(info).getString()));

            for (Object obj : component.getArgs())
            {
                if (!(obj instanceof MutableComponent))
                    continue;

                MutableComponent msgChildren = (MutableComponent) obj;
                String key = msgChildren.getString();

                if (!msgChildren.getSiblings().isEmpty() && players.contains(key))
                    msgChildren.setStyle(msgChildren.getStyle().withColor(TextColor.fromRgb(ClientUtils.getClientPlayerColor(key))));
            }
        }
    }

    @SubscribeEvent
    public static void renderNameplate(RenderNameplateEvent event)
    {
        if (SplatcraftGameRules.getBooleanRuleValue(event.getEntity().level, SplatcraftGameRules.COLORED_PLAYER_NAMES) && event.getEntity() instanceof LivingEntity)
        {
            int color = ColorUtils.getEntityColor(event.getEntity());
            if (SplatcraftConfig.Client.getColorLock())
            {
                color = ColorUtils.getLockedColor(color);
            }
            if (color != -1)
            {
                event.setContent(((MutableComponent) event.getContent()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
            }
        }
    }

    public static Component getDisplayName(PlayerInfo info)
    {
        return info.getTabListDisplayName() != null ? info.getTabListDisplayName().copy() : PlayerTeam.formatNameForTeam(info.getTeam(), new TextComponent(info.getProfile().getName()));
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void renderGui(RenderGameOverlayEvent event)
    {
        Player player = Minecraft.getInstance().player;
        if (player == null || !PlayerInfoCapability.hasCapability(player))
        {
            return;
        }
        net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfo info = PlayerInfoCapability.get(player);

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (event instanceof RenderGameOverlayEvent.Pre && event.getType().equals(RenderGameOverlayEvent.ElementType.LAYER))
        {
            if (player.getMainHandItem().getItem() instanceof ChargerItem || player.getOffhandItem().getItem() instanceof ChargerItem)
            {

                PoseStack matrixStack = event.getMatrixStack();
                matrixStack.pushPose();
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, WIDGETS);

                Screen.blit(matrixStack, width / 2 - 15, height / 2 + 14, 30, 9, 88, 0, 30, 9, 256, 256);
                if (PlayerInfoCapability.hasCapability(player) && PlayerInfoCapability.get(player).getPlayerCharge() != null) {
                    float charge = PlayerInfoCapability.get(player).getPlayerCharge().charge;
                    Screen.blit(matrixStack, width / 2 - 15, height / 2 + 14, (int) (30 * charge), 9, 88, 9, (int) (30 * charge), 9, 256, 256);
                }

                matrixStack.popPose();
            }
        }

        boolean showCrosshairInkIndicator = SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.BOTH) || SplatcraftConfig.Client.inkIndicator.get().equals(SplatcraftConfig.InkIndicator.CROSSHAIR);
        boolean isHoldingMatchItem = player.getMainHandItem().is(SplatcraftTags.Items.MATCH_ITEMS) || player.getOffhandItem().is(SplatcraftTags.Items.MATCH_ITEMS);
        boolean showLowInkWarning = showCrosshairInkIndicator && SplatcraftConfig.Client.lowInkWarning.get() && (isHoldingMatchItem || info.isSquid()) && !enoughInk(player, null, 10f, 0, false);

        boolean canUse = true;
        boolean hasTank = player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof InkTankItem;
        float inkPctg = 0;
        if (hasTank) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            inkPctg = InkTankItem.getInkAmount(stack) / ((InkTankItem) stack.getItem()).capacity;
            if (isHoldingMatchItem)
                canUse = ((InkTankItem) stack.getItem()).canUse(player.getMainHandItem().getItem()) || ((InkTankItem) stack.getItem()).canUse(player.getOffhandItem().getItem());
        }
        if (info.isSquid() || showLowInkWarning || !canUse) {
            if (event.getType().equals(RenderGameOverlayEvent.ElementType.LAYER)) {
                squidTime++;

                if (showCrosshairInkIndicator) {
                    int heightAnim = Math.min(14, squidTime);
                    int glowAnim = Math.max(0, Math.min(18, squidTime - 16));
                    float[] rgb = ColorUtils.hexToRGB(info.getColor());

                    PoseStack matrixStack = event.getMatrixStack();
                    matrixStack.pushPose();
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderTexture(0, WIDGETS);

                    if (enoughInk(player, null, 220, 0, false)) { // checks if you have unlimited ink
                        Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9 + 14 - heightAnim, 18, 2, 0, 131, 18, 2, 256, 256);
                        Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9 + 14 - heightAnim, 18, 4 + heightAnim, 0, 131, 18, 4 + heightAnim, 256, 256);

                        RenderSystem.setShaderColor(rgb[0], rgb[1], rgb[2], 1);

                        Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9 + 14 - heightAnim, 18, 4 + heightAnim, 18, 131, 18, 4 + heightAnim, 256, 256);
                        Screen.blit(matrixStack, width / 2 + 9 + 18 - glowAnim, height / 2 - 9, glowAnim, 18, 18 - glowAnim, 149, glowAnim, 18, 256, 256);
                    } else {
                        Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9 + 14 - heightAnim, 18, 2, 0, 95, 18, 2, 256, 256);
                        Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9 + 14 - heightAnim, 18, 4 + heightAnim, 0, 95, 18, 4 + heightAnim, 256, 256);

                        if (inkPctg != prevInkPctg && inkPctg == 1) {
                            inkFlash = 0.1f;
                        }
                        inkFlash = Math.max(0, inkFlash - 0.002f);

                        float inkPctgLerp = lerp(prevInkPctg, inkPctg, 0.05f);
                        float inkSize = (1 - inkPctg) * 18;

                        RenderSystem.setShaderColor(rgb[0] + inkFlash, rgb[1] + inkFlash, rgb[2] + inkFlash, 1);
                        matrixStack.translate(0, inkSize - Math.floor(inkSize), 0);
                        Screen.blit(matrixStack, width / 2 + 9, (int) (height / 2 - 9 + (14 - heightAnim) + (1 - inkPctgLerp) * 18), 18, (int) ((4 + heightAnim) * inkPctgLerp), 18, 95 + inkSize, 18, (int) ((4 + heightAnim) * inkPctg), 256, 256);
                        matrixStack.translate(0, -(inkSize - Math.floor(inkSize)), 0);

                        if (SplatcraftConfig.Client.vanillaInkDurability.get())
                        {
                            float[] durRgb = ColorUtils.hexToRGB(Mth.hsvToRgb(Math.max(0.0F, inkPctgLerp) / 3.0F, 1.0F, 1.0F));
                            RenderSystem.setShaderColor(durRgb[0], durRgb[1], durRgb[2], 1);
                        } else
                        {
                            RenderSystem.setShaderColor(rgb[0], rgb[1], rgb[2], 1);
                        }

                        Screen.blit(matrixStack, width / 2 + 9 + 18 - glowAnim, height / 2 - 9, glowAnim, 18, 18 - glowAnim, 113, glowAnim, 18, 256, 256);

                        RenderSystem.setShaderColor(1, 1, 1, 1);
                        if (glowAnim == 18) {
                            if (!canUse) {
                                Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9, 36, 112, 18, 18, 256, 256);
                            } else if (showLowInkWarning) {
                                Screen.blit(matrixStack, width / 2 + 9, height / 2 - 9, 18, 112, 18, 18, 256, 256);
                            }
                        }
                    }
                    matrixStack.popPose();
                }
                prevInkPctg = inkPctg;
            }
        } else
        {
            squidTime = 0;
        }

    }

    private static float lerp(float a, float b, float f)
    {
        return a + f * (b - a);
    }
}
