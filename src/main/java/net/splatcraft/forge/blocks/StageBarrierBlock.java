package net.splatcraft.forge.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.StageBarrierTileEntity;
import org.jetbrains.annotations.Nullable;

public class StageBarrierBlock extends Block implements EntityBlock
{
    public static final VoxelShape COLLISION = box(0.0, 0.01, 0.0, 16, 15.99, 16);
    public final boolean damagesPlayer;

    public StageBarrierBlock(boolean damagesPlayer)
    {
        super(Properties.of(Material.BARRIER, MaterialColor.NONE).strength(-1.0F, 3600000.8F).noDrops().noOcclusion());
        this.damagesPlayer = damagesPlayer;

    }

    
    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel levelserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        return true;
    }

    /* ???
    @Override
    public boolean addHitEffects(BlockState state, Level levelObj, HitResult target, ParticleManager manager)
    {
        return true;
    }
    */

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        if (Minecraft.getInstance().player.isCreative() || !(levelIn.getBlockEntity(pos) instanceof StageBarrierTileEntity))
        {
            return Shapes.block();
        }

        StageBarrierTileEntity te = (StageBarrierTileEntity) levelIn.getBlockEntity(pos);

        return te.getActiveTime() > 5 ? super.getShape(state, levelIn, pos, context) : Shapes.empty();
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        return COLLISION;
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
    {
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState p_220080_1_, BlockGetter p_220080_2_, BlockPos p_220080_3_) {
        return 1.0F;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SplatcraftTileEntities.stageBarrierTileEntity.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) 
    {
        return (level, pos, state, blockEntity) -> ((StageBarrierTileEntity)blockEntity).tick();
    }
}
