package net.splatcraft.forge.registries;

import java.util.Objects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.client.layer.InkAccessoryLayer;
import net.splatcraft.forge.client.layer.InkOverlayLayer;
import net.splatcraft.forge.client.layer.PlayerInkColoredSkinLayer;
import net.splatcraft.forge.client.models.InkSquidModel;
import net.splatcraft.forge.client.models.SquidBumperModel;
import net.splatcraft.forge.client.models.inktanks.ArmoredInkTankModel;
import net.splatcraft.forge.client.models.inktanks.ClassicInkTankModel;
import net.splatcraft.forge.client.models.inktanks.InkTankJrModel;
import net.splatcraft.forge.client.models.inktanks.InkTankModel;
import net.splatcraft.forge.client.models.projectiles.BlasterInkProjectileModel;
import net.splatcraft.forge.client.models.projectiles.InkProjectileModel;
import net.splatcraft.forge.client.models.projectiles.RollerInkProjectileModel;
import net.splatcraft.forge.client.models.projectiles.ShooterInkProjectileModel;
import net.splatcraft.forge.client.models.subs.BurstBombModel;
import net.splatcraft.forge.client.models.subs.CurlingBombModel;
import net.splatcraft.forge.client.models.subs.SplatBombModel;
import net.splatcraft.forge.client.models.subs.SuctionBombModel;
import net.splatcraft.forge.client.renderer.InkProjectileRenderer;
import net.splatcraft.forge.client.renderer.InkSquidRenderer;
import net.splatcraft.forge.client.renderer.SpawnShieldRenderer;
import net.splatcraft.forge.client.renderer.SquidBumperRenderer;
import net.splatcraft.forge.client.renderer.subs.BurstBombRenderer;
import net.splatcraft.forge.client.renderer.subs.CurlingBombRenderer;
import net.splatcraft.forge.client.renderer.subs.SplatBombRenderer;
import net.splatcraft.forge.client.renderer.subs.SuctionBombRenderer;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.entities.InkSquidEntity;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import net.splatcraft.forge.entities.subs.CurlingBombEntity;
import net.splatcraft.forge.entities.subs.SplatBombEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import net.splatcraft.forge.mixin.AddLayersAccessor;

