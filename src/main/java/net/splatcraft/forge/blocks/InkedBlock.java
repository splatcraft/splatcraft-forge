package net.splatcraft.forge.blocks;

import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.BlockInkedResult;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

public class InkedBlock extends Block implements EntityBlock, IColoredBlock
{
    public static final int GLOWING_LIGHT_LEVEL = 6;
    public InkedBlock()
    {
        this(defaultProperties());
    }

    public InkedBlock(Properties properties)
    {
        super(properties);
        SplatcraftBlocks.inkColoredBlocks.add(this);
    }


    private static Properties defaultProperties()
    {
        return Properties.of(Material.CLAY, MaterialColor.TERRACOTTA_BLACK).randomTicks().requiresCorrectToolForDrops().sound(SoundType.SLIME_BLOCK).noOcclusion().dynamicShape();
    }

    public static boolean isTouchingLiquid(BlockGetter reader, BlockPos pos)
    {
        boolean flag = false;
        BlockPos.MutableBlockPos blockpos$mutable = pos.mutable();

        BlockState currentState = reader.getBlockState(pos);

        if (currentState.hasProperty(BlockStateProperties.WATERLOGGED) && currentState.getValue(BlockStateProperties.WATERLOGGED))
        {
            return true;
        }

        for (Direction direction : Direction.values())
        {
            blockpos$mutable.setWithOffset(pos, direction);
            BlockState blockstate = reader.getBlockState(blockpos$mutable);

            if (causesClear(reader, pos, blockstate, direction))
            {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public static boolean causesClear(BlockGetter level, BlockPos pos, BlockState state)
    {
        return causesClear(level, pos, state, Direction.UP);
    }

    public static boolean causesClear(BlockGetter level, BlockPos pos, BlockState state, Direction dir)
    {
        if(state.is(SplatcraftTags.Blocks.INK_CLEARING_BLOCKS))
            return true;

        if(dir != Direction.DOWN && state.getFluidState().is(FluidTags.WATER))
            return !state.isFaceSturdy(level, pos, dir.getOpposite());

        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return SplatcraftTileEntities.inkedTileEntity.get().create(pos, state);
    }

    public static InkedBlock glowing()
    {
        return new InkedBlock(defaultProperties().lightLevel(state -> GLOWING_LIGHT_LEVEL));
    }



    private static BlockState clearInk(LevelAccessor level, BlockPos pos)
    {
        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
        int color = te.getPermanentColor();
        if(te.hasPermanentColor())
        {
            if(te.getColor() == color)
                return level.getBlockState(pos);

            if(!InkBlockUtils.getInkType(level.getBlockState(pos)).equals(te.getPermanentInkType()) && (level instanceof Level))
            {
                level.setBlock(pos, InkBlockUtils.getInkState(te.getPermanentInkType()), 2);
                InkedBlockTileEntity newTe = (InkedBlockTileEntity) level.getBlockEntity(pos);
                newTe.setSavedState(te.getSavedState());
                newTe.setSavedColor(te.getSavedColor());
                newTe.setPermanentInkType(te.getPermanentInkType());
                newTe.setPermanentColor(te.getPermanentColor());
                ((Level)level).setBlockEntity(newTe);
                te = newTe;
            }
            te.setColor(color);

        }
        else if (te.hasSavedState())
        {
            level.setBlock(pos, te.getSavedState(), 3);

            if (te.hasSavedColor() && te.getSavedState().getBlock() instanceof IColoredBlock)
            {
                if(te.getSavedState().getBlock() instanceof EntityBlock)
                ((Level) level).setBlockEntity(Objects.requireNonNull(((EntityBlock) te.getSavedState().getBlock()).newBlockEntity(pos, te.getSavedState())));
                ((IColoredBlock) te.getSavedState().getBlock()).setColor((Level) level, pos, te.getSavedColor());
            }

            return te.getSavedState();
        }

        return level.getBlockState(pos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        if (level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getSavedState();
            return savedState.getBlock().getCloneItemStack(savedState, target, level, pos, player);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player)
    {
        if(level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getSavedState();
            return savedState.getBlock().canHarvestBlock(savedState, level, pos, player);
        }
        return super.canHarvestBlock(state, level, pos, player);
    }



    @Override
    public void playerDestroy(Level level, Player playerEntity, BlockPos pos, BlockState state, @Nullable BlockEntity tileEntity, ItemStack stack)
    {
        if(tileEntity instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) tileEntity).getSavedState();
            savedState.getBlock().playerDestroy(level, playerEntity, pos, savedState, null, stack);
        }
        super.playerDestroy(level, playerEntity, pos, state, tileEntity, stack);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        if(!(levelIn.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return Shapes.empty();
        BlockState savedState = ((InkedBlockTileEntity) levelIn.getBlockEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
            return super.getShape(state, levelIn, pos, context);

        VoxelShape result = savedState.getBlock().getShape(savedState, levelIn, pos, context);
        if(!result.isEmpty())
            return result;
        return super.getShape(state, levelIn, pos, context);

    }

    @Override
    public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity)
    {
        if(!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity))
        return super.collisionExtendsVertically(state, level, pos, collidingEntity);
        BlockState savedState = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
            return super.collisionExtendsVertically(state, level, pos, collidingEntity);
        return savedState.getBlock().collisionExtendsVertically(savedState, level, pos, collidingEntity);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        if(!(levelIn.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return super.getCollisionShape(state, levelIn, pos, context);
        BlockState savedState = ((InkedBlockTileEntity) levelIn.getBlockEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
        {
            return super.getCollisionShape(state, levelIn, pos, context);
        }
        VoxelShape result = savedState.getBlock().getCollisionShape(savedState, levelIn, pos, context);
        if(!result.isEmpty())
            return result;
        return super.getCollisionShape(state, levelIn, pos, context);
    }


    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context)
    {
        if(!(levelIn.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return super.getVisualShape(state, levelIn, pos, context);
        BlockState savedState = ((InkedBlockTileEntity) levelIn.getBlockEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
        {
            return super.getVisualShape(state, levelIn, pos, context);
        }
        VoxelShape result = savedState.getBlock().getVisualShape(savedState, levelIn, pos, context);
        if(!result.isEmpty())
            return result;
        return super.getVisualShape(state, levelIn, pos, context);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.BLOCK;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter levelIn, BlockPos pos)
    {
        if (!(levelIn.getBlockEntity(pos) instanceof InkedBlockTileEntity te))
            return super.getDestroyProgress(state, player, levelIn, pos);

        if (te.getSavedState().getBlock() instanceof InkedBlock)
            return super.getDestroyProgress(state, player, levelIn, pos);

        return te.getSavedState().getBlock().getDestroyProgress(te.getSavedState(), player, levelIn, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        if (!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity te))
            return super.getExplosionResistance(state, level, pos, explosion);

        if (te.getSavedState().getBlock() instanceof InkedBlock)
            return super.getExplosionResistance(state, level, pos, explosion);

        return te.getSavedState().getBlock().getExplosionResistance(te.getSavedState(), level, pos, explosion);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel levelserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        ColorUtils.addInkSplashParticle(levelserver, getColor(levelserver, pos), entity.getX(), entity.getY(levelserver.random.nextFloat() * 0.3f), entity.getZ(), (float) Math.sqrt(numberOfParticles) * 0.3f);
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        ColorUtils.addInkSplashParticle(level, getColor(level, pos), entity.getX(), entity.getY(level.getRandom().nextFloat() * 0.3f), entity.getZ(), 0.6f);
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (SplatcraftGameRules.getLocalizedRule(level, pos, SplatcraftGameRules.INK_DECAY) && level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            boolean decay = level.isRainingAt(pos);

            if(!decay)
            {
                int i = 0;
                for(Direction dir : Direction.values())
                    if(level.getBlockEntity(pos.relative(dir)) instanceof InkedBlockTileEntity)
                        i++;
                decay = i <= 0 || rand.nextInt(i*2) == 0;
            }

            if(decay)
                level.sendBlockUpdated(pos, level.getBlockState(pos), clearInk(level, pos), 3);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (isTouchingLiquid(levelIn, currentPos))
        {
            if (levelIn.getBlockEntity(currentPos) instanceof InkedBlockTileEntity)
                return clearInk(levelIn, currentPos);
        }



        if (levelIn.getBlockEntity(currentPos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) levelIn.getBlockEntity(currentPos)).getSavedState();

            if (savedState != null && !savedState.getBlock().equals(this))
            {
                if(facingState != null && levelIn.getBlockEntity(facingPos) instanceof InkedBlockTileEntity)
                    facingState = ((InkedBlockTileEntity) levelIn.getBlockEntity(facingPos)).getSavedState();

                ((InkedBlockTileEntity) levelIn.getBlockEntity(currentPos)).setSavedState(savedState.getBlock().updateShape(savedState, facing, facingState, levelIn, currentPos, facingPos));
            }
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }


    @Override
    public boolean canClimb()
    {
        return true;
    }

    @Override
    public boolean canSwim()
    {
        return true;
    }

    @Override
    public boolean canDamage()
    {
        return true;
    }

    @Override
    public int getColor(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {

        return false;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos)
    {
        BlockState oldState = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            int color = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getColor();

            if(clearInk(level, pos).equals(oldState) && (!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity) || ((InkedBlockTileEntity) level.getBlockEntity(pos)).getColor() == color))
                return false;
            level.sendBlockUpdated(pos, oldState, level.getBlockState(pos), 3);
            return true;
        }
        return false;
    }

    @Override
    public BlockInkedResult inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity te))
            return BlockInkedResult.FAIL;

        BlockState oldState = level.getBlockState(pos);
        BlockState state = level.getBlockState(pos);
        boolean changeColor = te.getColor() != color;

        if (changeColor)
            te.setColor(color);
        BlockState inkState = InkBlockUtils.getInkState(inkType);

        if (inkState.getBlock() != state.getBlock())
        {
            state = inkState;
            level.setBlock(pos, state, 2);
            InkedBlockTileEntity newTe = (InkedBlockTileEntity) level.getBlockEntity(pos);
            newTe.setSavedState(te.getSavedState());
            newTe.setSavedColor(te.getSavedColor());
            newTe.setColor(te.getColor());
            newTe.setPermanentInkType(te.getPermanentInkType());
            newTe.setPermanentColor(te.getPermanentColor());

            //level.setBlockEntity(pos, newTe);
        }
        level.sendBlockUpdated(pos, oldState, state, 2);

        return changeColor ? BlockInkedResult.SUCCESS : BlockInkedResult.ALREADY_INKED;
    }
}
