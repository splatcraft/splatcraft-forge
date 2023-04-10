package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.blocks.CrateBlock;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.CommonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrateTileEntity extends InkColorTileEntity implements Container
{
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private float health;
    private float maxHealth;
    private boolean hasLoot;
    private ResourceLocation lootTable = CrateBlock.STORAGE_SUNKEN_CRATE;

    public CrateTileEntity(BlockPos pos, BlockState state)
    {
        super(SplatcraftTileEntities.crateTileEntity.get(), pos, state);
    }

    public void ink(int color, float damage)
    {
        if (level != null && level.isClientSide)
        {
            return;
        }

        setColor(color);
        health -= damage;
        if (health <= 0)
        {
            level.destroyBlock(getBlockPos(), false);

            dropInventory();
        } else
        {
            level.setBlock(getBlockPos(), getBlockState().setValue(CrateBlock.STATE, getState()), 2);
        }

    }

    @Override
    public void onLoad()
    {
        super.onLoad();
    }

    public void dropInventory()
    {
        if (level != null && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
        {
            getDrops().forEach(stack -> CommonUtils.blockDrop(level, getBlockPos(), stack));
        }
    }

    public List<ItemStack> getDrops()
    {
        return hasLoot ? CrateBlock.generateLoot(level, getBlockPos(), getBlockState(), 0f) : getInventory();
    }

    public ResourceLocation getLootTable()
    {
        return lootTable;
    }

    @Override
    public void load(@NotNull CompoundTag nbt)
    {
        super.load(nbt);

        health = nbt.getFloat("Health");
        maxHealth = nbt.getFloat("MaxHealth");
        ContainerHelper.loadAllItems(nbt, inventory);

        if (getBlockState().getBlock() instanceof CrateBlock)
        {
            hasLoot = ((CrateBlock) getBlockState().getBlock()).hasLoot;
            if(nbt.contains("LootTable"))
                lootTable = new ResourceLocation(nbt.getString("LootTable"));
        }
    }

    @Override
    public @NotNull void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("Health", health);
        nbt.putFloat("MaxHealth", maxHealth);
        ContainerHelper.saveAllItems(nbt, inventory);

        if(hasLoot)
            nbt.putString("LootTable", lootTable.toString());

        super.saveAdditional(nbt);
    }

    @Override
    public int getContainerSize()
    {
        return getBlockState().getBlock() instanceof CrateBlock && ((CrateBlock) getBlockState().getBlock()).hasLoot ? 0 : 1;
    }

    @Override
    public boolean isEmpty()
    {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        if (getBlockState().getBlock() instanceof CrateBlock && ((CrateBlock) getBlockState().getBlock()).hasLoot)
        {
            return ItemStack.EMPTY;
        }

        ItemStack itemstack = ContainerHelper.removeItem(inventory, index, count);
        if (!itemstack.isEmpty())
        {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return ContainerHelper.takeItem(inventory, index);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        inventory.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize())
        {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return false;
    }

    @Override
    public void clearContent()
    {
        inventory.clear();
    }

    public float getHealth()
    {
        return health;
    }

    public void setHealth(float value)
    {
        health = value;
    }

    public void resetHealth()
    {
        setHealth(maxHealth);
        setColor(-1);
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public void setMaxHealth(float value)
    {
        maxHealth = value;
    }

    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    public int getState()
    {
        if (health == maxHealth)
        {
            setColor(-1);
        }
        return 4 - Math.round(health * 4 / maxHealth);
    }

    public void setHasLoot(boolean hasLoot)
    {
        this.hasLoot = hasLoot;
    }
}
