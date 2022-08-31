package net.splatcraft.forge.tileentities;

import net.splatcraft.forge.blocks.CrateBlock;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.util.CommonUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;

import java.util.List;

public class CrateTileEntity extends InkColorTileEntity implements IInventory
{
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private float health;
    private float maxHealth;
    private boolean hasLoot;
    private ResourceLocation lootTable = CrateBlock.STORAGE_SUNKEN_CRATE;

    public CrateTileEntity()
    {
        super(SplatcraftTileEntitites.crateTileEntity);
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
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);

        health = nbt.getFloat("Health");
        maxHealth = nbt.getFloat("MaxHealth");
        ItemStackHelper.loadAllItems(nbt, inventory);

        if (state.getBlock() instanceof CrateBlock)
        {
            hasLoot = ((CrateBlock) state.getBlock()).hasLoot;
            if(nbt.contains("LootTable"))
                lootTable = new ResourceLocation(nbt.getString("LootTable"));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putFloat("Health", health);
        nbt.putFloat("MaxHealth", maxHealth);
        ItemStackHelper.saveAllItems(nbt, inventory);

        if(hasLoot)
            nbt.putString("LootTable", lootTable.toString());

        return super.save(nbt);
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

        ItemStack itemstack = ItemStackHelper.removeItem(inventory, index, count);
        if (!itemstack.isEmpty())
        {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return ItemStackHelper.takeItem(inventory, index);
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
    public boolean stillValid(PlayerEntity player)
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
