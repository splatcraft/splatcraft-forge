package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkedBlockTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import com.cibernet.splatcraft.util.InkBlockUtils;
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

import javax.annotation.Nullable;
import java.util.Random;

public class InkedBlock extends Block implements IColoredBlock
{
    public static final Properties DEFAULT_PROPERTIES = Properties.create(Material.CLAY, MaterialColor.BLACK_TERRACOTTA).tickRandomly().harvestTool(ToolType.PICKAXE).setRequiresTool().sound(SoundType.SLIME).notSolid().variableOpacity();
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
        return new InkedBlock(name, DEFAULT_PROPERTIES.setLightLevel(state -> GLOWING_LIGHT_LEVEL));
    }

    public static boolean isTouchingLiquid(IBlockReader reader, BlockPos pos)
    {
        boolean flag = false;
        BlockPos.Mutable blockpos$mutable = pos.toMutable();

        BlockState currentState = reader.getBlockState(pos);

        if (currentState.hasProperty(BlockStateProperties.WATERLOGGED) && currentState.get(BlockStateProperties.WATERLOGGED))
        {
            return true;
        }

        for (Direction direction : Direction.values())
        {
            blockpos$mutable.setAndMove(pos, direction);
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
        return state.isIn(SplatcraftTags.Blocks.INK_CLEARING_BLOCKS) || (dir != Direction.DOWN && state.getFluidState().isTagged(FluidTags.WATER));
    }

    private static BlockState clearInk(IWorld world, BlockPos pos)
    {
        InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
        int color = te.getPermanentColor();
        if(te.hasPermanentColor())
        {
            if(te.getColor() == color)
                return world.getBlockState(pos);

            if(!InkBlockUtils.getInkType(world.getBlockState(pos)).equals(te.getPermanentInkType()) && (world instanceof World))
            {
                world.setBlockState(pos, InkBlockUtils.getInkState(te.getPermanentInkType(), (World) world, pos), 2);
                InkedBlockTileEntity newTe = (InkedBlockTileEntity) world.getTileEntity(pos);
                newTe.setSavedState(te.getSavedState());
                newTe.setSavedColor(te.getSavedColor());
                newTe.setPermanentInkType(te.getPermanentInkType());
                newTe.setPermanentColor(te.getPermanentColor());
                ((World)world).setTileEntity(pos, newTe);
                te = newTe;
            }
            te.setColor(color);

        }
        else if (te.hasSavedState())
        {
            world.setBlockState(pos, te.getSavedState(), 3);

            if (te.hasSavedColor() && te.getSavedState().getBlock() instanceof IColoredBlock)
            {
                ((World) world).setTileEntity(pos, te.getSavedState().getBlock().createTileEntity(te.getSavedState(), world));
                if (world.getTileEntity(pos) instanceof InkColorTileEntity)
                {
                    InkColorTileEntity newte = (InkColorTileEntity) world.getTileEntity(pos);
                    newte.setColor(te.getSavedColor());
                }
            }

            return te.getSavedState();
        }

        return world.getBlockState(pos);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        if (world.getTileEntity(pos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) world.getTileEntity(pos)).getSavedState();
            return savedState.getBlock().getPickBlock(savedState, target, world, pos, player);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        if(world.getTileEntity(pos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) world.getTileEntity(pos)).getSavedState();
            return savedState.getBlock().canHarvestBlock(savedState, world, pos, player);
        }
        return super.canHarvestBlock(state, world, pos, player);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity playerEntity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack)
    {
        if(tileEntity instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) tileEntity).getSavedState();
            savedState.getBlock().harvestBlock(world, playerEntity, pos, savedState, null, stack);
        }
        super.harvestBlock(world, playerEntity, pos, state, tileEntity, stack);
    }



    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return VoxelShapes.empty();
        BlockState savedState = ((InkedBlockTileEntity) worldIn.getTileEntity(pos)).getSavedState();
        
        if(savedState == null || savedState.getBlock().equals(this))
            return super.getShape(state, worldIn, pos, context);

        VoxelShape result = savedState.getBlock().getShape(savedState, worldIn, pos, context);
        if(!result.isEmpty())
            return result;
        return super.getShape(state, worldIn, pos, context);

    }

    @Override
    public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity)
    {
        if(!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
        return super.collisionExtendsVertically(state, world, pos, collidingEntity);
        BlockState savedState = ((InkedBlockTileEntity) world.getTileEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
            return super.collisionExtendsVertically(state, world, pos, collidingEntity);
        return savedState.getBlock().collisionExtendsVertically(savedState, world, pos, collidingEntity);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if(!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return super.getCollisionShape(state, worldIn, pos, context);
        BlockState savedState = ((InkedBlockTileEntity) worldIn.getTileEntity(pos)).getSavedState();

        if(savedState == null || savedState.getBlock().equals(this))
        {
            return super.getCollisionShape(state, worldIn, pos, context);
        }
        VoxelShape result = savedState.getBlock().getCollisionShape(savedState, worldIn, pos, context);
        if(!result.isEmpty())
            return result;
        return super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.BLOCK;
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
    {
        if (!(worldIn.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
        InkedBlockTileEntity te = (InkedBlockTileEntity) worldIn.getTileEntity(pos);

        if (te.getSavedState().getBlock() instanceof InkedBlock)
            return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);


        return te.getSavedState().getBlock().getPlayerRelativeBlockHardness(te.getSavedState(), player, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {
        if (!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return super.getExplosionResistance(state, world, pos, explosion);
        InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);

        if (te.getSavedState().getBlock() instanceof InkedBlock)
            return super.getExplosionResistance(state, world, pos, explosion);

        return te.getSavedState().getBlock().getExplosionResistance(te.getSavedState(), world, pos, explosion);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        ColorUtils.addInkSplashParticle(worldserver, getColor(worldserver, pos), entity.getPosX(), entity.getPosYHeight(worldserver.rand.nextDouble() * 0.3), entity.getPosZ(), (float) Math.sqrt(numberOfParticles) * 0.3f);
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity)
    {
        ColorUtils.addInkSplashParticle(world, getColor(world, pos), entity.getPosX(), entity.getPosYHeight(world.rand.nextDouble() * 0.3), entity.getPosZ(), 0.6f);
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
    {
        if (world.getGameRules().getBoolean(SplatcraftGameRules.INK_DECAY) && world.getTileEntity(pos) instanceof InkedBlockTileEntity)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), clearInk(world, pos), 3);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (isTouchingLiquid(worldIn, currentPos))
        {
            if (worldIn.getTileEntity(currentPos) instanceof InkedBlockTileEntity)
                return clearInk(worldIn, currentPos);
        }

        if (worldIn.getTileEntity(currentPos) instanceof InkedBlockTileEntity)
        {
            BlockState savedState = ((InkedBlockTileEntity) worldIn.getTileEntity(currentPos)).getSavedState();

            if (savedState != null && !savedState.getBlock().equals(this))
            {
                if(facingState != null && worldIn.getTileEntity(facingPos) instanceof InkedBlockTileEntity)
                    facingState = ((InkedBlockTileEntity) worldIn.getTileEntity(facingPos)).getSavedState();

                ((InkedBlockTileEntity) worldIn.getTileEntity(currentPos)).setSavedState(savedState.getBlock().updatePostPlacement(savedState, facing, facingState, worldIn, currentPos, facingPos));
            }
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
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
    public int getColor(World world, BlockPos pos)
    {
        if (world.getTileEntity(pos) instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) world.getTileEntity(pos)).getColor();
        }
        return -1;
    }

    @Override
    public boolean remoteColorChange(World world, BlockPos pos, int newColor)
    {

        return false;
    }

    @Override
    public boolean remoteInkClear(World world, BlockPos pos)
    {
        BlockState oldState = world.getBlockState(pos);
        if (world.getTileEntity(pos) instanceof InkedBlockTileEntity)
        {
            if(clearInk(world, pos).equals(oldState))
                return false;
            world.notifyBlockUpdate(pos, oldState, world.getBlockState(pos), 3);
            return true;
        }
        return false;
    }

    @Override
    public boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (!(world.getTileEntity(pos) instanceof InkedBlockTileEntity))
            return false;

        InkedBlockTileEntity te = (InkedBlockTileEntity) world.getTileEntity(pos);
        BlockState oldState = world.getBlockState(pos);
        BlockState state = world.getBlockState(pos);

        if (te.getColor() != color)
            te.setColor(color);
        BlockState inkState = InkBlockUtils.getInkState(inkType, world, pos);

        if (inkState.getBlock() != state.getBlock())
        {
            state = inkState;
            world.setBlockState(pos, state, 2);
            InkedBlockTileEntity newTe = (InkedBlockTileEntity) world.getTileEntity(pos);
            newTe.setSavedState(te.getSavedState());
            newTe.setSavedColor(te.getSavedColor());
            newTe.setColor(te.getColor());
            newTe.setPermanentInkType(te.getPermanentInkType());
            newTe.setPermanentColor(te.getPermanentColor());

            world.setTileEntity(pos, newTe);
        } else
            world.notifyBlockUpdate(pos, oldState, state, 2);
        return !(te.getColor() == color && inkState.getBlock() == state.getBlock());
    }
}
