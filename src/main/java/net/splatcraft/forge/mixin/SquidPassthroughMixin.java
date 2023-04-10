package net.splatcraft.forge.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockBehaviour.class})
public class SquidPassthroughMixin
{


    @Inject(at = @At("TAIL"), method = "getCollisionShape", cancellable = true)
    private void getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback)
    {
        try
        {
            if(state.is(SplatcraftTags.Blocks.SQUID_PASSTHROUGH) && context instanceof EntityCollisionContext eContext &&
                    eContext.getEntity() instanceof LivingEntity && PlayerInfoCapability.isSquid((LivingEntity) eContext.getEntity()))
                callback.setReturnValue(Shapes.empty());
        }
        catch (IllegalStateException ignored) {}
    }
}
