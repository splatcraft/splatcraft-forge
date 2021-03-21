package com.cibernet.splatcraft.items.remotes;

import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class RemoteItem extends Item
{
    public static final List<RemoteItem> remotes = new ArrayList<>();
    protected final int totalModes;

    public RemoteItem(String name, Properties properties)
    {
        this(name, properties, 1);
    }

    public RemoteItem(String name, Properties properties, int totalModes)
    {
        super(properties);
        setRegistryName(name);
        remotes.add(this);

        this.totalModes = totalModes;
    }

    public static int getRemoteMode(ItemStack stack)
    {
        return stack.getOrCreateTag().getInt("Mode");
    }

    public static void setRemoteMode(ItemStack stack, int mode)
    {
        stack.getOrCreateTag().putInt("Mode", mode);
    }

    public static int cycleRemoteMode(ItemStack stack)
    {
        int mode = getRemoteMode(stack) + 1;
        if (stack.getItem() instanceof RemoteItem)
        {
            mode %= ((RemoteItem) stack.getItem()).totalModes;
        }
        setRemoteMode(stack, mode);
        return mode;
    }

    public static boolean hasCoordSet(ItemStack stack)
    {
        return hasCoordA(stack) && hasCoordB(stack);
    }

    public static boolean hasCoordA(ItemStack stack)
    {
        CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.contains("PointAX") && nbt.contains("PointAY") && nbt.contains("PointAZ");
    }

    public static boolean hasCoordB(ItemStack stack)
    {
        CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.contains("PointBX") && nbt.contains("PointBY") && nbt.contains("PointBZ");
    }

    public static BlockPos[] getCoordSet(ItemStack stack)
    {
        if (!hasCoordSet(stack))
        {
            return new BlockPos[0];
        }
        CompoundNBT nbt = stack.getTag();

        return new BlockPos[]{new BlockPos(nbt.getInt("PointAX"), nbt.getInt("PointAY"), nbt.getInt("PointAZ")), new BlockPos(nbt.getInt("PointBX"), nbt.getInt("PointBY"), nbt.getInt("PointBZ"))};
    }

    public static boolean addCoords(ItemStack stack, BlockPos pos)
    {
        if (hasCoordSet(stack))
        {
            return false;
        }

        CompoundNBT nbt = stack.getOrCreateTag();

        String key = hasCoordA(stack) ? "B" : "A";

        nbt.putInt("Point" + key + "X", pos.getX());
        nbt.putInt("Point" + key + "Y", pos.getY());
        nbt.putInt("Point" + key + "Z", pos.getZ());

        return true;
    }

    public static RemoteResult createResult(boolean success, TextComponent output)
    {
        return new RemoteResult(success, output);
    }

    public IItemPropertyGetter getActiveProperty()
    {
        return (stack, world, entity) -> hasCoordSet(stack) ? 1 : 0;
    }

    public IItemPropertyGetter getModeProperty()
    {
        return (stack, world, entity) -> getRemoteMode(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT nbt = stack.getOrCreateTag();

        if (hasCoordSet(stack))
        {
            tooltip.add(new TranslationTextComponent("item.remote.coords.b", nbt.getInt("PointAX"), nbt.getInt("PointAY"), nbt.getInt("PointAZ"),
                    nbt.getInt("PointBX"), nbt.getInt("PointBY"), nbt.getInt("PointBZ")));
        } else if (hasCoordA(stack))
        {
            tooltip.add(new TranslationTextComponent("item.remote.coords.a", nbt.getInt("PointAX"), nbt.getInt("PointAY"), nbt.getInt("PointAZ")));
        }

    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        if (context.getWorld().isRemote)
        {
            return ActionResultType.PASS;
        }

        if (addCoords(context.getItem(), context.getPos()))
        {
            String key = hasCoordA(context.getItem()) ? "b" : "a";
            BlockPos pos = context.getPos();

            context.getPlayer().sendStatusMessage(new TranslationTextComponent("status.coord_set." + key, pos.getX(), pos.getY(), pos.getZ()), true);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        int mode = getRemoteMode(stack);

        if (playerIn.isSneaking() && totalModes > 1)
        {
            mode = cycleRemoteMode(stack);
            String statusMsg = getTranslationKey() + ".mode." + mode;

            if (worldIn.isRemote && I18n.hasKey(statusMsg))
            {
                playerIn.sendStatusMessage(new TranslationTextComponent("status.remote_mode", new TranslationTextComponent(statusMsg)), true);
            }
        } else if (hasCoordSet(stack) && !worldIn.isRemote)
        {
            RemoteResult remoteResult = onRemoteUse(worldIn, stack, ColorUtils.getPlayerColor(playerIn), mode);

            if (remoteResult.getOutput() != null)
            {
                playerIn.sendStatusMessage(remoteResult.getOutput(), true);
            }
            worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SplatcraftSounds.remoteUse, SoundCategory.BLOCKS, 0.8f, 1);
            return new ActionResult<>(remoteResult.wasSuccessful() ? ActionResultType.SUCCESS : ActionResultType.FAIL, stack);
        }


        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public abstract RemoteResult onRemoteUse(World world, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode);

    public RemoteResult onRemoteUse(World world, ItemStack stack, int colorIn, int mode)
    {
        BlockPos[] coordSet = getCoordSet(stack);
        BlockPos blockpos = coordSet[0];
        BlockPos blockpos1 = coordSet[1];

        return onRemoteUse(world, blockpos, blockpos1, stack, colorIn, mode);
    }

    public static class RemoteResult
    {
        boolean success;
        TextComponent output;

        int commandResult = 0;
        int comparatorResult = 0;

        public RemoteResult(boolean success, TextComponent output)
        {
            this.success = success;
            this.output = output;
        }

        public RemoteResult setIntResults(int commandResult, int comparatorResult)
        {
            this.commandResult = commandResult;
            this.comparatorResult = comparatorResult;
            return this;
        }

        public int getCommandResult()
        {
            return commandResult;
        }

        public int getComparatorResult()
        {
            return comparatorResult;
        }

        public boolean wasSuccessful()
        {
            return success;
        }

        public TextComponent getOutput()
        {
            return output;
        }
    }
}