import static net.splatcraft.forge.Splatcraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftEntities {
    protected static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<InkSquidEntity>> INK_SQUID = create("ink_squid", InkSquidEntity::new, MobCategory.AMBIENT, 0.6f, 0.5f);

    public static final RegistryObject<EntityType<InkProjectileEntity>> INK_PROJECTILE = create("ink_projectile", InkProjectileEntity::new, MobCategory.MISC);
    public static final RegistryObject<EntityType<SquidBumperEntity>> SQUID_BUMPER = create("squid_bumper", SquidBumperEntity::new, MobCategory.MISC, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<SpawnShieldEntity>> SPAWN_SHIELD = create("spawn_shield", SpawnShieldEntity::new, MobCategory.MISC, 1, 1);

    //Sub Weapons
    public static final RegistryObject<EntityType<BurstBombEntity>> BURST_BOMB = create("burst_bomb", BurstBombEntity::new, MobCategory.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<SuctionBombEntity>> SUCTION_BOMB = create("suction_bomb", SuctionBombEntity::new, MobCategory.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<SplatBombEntity>> SPLAT_BOMB = create("splat_bomb", SplatBombEntity::new, MobCategory.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<CurlingBombEntity>> CURLING_BOMB = create("curling_bomb", CurlingBombEntity::new, MobCategory.MISC, 0.5f, 0.5f);

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.EntityFactory<T> supplier, MobCategory classification, float width, float height) {
        return REGISTRY.register(name, () -> EntityType.Builder.of(supplier, classification).sized(width, height).build(new ResourceLocation(MODID, name).toString()));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.EntityFactory<T> supplier, MobCategory classification) {
        return create(name, supplier, classification, 1, 1);
    }


    public static void bindRenderers() {
        EntityRenderers.register(INK_PROJECTILE.get(), InkProjectileRenderer::new);
        EntityRenderers.register(INK_SQUID.get(), InkSquidRenderer::new);
        EntityRenderers.register(SQUID_BUMPER.get(), SquidBumperRenderer::new);

        EntityRenderers.register(SPLAT_BOMB.get(), SplatBombRenderer::new);
        EntityRenderers.register(BURST_BOMB.get(), BurstBombRenderer::new);
        EntityRenderers.register(SUCTION_BOMB.get(), SuctionBombRenderer::new);
        EntityRenderers.register(CURLING_BOMB.get(), CurlingBombRenderer::new);

		EntityRenderers.register(SPAWN_SHIELD.get(), SpawnShieldRenderer::new);
	}

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void defineModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(InkSquidModel.LAYER_LOCATION, InkSquidModel::createBodyLayer);
        event.registerLayerDefinition(SquidBumperModel.LAYER_LOCATION, SquidBumperModel::createBodyLayer);

        event.registerLayerDefinition(SplatBombModel.LAYER_LOCATION, SplatBombModel::createBodyLayer);
        event.registerLayerDefinition(BurstBombModel.LAYER_LOCATION, BurstBombModel::createBodyLayer);
        event.registerLayerDefinition(SuctionBombModel.LAYER_LOCATION, SuctionBombModel::createBodyLayer);
        event.registerLayerDefinition(CurlingBombModel.LAYER_LOCATION, CurlingBombModel::createBodyLayer);

        event.registerLayerDefinition(InkProjectileModel.LAYER_LOCATION, InkProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ShooterInkProjectileModel.LAYER_LOCATION, ShooterInkProjectileModel::createBodyLayer);
        event.registerLayerDefinition(BlasterInkProjectileModel.LAYER_LOCATION, BlasterInkProjectileModel::createBodyLayer);
        event.registerLayerDefinition(RollerInkProjectileModel.LAYER_LOCATION, RollerInkProjectileModel::createBodyLayer);

        event.registerLayerDefinition(InkTankModel.LAYER_LOCATION, InkTankModel::createBodyLayer);
        event.registerLayerDefinition(ClassicInkTankModel.LAYER_LOCATION, ClassicInkTankModel::createBodyLayer);
        event.registerLayerDefinition(InkTankJrModel.LAYER_LOCATION, InkTankJrModel::createBodyLayer);
        event.registerLayerDefinition(ArmoredInkTankModel.LAYER_LOCATION, ArmoredInkTankModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void setEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(SplatcraftEntities.INK_SQUID.get(), InkSquidEntity.setCustomAttributes().build());
        event.put(SplatcraftEntities.SQUID_BUMPER.get(), SquidBumperEntity.setCustomAttributes().build());

        InkProjectileEntity.registerDataAccessors();
    }

    public static AttributeSupplier.Builder injectPlayerAttributes(AttributeSupplier.Builder builder) {
        builder.add(SplatcraftItems.INK_SWIM_SPEED, 0.075);
        return builder;
    }

    @OnlyIn(Dist.CLIENT)
    private static <T extends LivingEntity, M extends EntityModel<T>> void attachInkOverlay(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new InkOverlayLayer<>(renderer));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void addRenderLayers(EntityRenderersEvent.AddLayers event)
    {
        event.getSkins().forEach(renderer -> {
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> skin = event.getSkin(renderer);

            skin.addLayer(new InkAccessoryLayer(skin, new HumanoidModel<AbstractClientPlayer>(event.getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
            skin.addLayer(new PlayerInkColoredSkinLayer(skin, new PlayerModel<>(event.getEntityModels().bakeLayer(renderer.equals("slim") ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), renderer.equals("slim"))));
            attachInkOverlay(Objects.requireNonNull(skin));

        });
        ((AddLayersAccessor) event).getRenderers()
                .values().stream()
                .filter(LivingEntityRenderer.class::isInstance)
                .map(LivingEntityRenderer.class::cast)
                .forEach(SplatcraftEntities::attachInkOverlay);
    }
}
