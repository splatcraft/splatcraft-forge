package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ChainBlockOverride extends ChainBlock implements IInkPassthrough
{
    @Deprecated
    public ChainBlockOverride()
    {
        super(AbstractBlock.Properties.create(Material.IRON, MaterialColor.AIR).setRequiresTool().hardnessAndResistance(5.0F, 6.0F).sound(SoundType.CHAIN).notSolid());
        setRegistryName("minecraft", "chain");
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        boolean isSquid = false;

        if (context.getEntity() instanceof LivingEntity)
        {
            isSquid = PlayerInfoCapability.isSquid((LivingEntity) context.getEntity());
        }

        return isSquid ? VoxelShapes.empty() : super.getCollisionShape(state, worldIn, pos, context);
    }
}
