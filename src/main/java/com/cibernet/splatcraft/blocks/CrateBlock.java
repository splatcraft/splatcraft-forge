package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.registries.SplatcraftBlocks;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.tileentities.CrateTileEntity;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CrateBlock extends Block implements IColoredBlock
{
	public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 4);
	public static final ResourceLocation STORAGE_SUNKEN_CRATE = new ResourceLocation(Splatcraft.MODID, "storage/sunken_crate");
	
	public final boolean hasLoot;
	
	public CrateBlock(String name, boolean hasLoot)
	{
		super(Properties.create(Material.WOOD).harvestTool(ToolType.AXE).setRequiresTool().sound(SoundType.WOOD).hardnessAndResistance(2.0f));
		
		setRegistryName(name);
		this.hasLoot = hasLoot;
		
		SplatcraftBlocks.inkColoredBlocks.add(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");

		if (compoundnbt != null && !hasLoot && compoundnbt.contains("Items", 9))
		{
			NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
			int i = 0;
			int j = 0;
			
			for(ItemStack itemstack : nonnulllist) {
				if (!itemstack.isEmpty()) {
					++j;
					if (i <= 4) {
						++i;
						IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().deepCopy();
						iformattabletextcomponent.appendString(" x").appendString(String.valueOf(itemstack.getCount()));
						tooltip.add(iformattabletextcomponent);
					}
				}
			}
			
			if (j - i > 0) {
				tooltip.add((new TranslationTextComponent("container.shulkerBox.more", j - i)).mergeStyle(TextFormatting.ITALIC));
			}
		}
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(STATE);
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		if(worldIn.getTileEntity(currentPos) instanceof CrateTileEntity)
			return stateIn.with(STATE, ((CrateTileEntity) worldIn.getTileEntity(currentPos)).getState());
		
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		CrateTileEntity te = SplatcraftTileEntitites.crateTileEntity.get().create();
		te.setMaxHealth(hasLoot ? 25 : 20);
		te.resetHealth();
		te.setHasLoot(hasLoot);
		
		return te;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}
	
	public static List<ItemStack> generateLoot(World world, BlockPos pos, BlockState state, float luckValue)
	{
		if(world == null || world.isRemote)
			return Collections.emptyList();
		
		LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world);
		List<ItemStack> loot = world.getServer().getLootTableManager().getLootTableFromLocation(STORAGE_SUNKEN_CRATE).generate(contextBuilder.withLuck(luckValue)
				.withParameter(LootParameters.BLOCK_STATE, state).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withParameter(LootParameters.POSITION, pos).build(LootParameterSets.BLOCK));
		
		return loot;
	}
	
	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		player.addStat(Stats.BLOCK_MINED.get(this));
		player.addExhaustion(0.005F);
		
		
		
		if(worldIn.getGameRules().getBoolean(SplatcraftGameRules.DROP_CRATE_LOOT) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) <= 0 && worldIn.getTileEntity(pos) instanceof CrateTileEntity)
			((CrateTileEntity) worldIn.getTileEntity(pos)).dropInventory();
		else spawnDrops(state, worldIn, pos, te, player, stack);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		ItemStack tool = builder.get(LootParameters.TOOL);
		World world = builder.getWorld();
		
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
		
		if(te instanceof CrateTileEntity)
		{
			CrateTileEntity crate = (CrateTileEntity) te;
			
			boolean silkTouched = tool != null && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
			
			if(world.getGameRules().getBoolean(SplatcraftGameRules.DROP_CRATE_LOOT) && !silkTouched)
				return crate.getDrops();
		}
		
		return super.getDrops(state, builder);
	}
	
	@Override
	public boolean inkBlock(World world, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
	{
		if(world.getTileEntity(pos) instanceof CrateTileEntity)
			((CrateTileEntity) world.getTileEntity(pos)).ink(color, damage);
		
		return false;
	}
	
	@Override
	public boolean canClimb()
	{
		return false;
	}
	
	@Override
	public boolean canSwim()
	{
		return false;
	}
	
	@Override
	public boolean canDamage()
	{
		return false;
	}
	
	@Override
	public void remoteColorChange(World world, BlockPos pos, int newColor)
	{
	
	}
	
	@Override
	public void remoteInkClear(World world, BlockPos pos)
	{
	
	}
	
	@Override
	public boolean countsTowardsTurf(World world, BlockPos pos)
	{
		return false;
	}
}
