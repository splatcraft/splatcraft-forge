package net.splatcraft.forge.items.remotes;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

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
        return stack.getOrCreateTag().contains("PointA") && stack.getOrCreateTag().contains("PointB");
    }

    public static Tuple<BlockPos, BlockPos> getCoordSet(ItemStack stack)
    {
        if (!hasCoordSet(stack))
            return null;
        CompoundNBT nbt = stack.getTag();
        return new Tuple<>(NBTUtil.readBlockPos(nbt.getCompound("PointA")), NBTUtil.readBlockPos(nbt.getCompound("PointB")));
    }

    public static boolean addCoords(World level, ItemStack stack, BlockPos pos)
    {
        if (hasCoordSet(stack))
            return false;

        CompoundNBT nbt = stack.getOrCreateTag();


        if(!nbt.contains("Dimension"))
            nbt.putString("Dimension", level.dimension().location().toString());
        else if(!level.equals(getLevel(level, stack)))
                return false;

        nbt.put("Point" + (stack.getOrCreateTag().contains("PointA") ? "B" : "A"), NBTUtil.writeBlockPos(pos));

        return true;
    }

    public static World getLevel(World level, ItemStack stack)
    {
        return level.getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(stack.getOrCreateTag().getString("Dimension"))));
    }

    public static RemoteResult createResult(boolean success, TextComponent output)
    {
        return new RemoteResult(success, output);
    }

    public IItemPropertyGetter getActiveProperty()
    {
        return (stack, level, entity) -> hasCoordSet(stack) ? 1 : 0;
    }

    public IItemPropertyGetter getModeProperty()
    {
        return (stack, level, entity) -> getRemoteMode(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World levelIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);

        CompoundNBT nbt = stack.getOrCreateTag();

        if (hasCoordSet(stack))
        {
            Tuple<BlockPos, BlockPos> set = getCoordSet(stack);
            tooltip.add(new TranslationTextComponent("item.remote.coords.b", set.getA().getX(), set.getA().getY(), set.getA().getZ(),
                    set.getB().getX(), set.getB().getY(), set.getB().getZ()));
        } else if (stack.getOrCreateTag().contains("PointA"))
        {
            BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("PointA"));
            tooltip.add(new TranslationTextComponent("item.remote.coords.a", pos.getX(), pos.getY(), pos.getZ()));
        }

    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        if (context.getLevel().isClientSide)
        {
            return hasCoordSet(context.getItemInHand()) ? ActionResultType.PASS : ActionResultType.SUCCESS;
        }

        if (addCoords(context.getLevel(), context.getItemInHand(), context.getClickedPos()))
        {
            String key = context.getItemInHand().getOrCreateTag().contains("PointB") ? "b" : "a";
            BlockPos pos = context.getClickedPos();

            context.getPlayer().displayClientMessage(new TranslationTextComponent("status.coord_set." + key, pos.getX(), pos.getY(), pos.getZ()), true);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> use(World levelIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int mode = getRemoteMode(stack);

        if (playerIn.isCrouching() && totalModes > 1)
        {
            mode = cycleRemoteMode(stack);
            String statusMsg = getDescriptionId() + ".mode." + mode;

            if (levelIn.isClientSide && I18n.exists(statusMsg))
            {
                playerIn.displayClientMessage(new TranslationTextComponent("status.remote_mode", new TranslationTextComponent(statusMsg)), true);
            }
        } else if (hasCoordSet(stack) && !levelIn.isClientSide)
        {
            RemoteResult remoteResult = onRemoteUse(levelIn, stack, ColorUtils.getPlayerColor(playerIn));

            if (remoteResult.getOutput() != null)
            {
                playerIn.displayClientMessage(remoteResult.getOutput(), true);
            }
            levelIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SplatcraftSounds.remoteUse, SoundCategory.BLOCKS, 0.8f, 1);
            return new ActionResult<>(remoteResult.wasSuccessful() ? ActionResultType.SUCCESS : ActionResultType.FAIL, stack);
        }


        return super.use(levelIn, playerIn, handIn);
    }

    public abstract RemoteResult onRemoteUse(World usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode);

    public RemoteResult onRemoteUse(World usedOnWorld, ItemStack stack, int colorIn)
    {
        Tuple<BlockPos, BlockPos> coordSet = getCoordSet(stack);

        if(coordSet == null)
            return new RemoteResult(false, null);

        return onRemoteUse(usedOnWorld, coordSet.getA(), coordSet.getB(), stack, colorIn, getRemoteMode(stack));
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
