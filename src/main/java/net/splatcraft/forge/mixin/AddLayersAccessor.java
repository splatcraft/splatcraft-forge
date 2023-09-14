package net.splatcraft.forge.mixin;

import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityRenderersEvent.AddLayers.class, remap = false)
public interface AddLayersAccessor {
    @Accessor
    Map<EntityType<?>, EntityRenderer<?>> getRenderers();
}
