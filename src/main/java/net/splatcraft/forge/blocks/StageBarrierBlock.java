package net.splatcraft.forge.blocks;

import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.StageBarrierTileEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class StageBarrierBlock extends Block
{
    public static final VoxelShape COLLISION = box(0.0, 0.01, 0.0, 16, 15.99, 16);
    public final boolean damagesPlayer;

    public StageBarrierBlock(String name, boolean damagesPlayer)
    {
        super(Properties.of(Material.BARRIER, MaterialColor.NONE).strength(-1.0F, 3600000.8F).noDrops().noOcclusion());
        setRegistryName(name);
        this.damagesPlayer = damagesPlayer;

    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld levelserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        return true;
    }

    @Override
    public boolean addHitEffects(BlockState state, World levelObj, RayTraceResult target, ParticleManager manager)
    {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World level, BlockPos pos, Entity entity)
    {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        if (Minecraft.getInstance().player.isCreative() || !(levelIn.getBlockEntity(pos) instanceof StageBarrierTileEntity))
        {
            return VoxelShapes.block();
        }

        StageBarrierTileEntity te = (StageBarrierTileEntity) levelIn.getBlockEntity(pos);

        return te.getActiveTime() > 5 ? super.getShape(state, levelIn, pos, context) : VoxelShapes.empty();
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        return COLLISION;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level)
    {
        return SplatcraftTileEntitites.stageBarrierTileEntity.create();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
        return 1.0F;
    }

}
