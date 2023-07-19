package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.splatcraft.forge.blocks.InkVatBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.FilterItem;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.tileentities.container.InkVatContainer;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

public class InkVatTileEntity extends BaseContainerBlockEntity implements WorldlyContainer
{
    private static final int[] INPUT_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] OUTPUT_SLOTS = new int[]{4};
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    public int pointer = -1;
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    private int color = -1;
    private int recipeEntries = 0;

    public InkVatTileEntity(BlockPos pos, BlockState state)
    {
        super(SplatcraftTileEntities.inkVatTileEntity.get(), pos, state);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        return side == Direction.UP ? INPUT_SLOTS : OUTPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction)
    {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        return index == 4;
    }

    @Override
    public int getContainerSize()
    {
        return inventory.size();
    }

    @Override
    public boolean isEmpty()
    {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        if (index == 4 && !consumeIngredients(count))
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

    public boolean consumeIngredients(int count)
    {
        if (inventory.get(0).getCount() >= count && inventory.get(1).getCount() >= count && inventory.get(2).getCount() >= count)
        {
            removeItem(0, count);
            removeItem(1, count);
            removeItem(2, count);
            return true;
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InkVatTileEntity te) {
        te.updateRecipeOutput();
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(InkVatBlock.ACTIVE, te.hasRecipe()), 3);
        }
    }

    public void updateRecipeOutput()
    {
        if (hasRecipe()) {
            setItem(4, ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(SplatcraftItems.inkwell.get(), Math.min(SplatcraftItems.inkwell.get().getMaxStackSize(),
                    Math.min(Math.min(inventory.get(0).getCount(), inventory.get(1).getCount()), inventory.get(2).getCount()))), getColor()), true));
        } else setItem(4, ItemStack.EMPTY);
    }

    public boolean hasOmniFilter()
    {
        if (inventory.get(3).getItem() instanceof FilterItem filter)
            return filter.isOmni();
        return false;
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
        if (this.level.getBlockEntity(this.getBlockPos()) != this)
        {
            return false;
        }
        return !(player.distanceToSqr((double) this.getBlockPos().getX() + 0.5D, (double) this.getBlockPos().getY() + 0.5D, (double) this.getBlockPos().getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void clearContent()
    {
        inventory.clear();
    }

    public boolean hasRecipe() {
        return !inventory.get(0).isEmpty() && !inventory.get(1).isEmpty() && !inventory.get(2).isEmpty() && getColor() != -1;
    }

    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("Color", color);
        nbt.putInt("Pointer", pointer);
        nbt.putInt("RecipeEntries", recipeEntries);
        ContainerHelper.saveAllItems(nbt, inventory);
        super.saveAdditional(nbt);
    }

    @Override
    protected Component getDefaultName()
    {
        return new TranslatableComponent("container.ink_vat");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player)
    {
        return new InkVatContainer(id, player, this, false);
    }

    //Nbt Read
    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        color = ColorUtils.getColorFromNbt(nbt);
        pointer = nbt.getInt("Pointer");
        recipeEntries = nbt.getInt("RecipeEntries");

        clearContent();
        ContainerHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        return new CompoundTag(){{saveAdditional(this);}};
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        if (level != null)
        {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 2);
            handleUpdateTag(pkt.getTag());
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return switch (index) {
            case 0 -> ItemStack.isSame(stack, new ItemStack(Items.INK_SAC));
            case 1 -> ItemStack.isSame(stack, new ItemStack(SplatcraftItems.powerEgg.get()));
            case 2 -> ItemStack.isSame(stack, new ItemStack(SplatcraftItems.emptyInkwell.get()));
            case 3 -> stack.is(SplatcraftTags.Items.FILTERS);
            default -> false;
        };

    }

    public void onRedstonePulse()
    {
        if (hasRecipe()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            if (pointer != -1 && recipeEntries > 0) {
                pointer = (pointer + 1) % recipeEntries;
                setColor(InkVatContainer.sortRecipeList(InkVatContainer.getAvailableRecipes(this)).get(pointer));
            }
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setRecipeEntries(int v) {
        recipeEntries = v;
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing)
    {
        if (!this.isRemoved() && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (facing == Direction.UP)
            {
                return handlers[0].cast();
            } else if (facing == Direction.DOWN)
            {
                return handlers[1].cast();
            } else
            {
                return handlers[2].cast();
            }
        }
        return super.getCapability(capability, facing);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved()
    {
        super.setRemoved();
        for (LazyOptional<? extends IItemHandler> handler : handlers)
        {
            handler.invalidate();
        }
    }

    public boolean setColorAndUpdate(int color)
    {
        boolean changeState = Math.min(color, 0) != Math.min(getColor(), 0);
        setColor(color);
        if (level != null)
        {
            if (changeState)
            {
                level.setBlock(getBlockPos(), getBlockState().setValue(InkVatBlock.ACTIVE, hasRecipe()), 2);
            } else
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            }
        }
        return true;
    }
}
