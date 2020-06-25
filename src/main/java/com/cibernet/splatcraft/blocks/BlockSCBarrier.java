package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.tileentities.TileEntityStageBarrier;
import com.cibernet.splatcraft.utils.ClientUtils;
import com.cibernet.splatcraft.utils.SplatCraftDamageSource;
import com.cibernet.splatcraft.utils.TabSplatCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockSCBarrier extends Block
{
	boolean damagesPlayer;
	boolean fancy;
	final ResourceLocation FANCY_TEXTURE;
	final ResourceLocation MODEL_TEXTURE;
	
	private static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.05, 0.05, 0.05, 0.95, 0.95, 0.95);
	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(-0.05, -0.05, -0.05, 1.05, 1.05, 1.05);
	private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0,0,0,0,0,0);
	private static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0,0,0,1,1,1);
	
	
	public BlockSCBarrier(String unlocName, String registryName, boolean damagesPlayer)
	{
		super(Material.BARRIER);
		
		setUnlocalizedName(unlocName);
		setRegistryName(registryName);
		setCreativeTab(TabSplatCraft.main);
		this.setBlockUnbreakable();
		this.setResistance(6000001.0F);
		this.disableStats();
		this.translucent = true;
		
		this.damagesPlayer = damagesPlayer;
		MODEL_TEXTURE = new ResourceLocation(SplatCraft.MODID, "textures/models/" + registryName + ".png");
		FANCY_TEXTURE = new ResourceLocation(SplatCraft.MODID, "textures/models/" + registryName + "_fancy.png");
	}
	
	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager)
	{
		return true;
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
	{
		return true;
	}
	
	@Override
	public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		return true;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityStageBarrier();
	}
	
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return damagesPlayer ? COLLISION_BOX : DEFAULT_AABB;
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		if(ClientUtils.getClientPlayer().isCreative())
			return super.getSelectedBoundingBox(state, worldIn, pos);
		return EMPTY_AABB;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
		
		if(entityIn instanceof EntityPlayer && damagesPlayer)
			entityIn.attackEntityFrom(SplatCraftDamageSource.VOID_DAMAGE, Float.MAX_VALUE);
		
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	public ResourceLocation getModelTexture()
	{
		return ClientUtils.getFancyGraphics() ? FANCY_TEXTURE : MODEL_TEXTURE;
	}
	
	@SideOnly(Side.CLIENT)
	public void setGraphicsLevel(boolean fancy)
	{
		this.fancy = fancy;
	}
	
	
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state)
{
	return 1.0F;
}

}
