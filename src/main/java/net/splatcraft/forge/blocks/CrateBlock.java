package net.splatcraft.forge.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.tileentities.CrateTileEntity;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CrateBlock extends Block implements IColoredBlock
{
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 4);
    public static final ResourceLocation STORAGE_SUNKEN_CRATE = new ResourceLocation(Splatcraft.MODID, "storage/sunken_crate");

    public final boolean hasLoot;

    public CrateBlock(String name, boolean hasLoot)
    {
        super(Properties.of(Material.WOOD).harvestTool(ToolType.AXE).requiresCorrectToolForDrops().sound(SoundType.WOOD).strength(2.0f));

        setRegistryName(name);
        this.hasLoot = hasLoot;

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    public static List<ItemStack> generateLoot(World level, BlockPos pos, BlockState state, float luckValue)
    {
        if (level == null || level.isClientSide)
            return Collections.emptyList();

        TileEntity crate = level.getBlockEntity(pos);

        LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) level);
        return level.getServer().getLootTables().get((crate instanceof CrateTileEntity) ? ((CrateTileEntity) crate).getLootTable() : STORAGE_SUNKEN_CRATE).getRandomItems(contextBuilder.withLuck(luckValue)
                .withParameter(LootParameters.BLOCK_STATE, state).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withParameter(LootParameters.ORIGIN, new Vector3d(pos.getX(), pos.getY(), pos.getZ())).create(LootParameterSets.BLOCK));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader levelIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        CompoundNBT compoundnbt = stack.getTagElement("BlockEntityTag");

        if (compoundnbt != null && !hasLoot && compoundnbt.contains("Items", 9))
        {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
            int i = 0;
            int j = 0;

            for (ItemStack itemstack : nonnulllist)
            {
                if (!itemstack.isEmpty())
                {
                    ++j;
                    if (i <= 4)
                    {
                        ++i;
                        IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().copy();
                        iformattabletextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                        tooltip.add(iformattabletextcomponent);
                    }
                }
            }

            if (j - i > 0)
            {
                tooltip.add(new TranslationTextComponent("container.shulkerBox.more", j - i).withStyle(TextFormatting.ITALIC));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(STATE);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, IWorld levelIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos)
    {
        if (levelIn.getBlockEntity(currentPos) instanceof CrateTileEntity)
        {
            return stateIn.setValue(STATE, ((CrateTileEntity) levelIn.getBlockEntity(currentPos)).getState());
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
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
        CrateTileEntity te = SplatcraftTileEntitites.crateTileEntity.create();
        if (te != null)
        {
            te.setMaxHealth(hasLoot ? 25 : 20);
            te.resetHealth();
            te.setHasLoot(hasLoot);
            te.setColor(-1);
        }

        return te;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state)
    {
        return !hasLoot;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull World levelIn, @NotNull BlockPos pos)
    {

        if (hasLoot || !(levelIn.getBlockEntity(pos) instanceof CrateTileEntity))
        {
            return 0;
        }
        ItemStack stack = ((CrateTileEntity) levelIn.getBlockEntity(pos)).getItem(0);
        return (int) Math.ceil(stack.getCount() / (float) stack.getMaxStackSize() * 15);
    }

    @Override
    public void playerDestroy(World levelIn, PlayerEntity player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable TileEntity te, @NotNull ItemStack stack)
    {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);


        if (levelIn.getGameRules().getBoolean(SplatcraftGameRules.DROP_CRATE_LOOT) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) <= 0 && levelIn.getBlockEntity(pos) instanceof CrateTileEntity)
        {
            ((CrateTileEntity) levelIn.getBlockEntity(pos)).dropInventory();
        } else
        {
            dropResources(state, levelIn, pos, te, player, stack);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootContext.Builder builder)
    {
        ItemStack tool = builder.getOptionalParameter(LootParameters.TOOL);
        World level = builder.getLevel();

        TileEntity te = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);

        if (te instanceof CrateTileEntity)
        {
            CrateTileEntity crate = (CrateTileEntity) te;

            boolean silkTouched = tool != null && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;

            if (level.getGameRules().getBoolean(SplatcraftGameRules.DROP_CRATE_LOOT) && !silkTouched)
            {
                return crate.getDrops();
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public boolean inkBlock(World level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
    {
        if (level.getBlockEntity(pos) instanceof CrateTileEntity)
        {
            ((CrateTileEntity) level.getBlockEntity(pos)).ink(color, damage);
        }

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
    public boolean remoteColorChange(World level, BlockPos pos, int newColor)
    {
        return false;
    }

    @Override
    public boolean remoteInkClear(World level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof CrateTileEntity)
        {
            CrateTileEntity crate = (CrateTileEntity) level.getBlockEntity(pos);
            if (crate.getHealth() == crate.getMaxHealth())
            {
                return false;
            }
            crate.resetHealth();
            level.setBlock(pos, crate.getBlockState().setValue(STATE, crate.getState()), 2);
            return true;
        }
        return false;
    }
}
