package com.cibernet.splatcraft.mixin;

import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractBlock.class, FourWayBlock.class})
public class SquidPassthroughMixin
{

    @Inject(at = @At("TAIL"), method = "getCollisionShape", cancellable = true, remap = false)
    private void getCollisionShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> callback)
    {
        try
        {
            if(state.is(SplatcraftTags.Blocks.SQUID_PASSTHROUGH) && context.getEntity() instanceof LivingEntity && PlayerInfoCapability.isSquid((LivingEntity) context.getEntity()))
                callback.setReturnValue(VoxelShapes.empty());
        }
        catch (IllegalStateException ignored) {}
    }
}
