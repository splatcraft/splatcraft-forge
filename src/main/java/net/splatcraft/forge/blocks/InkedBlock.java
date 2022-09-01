package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class InkedBlock extends Block implements IColoredBlock
{
    public static final Properties DEFAULT_PROPERTIES = Properties.of(Material.CLAY, MaterialColor.TERRACOTTA_BLACK).randomTicks().harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.SLIME_BLOCK).noOcclusion().dynamicShape();
    public static final int GLOWING_LIGHT_LEVEL = 6;


    public InkedBlock(String name)
    {
        this(name, DEFAULT_PROPERTIES);
    }

    public InkedBlock(String name, Properties properties)
    {
        super(properties);
        SplatcraftBlocks.inkColoredBlocks.add(this);
        setRegistryName(name);
    }

    public static InkedBlock glowing(String name)
    {
        return new InkedBlock(name, DEFAULT_PROPERTIES.lightLevel(state -> GLOWING_LIGHT_LEVEL));
    }

    public static boolean isTouchingLiquid(IBlockReader reader, BlockPos pos)
    {
        boolean flag = false;
        BlockPos.Mutable blockpos$mutable = pos.mutable();

        BlockState currentState = reader.getBlockState(pos);

        if (currentState.hasProperty(BlockStateProperties.WATERLOGGED) && currentState.getValue(BlockStateProperties.WATERLOGGED))
        {
            return true;
        }

        for (Direction direction : Direction.values())
        {
            blockpos$mutable.setWithOffset(pos, direction);
            BlockState blockstate = reader.getBlockState(blockpos$mutable);

            if (causesClear(blockstate, direction))
            {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public static boolean causesClear(BlockState state)
    {
        return causesClear(state, Direction.UP);
    }

    public static boolean causesClear(BlockState state, Direction dir)
    {
        return state.is(SplatcraftTags.Blocks.INK_CLEARING_BLOCKS) || (dir != Direction.DOWN && state.getFluidState().is(FluidTags.WATER));
    }

    private static BlockState clearInk(IWorld level, BlockPos pos)
    {
        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
        int color = te.getPermanentColor();
        if(te.hasPermanentColor())
        {
            if(te.getColor() == color)
                return level.getBlockState(pos);

            if(!InkBlockUtils.getInkType(level.getBlockState(pos)).equals(te.getPermanentInkType()) && (level instanceof World))
            {
                level.setBlock(pos, InkBlockUtils.getInkState(te.getPermanentInkType(), (World) level, pos), 2);
                InkedBlockTileEntity newTe = (InkedBlockTileEntity) level.getBlockEntity(pos);
                newTe.setSavedState(te.getSavedState());
                newTe.setSavedColor(te.getSavedColor());
                newTe.setPermanentInkType(te.getPermanentInkType());
                newTe.setPermanentColor(te.getPermanentColor());
                ((World)level).setBlockEntity(pos, newTe);
                te = newTe;
            }
            te.setColor(color);

        }
        else if (te.hasSavedState())
        {
            level.setBlock(pos, te.getSavedState(), 3);

            if (te.hasSavedColor() && te.getSavedState().getBlock() instanceof IColoredBlock)
            {
                ((World) level).setBlockEntity(pos, te.getSavedState().getBlock().createTileEntity(te.getSavedState(), level));
                if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
                {
                    InkColorTileEntity newte = (InkColorTileEntity) level.getBlockEntity(pos);
                    newte.setColor(te.getSavedColor());
                }
            }

            return te.getSavedState();
        }

        return level.getBlockState(pos);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        if (level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getSavedState();
            return savedState.getBlock().getPickBlock(savedState, target, level, pos, player);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player)
    {
        if(level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getSavedState();
            return savedState.getBlock().canHarvestBlock(savedState, level, pos, player);
        }
        return super.canHarvestBlock(state, level, pos, player);
    }



    @Override
    public void playerDestroy(World level, PlayerEntity playerEntity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack)
    {
        if(tileEntity instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) tileEntity).getSavedState();
            savedState.getBlock().playerDestroy(level, playerEntity, pos, savedState, null, stack);
        }
        super.playerDestroy(level, playerEntity, pos, state, tileEntity, stack);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
    {
        if(!(levelIn.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return VoxelShapes.empty();
        BlockState savedState = ((InkedBlockTileEntity) levelIn.getBlockEntity(pos)).getSavedState();
        
        if(savedState == null || savedState.getBlock().equals(this))
            return super.getShape(state, levelIn, pos, context);

        VoxelShape result = savedState.getBlock().getShape(savedState, levelIn, pos, context);
        if(!result.isEmpty())
            return result;
        return super.getShape(state, levelIn, pos, context);

    }

    @Override
    public boolean collisionExtendsVertically(BlockState state, IBlockReader level, BlockPos pos, Entity collidingEntity)
    {
        if(!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity))
        return super.collisionExtendsVertically(state, level, pos, collidingEntity);
        BlockState savedState = ((InkedBlockTileEntity) level.getBlockEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
            return super.collisionExtendsVertically(state, level, pos, collidingEntity);
        return savedState.getBlock().collisionExtendsVertically(savedState, level, pos, collidingEntity);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context)
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
    public PushReaction getPistonPushReaction(BlockState state)
    {
        return PushReaction.BLOCK;
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader levelIn, BlockPos pos)
    {
        if (!(levelIn.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return super.getDestroyProgress(state, player, levelIn, pos);
        InkedBlockTileEntity te = (InkedBlockTileEntity) levelIn.getBlockEntity(pos);

        if (te.getSavedState().getBlock() instanceof InkedBlock)
            return super.getDestroyProgress(state, player, levelIn, pos);

        return te.getSavedState().getBlock().getDestroyProgress(te.getSavedState(), player, levelIn, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader level, BlockPos pos, Explosion explosion)
    {
        if (!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return super.getExplosionResistance(state, level, pos, explosion);
        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);

        if (te.getSavedState().getBlock() instanceof InkedBlock)
            return super.getExplosionResistance(state, level, pos, explosion);

        return te.getSavedState().getBlock().getExplosionResistance(te.getSavedState(), level, pos, explosion);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld levelserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        ColorUtils.addInkSplashParticle(levelserver, getColor(levelserver, pos), entity.getX(), entity.getY(levelserver.random.nextFloat() * 0.3f), entity.getZ(), (float) Math.sqrt(numberOfParticles) * 0.3f);
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World level, BlockPos pos, Entity entity)
    {
        ColorUtils.addInkSplashParticle(level, getColor(level, pos), entity.getX(), entity.getY(level.getRandom().nextFloat() * 0.3f), entity.getZ(), 0.6f);
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld level, BlockPos pos, Random rand)
    {
        if (level.getGameRules().getBoolean(SplatcraftGameRules.INK_DECAY) && level.getBlockEntity(pos) instanceof InkedBlockTileEntity)
            level.sendBlockUpdated(pos, level.getBlockState(pos), clearInk(level, pos), 3);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld levelIn, BlockPos currentPos, BlockPos facingPos)
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

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level)
    {
        return SplatcraftTileEntitites.inkedTileEntity.create();
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
    public int getColor(World level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) level.getBlockEntity(pos)).getColor();
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {

        return false;
    }

    @Override
    public boolean remoteInkClear(World level, BlockPos pos)
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
    public boolean inkBlock(World level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (!(level.getBlockEntity(pos) instanceof InkedBlockTileEntity))
            return false;

        InkedBlockTileEntity te = (InkedBlockTileEntity) level.getBlockEntity(pos);
        BlockState oldState = level.getBlockState(pos);
        BlockState state = level.getBlockState(pos);

        if (te.getColor() != color)
            te.setColor(color);
        BlockState inkState = InkBlockUtils.getInkState(inkType, level, pos);

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

        return !(te.getColor() == color && inkState.getBlock() == state.getBlock());
    }
}
