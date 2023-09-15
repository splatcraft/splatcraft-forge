package net.splatcraft.forge.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.CrateTileEntity;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CrateBlock extends Block implements IColoredBlock, EntityBlock
{
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 4);
    public static final ResourceLocation STORAGE_SUNKEN_CRATE = new ResourceLocation(Splatcraft.MODID, "storage/sunken_crate");

    public final boolean hasLoot;

    public CrateBlock(String name, boolean hasLoot)
    {
        super(Properties.of(Material.WOOD).requiresCorrectToolForDrops().sound(SoundType.WOOD).strength(2.0f));

        this.hasLoot = hasLoot;

        SplatcraftBlocks.inkColoredBlocks.add(this);
    }

    public static List<ItemStack> generateLoot(Level level, BlockPos pos, BlockState state, float luckValue)
    {
        if (level == null || level.isClientSide)
            return Collections.emptyList();

        BlockEntity crate = level.getBlockEntity(pos);

        LootContext.Builder contextBuilder = new LootContext.Builder((ServerLevel) level);
        return level.getServer().getLootTables().get((crate instanceof CrateTileEntity) ? ((CrateTileEntity) crate).getLootTable() : STORAGE_SUNKEN_CRATE).getRandomItems(contextBuilder.withLuck(luckValue)
                .withParameter(LootContextParams.BLOCK_STATE, state).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ())).create(LootContextParamSets.BLOCK));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter levelIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn)
    {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        CompoundTag compoundnbt = stack.getTagElement("BlockEntityTag");

        if (compoundnbt != null && !hasLoot && compoundnbt.contains("Items", 9))
        {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            
            ContainerHelper.loadAllItems(compoundnbt, nonnulllist);
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
                        MutableComponent iformattabletextcomponent = itemstack.getDisplayName().copy();
                        iformattabletextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                        tooltip.add(iformattabletextcomponent);
                    }
                }
            }

            if (j - i > 0)
            {
                tooltip.add(new TranslatableComponent("container.shulkerBox.more", j - i).withStyle(ChatFormatting.ITALIC));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(STATE);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, LevelAccessor levelIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos)
    {
        if (levelIn.getBlockEntity(currentPos) instanceof CrateTileEntity)
        {
            return stateIn.setValue(STATE, ((CrateTileEntity) levelIn.getBlockEntity(currentPos)).getState());
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state)
    {
        return !hasLoot;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level levelIn, @NotNull BlockPos pos)
    {

        if (hasLoot || !(levelIn.getBlockEntity(pos) instanceof CrateTileEntity))
        {
            return 0;
        }
        ItemStack stack = ((CrateTileEntity) levelIn.getBlockEntity(pos)).getItem(0);
        return (int) Math.ceil(stack.getCount() / (float) stack.getMaxStackSize() * 15);
    }

    @Override
    public void playerDestroy(Level levelIn, Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack stack)
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
        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        Level level = builder.getLevel();

        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

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
    public boolean inkBlock(Level level, BlockPos pos, int color, float damage, InkBlockUtils.InkType inkType)
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
    public boolean remoteColorChange(Level level, BlockPos pos, int newColor)
    {
        return false;
    }

    @Override
    public boolean remoteInkClear(Level level, BlockPos pos)
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        CrateTileEntity te = SplatcraftTileEntities.crateTileEntity.get().create(pos, state);

        if (te != null)
        {
            te.setMaxHealth(hasLoot ? 25 : 20);
            te.resetHealth();
            te.setHasLoot(hasLoot);
            te.setColor(-1);
        }
        
        return te;
    }
}
