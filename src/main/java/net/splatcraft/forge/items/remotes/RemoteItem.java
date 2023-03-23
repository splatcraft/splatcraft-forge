package net.splatcraft.forge.items.remotes;

import java.util.*;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

public abstract class RemoteItem extends Item implements ICommandSource
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
        return stack.getOrCreateTag().contains("Stage") || (stack.getOrCreateTag().contains("PointA") && stack.getOrCreateTag().contains("PointB"));
    }

    public static Tuple<BlockPos, BlockPos> getCoordSet(ItemStack stack, World level)
    {
        if (!hasCoordSet(stack))
            return null;
        CompoundNBT nbt = stack.getTag();

        if(nbt.contains("Stage"))
        {
            Stage stage = level.isClientSide() ? ClientUtils.clientStages.get(nbt.getString("Stage")) : SaveInfoCapability.get(level.getServer()).getStages().get(nbt.getString("Stage"));
            if(stage == null)
                return null;

            return new Tuple<>(stage.cornerA, stage.cornerB);
        }

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
        CompoundNBT nbt = stack.getOrCreateTag();

        World result = level.getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, nbt.contains("Stage") ?
                (level.isClientSide() ? ClientUtils.clientStages.get(nbt.getString("Stage")) : SaveInfoCapability.get(level.getServer()).getStages().get(nbt.getString("Stage"))).dimID
                : new ResourceLocation(nbt.getString("Dimension"))));

        return result == null ? level : result;
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
            Tuple<BlockPos, BlockPos> set = getCoordSet(stack, levelIn);
            tooltip.add(new TranslationTextComponent("item.remote.coords.b", set.getA().getX(), set.getA().getY(), set.getA().getZ(),
                    set.getB().getX(), set.getB().getY(), set.getB().getZ()));
        } else if (stack.getOrCreateTag().contains("PointA"))
        {
            BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("PointA"));
            tooltip.add(new TranslationTextComponent("item.remote.coords.a", pos.getX(), pos.getY(), pos.getZ()));
        }

        if(nbt.contains("Targets") && !nbt.getString("Targets").isEmpty())
            tooltip.add(TextComponentUtils.mergeStyles(new StringTextComponent(nbt.getString("Targets")), TARGETS_STYLE));
    }

    protected static final Style TARGETS_STYLE = Style.EMPTY.withColor(TextFormatting.DARK_BLUE).withItalic(true);

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
            RemoteResult remoteResult = onRemoteUse(levelIn, stack, ColorUtils.getPlayerColor(playerIn), playerIn.position(), playerIn);

            if (remoteResult.getOutput() != null)
            {
                playerIn.displayClientMessage(remoteResult.getOutput(), true);
            }
            levelIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SplatcraftSounds.remoteUse, SoundCategory.BLOCKS, 0.8f, 1);
            return new ActionResult<>(remoteResult.wasSuccessful() ? ActionResultType.SUCCESS : ActionResultType.FAIL, stack);
        }


        return super.use(levelIn, playerIn, handIn);
    }

    public static final Collection<ServerPlayerEntity> ALL_TARGETS = new ArrayList<>();

    public abstract RemoteResult onRemoteUse(World usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode, Collection<ServerPlayerEntity> targets);

    public RemoteResult onRemoteUse(World usedOnWorld, ItemStack stack, int colorIn, Vector3d pos, Entity user)
    {
        Tuple<BlockPos, BlockPos> coordSet = getCoordSet(stack, usedOnWorld);

        if(coordSet == null)
            return new RemoteResult(false, new TranslationTextComponent("status.remote.undefined_area"));

        Collection<ServerPlayerEntity> targets = ALL_TARGETS;

        if(stack.getTag().contains("Targets") && !stack.getTag().getString("Targets").isEmpty())
        try {
            targets = EntityArgument.players().parse(new StringReader(stack.getTag().getString("Targets"))).findPlayers(createCommandSourceStack(stack, (ServerWorld) usedOnWorld, pos, user));
        } catch (CommandSyntaxException e) {
            targets = Collections.emptyList();
            System.out.println(e.getMessage());
        }

        return onRemoteUse(usedOnWorld, coordSet.getA(), coordSet.getB(), stack, colorIn, getRemoteMode(stack), targets);
    }

    public CommandSource createCommandSourceStack(ItemStack stack, ServerWorld level, Vector3d pos, Entity user)
    {
        return new CommandSource(this, pos, Vector2f.ZERO, level, 2, getName(stack).toString(), getName(stack), level.getServer(), user);
    }

    @Override
    public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {

    }

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
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
