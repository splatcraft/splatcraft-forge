package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.StageBarrierTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class StageBarrierBlock extends Block {
    public final boolean damagesPlayer;

    public static final VoxelShape COLLISION = makeCuboidShape(0.0, 0.01, 0.0, 16, 15.99, 16);

    public StageBarrierBlock(String name, boolean damagesPlayer) {
        super(Properties.create(Material.BARRIER, MaterialColor.AIR).hardnessAndResistance(-1.0F, 3600000.8F).noDrops().notSolid());
        setRegistryName(name);
        this.damagesPlayer = damagesPlayer;

    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addHitEffects(BlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (Minecraft.getInstance().player.isCreative() || !(worldIn.getTileEntity(pos) instanceof StageBarrierTileEntity))
            return VoxelShapes.fullCube();

        StageBarrierTileEntity te = (StageBarrierTileEntity) worldIn.getTileEntity(pos);

        return te.getActiveTime() > 5 ? super.getShape(state, worldIn, pos, context) : VoxelShapes.empty();
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return COLLISION;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return SplatcraftTileEntitites.stageBarrierTileEntity.create();
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return super.getRenderShape(state, worldIn, pos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

}
