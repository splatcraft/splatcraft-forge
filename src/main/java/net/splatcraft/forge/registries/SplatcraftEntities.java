package net.splatcraft.forge.registries;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.renderer.InkProjectileRenderer;
import net.splatcraft.forge.client.renderer.InkSquidRenderer;
import net.splatcraft.forge.client.renderer.SquidBumperRenderer;
import net.splatcraft.forge.client.renderer.subs.BurstBombRenderer;
import net.splatcraft.forge.client.renderer.subs.SplatBombRenderer;
import net.splatcraft.forge.client.renderer.subs.SuctionBombRenderer;
import net.splatcraft.forge.entities.InkProjectileEntity;
import net.splatcraft.forge.entities.InkSquidEntity;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.entities.subs.BurstBombEntity;
import net.splatcraft.forge.entities.subs.SplatBombEntity;
import net.splatcraft.forge.entities.subs.SuctionBombEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftEntities
{

    public static final EntityType<InkProjectileEntity> INK_PROJECTILE = create("ink_projectile", InkProjectileEntity::new, EntityClassification.MISC);
    public static final EntityType<InkSquidEntity> INK_SQUID = create("ink_squid", InkSquidEntity::new, EntityClassification.AMBIENT, 0.6f, 0.5f);
    public static final EntityType<SquidBumperEntity> SQUID_BUMPER = create("squid_bumper", SquidBumperEntity::new, EntityClassification.MISC, 0.6f, 1.8f);

    //Sub Weapons
    public static final EntityType<BurstBombEntity> BURST_BOMB = create("burst_bomb", BurstBombEntity::new, EntityClassification.MISC, 0.5f, 0.5f);
    public static final EntityType<SuctionBombEntity> SUCTION_BOMB = create("suction_bomb", SuctionBombEntity::new, EntityClassification.MISC, 0.5f, 0.5f);
    public static final EntityType<SplatBombEntity> SPLAT_BOMB = create("splat_bomb", SplatBombEntity::new, EntityClassification.MISC, 0.5f, 0.5f);


    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
    {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();

        registry.register(INK_PROJECTILE);
        registry.register(INK_SQUID);
        registry.register(SQUID_BUMPER);

        registry.register(BURST_BOMB);
        registry.register(SUCTION_BOMB);
        registry.register(SPLAT_BOMB);
    }

    private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification, float width, float height)
    {
        EntityType<T> type = EntityType.Builder.of(supplier, classification).sized(width, height).build(new ResourceLocation(Splatcraft.MODID, name).toString());

        type.setRegistryName(name);
        return type;
    }

    private static <T extends LivingEntity> EntityType<T> createLiving(String name, EntityType.IFactory<T> supplier, EntityClassification classification, float width, float height, Consumer<AttributeModifierMap> map)
    {
        EntityType<T> type = EntityType.Builder.of(supplier, classification).sized(width, height).build(new ResourceLocation(Splatcraft.MODID, name).toString());
        type.setRegistryName(name);
        return type;
    }

    private static <T extends Entity> EntityType<T> create(String name, EntityType.IFactory<T> supplier, EntityClassification classification)
    {
        return create(name, supplier, classification, 1, 1);
    }

    public static void bindRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(INK_PROJECTILE, InkProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(INK_SQUID, InkSquidRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SQUID_BUMPER, SquidBumperRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(BURST_BOMB, BurstBombRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SUCTION_BOMB, SuctionBombRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SPLAT_BOMB, SplatBombRenderer::new);
    }

    //@SubscribeEvent
    public static void setEntityAttributes(/*EntityAttributeCreationEvent event*/)
    {
        GlobalEntityTypeAttributes.put(SplatcraftEntities.INK_SQUID, InkSquidEntity.setCustomAttributes().build());
        GlobalEntityTypeAttributes.put(SplatcraftEntities.SQUID_BUMPER, SquidBumperEntity.setCustomAttributes().build());

        GlobalEntityTypeAttributes.put(EntityType.PLAYER, getAttributeMutableMap(EntityType.PLAYER).add(SplatcraftItems.INK_SWIM_SPEED, 0.075).build());
    }

    protected static AttributeModifierMap.MutableAttribute getAttributeMutableMap(EntityType<? extends LivingEntity> entityType)
    {
        AttributeModifierMap.MutableAttribute result = new AttributeModifierMap.MutableAttribute();

        Object obj = ObfuscationReflectionHelper.getPrivateValue(AttributeModifierMap.class, GlobalEntityTypeAttributes.getSupplier(entityType), "field_233802_a_");

        if (obj instanceof Map)
        {
            Map<Attribute, ModifiableAttributeInstance> map = (Map<Attribute, ModifiableAttributeInstance>) obj;
            for (Map.Entry<Attribute, ModifiableAttributeInstance> entry : map.entrySet())
            {
                result.add(entry.getKey(), entry.getValue().getValue());
            }
        }

        return result;
    }


}
