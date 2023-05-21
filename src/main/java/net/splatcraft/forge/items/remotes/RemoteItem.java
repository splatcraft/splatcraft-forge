package net.splatcraft.forge.items.remotes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class RemoteItem extends Item implements CommandSource
{
    public static final List<RemoteItem> remotes = new ArrayList<>();
    protected final int totalModes;

    public RemoteItem(Properties properties)
    {
        this(properties, 1);
    }

    public RemoteItem(Properties properties, int totalModes)
    {
        super(properties);
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

    public static Tuple<BlockPos, BlockPos> getCoordSet(ItemStack stack, Level level)
    {
        if (!hasCoordSet(stack))
            return null;
        CompoundTag nbt = stack.getTag();

        if(nbt.contains("Stage"))
        {
            Stage stage = level.isClientSide() ? ClientUtils.clientStages.get(nbt.getString("Stage")) : SaveInfoCapability.get(level.getServer()).getStages().get(nbt.getString("Stage"));
            if(stage == null)
                return null;

            return new Tuple<>(stage.cornerA, stage.cornerB);
        }

        return new Tuple<>(NbtUtils.readBlockPos(nbt.getCompound("PointA")), NbtUtils.readBlockPos(nbt.getCompound("PointB")));
    }

    public static boolean addCoords(Level level, ItemStack stack, BlockPos pos)
    {
        if (hasCoordSet(stack))
            return false;

        CompoundTag nbt = stack.getOrCreateTag();


        if(!nbt.contains("Dimension"))
            nbt.putString("Dimension", level.dimension().location().toString());
        else if(!level.equals(getLevel(level, stack)))
                return false;

        nbt.put("Point" + (stack.getOrCreateTag().contains("PointA") ? "B" : "A"), NbtUtils.writeBlockPos(pos));

        return true;
    }

    public static Level getLevel(Level level, ItemStack stack)
    {
        CompoundTag nbt = stack.getOrCreateTag();

        Level result = level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, nbt.contains("Stage") ?
                (level.isClientSide() ? ClientUtils.clientStages.get(nbt.getString("Stage")) : SaveInfoCapability.get(level.getServer()).getStages().get(nbt.getString("Stage"))).dimID
                : new ResourceLocation(nbt.getString("Dimension"))));

        return result == null ? level : result;
    }

    public static RemoteResult createResult(boolean success, Component output)
    {
        return new RemoteResult(success, output);
    }

    public ClampedItemPropertyFunction getActiveProperty()
    {
        return (stack, level, entity, seed) -> hasCoordSet(stack) ? 1 : 0;
    }

    public ClampedItemPropertyFunction getModeProperty()
    {
        return (stack, level, entity, seed) -> getRemoteMode(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);

        CompoundTag nbt = stack.getOrCreateTag();

        if(!nbt.contains("Stage") || ClientUtils.clientStages.containsKey(nbt.getString("Stage")))
        {
            if (hasCoordSet(stack))
            {
                Tuple<BlockPos, BlockPos> set = getCoordSet(stack, levelIn);
                tooltip.add(new TranslatableComponent("item.remote.coords.b", set.getA().getX(), set.getA().getY(), set.getA().getZ(),
                        set.getB().getX(), set.getB().getY(), set.getB().getZ()));
            } else if (stack.getOrCreateTag().contains("PointA"))
            {
                BlockPos pos = NbtUtils.readBlockPos(nbt.getCompound("PointA"));
                tooltip.add(new TranslatableComponent("item.remote.coords.a", pos.getX(), pos.getY(), pos.getZ()));
            }
        }
        else tooltip.add(new TranslatableComponent("item.remote.coords.invalid").withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(true)));

        if(nbt.contains("Targets") && !nbt.getString("Targets").isEmpty())
            tooltip.add(ComponentUtils.mergeStyles(new TextComponent(nbt.getString("Targets")), TARGETS_STYLE));
    }

    protected static final Style TARGETS_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_BLUE).withItalic(true);

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        if (context.getLevel().isClientSide)
        {
            return hasCoordSet(context.getItemInHand()) ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }

        if (addCoords(context.getLevel(), context.getItemInHand(), context.getClickedPos()))
        {
            String key = context.getItemInHand().getOrCreateTag().contains("PointB") ? "b" : "a";
            BlockPos pos = context.getClickedPos();

            context.getPlayer().displayClientMessage(new TranslatableComponent("status.coord_set." + key, pos.getX(), pos.getY(), pos.getZ()), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int mode = getRemoteMode(stack);

        if (playerIn.isCrouching() && totalModes > 1)
        {
            mode = cycleRemoteMode(stack);
            String statusMsg = getDescriptionId() + ".mode." + mode;

            if (levelIn.isClientSide && I18n.exists(statusMsg))
            {
                playerIn.displayClientMessage(new TranslatableComponent("status.remote_mode", new TranslatableComponent(statusMsg)), true);
            }
        } else if (hasCoordSet(stack) && !levelIn.isClientSide)
        {
            RemoteResult remoteResult = onRemoteUse(levelIn, stack, ColorUtils.getPlayerColor(playerIn), playerIn.position(), playerIn);

            if (remoteResult.getOutput() != null)
            {
                playerIn.displayClientMessage(remoteResult.getOutput(), true);
            }
            levelIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SplatcraftSounds.remoteUse, SoundSource.BLOCKS, 0.8f, 1);
            return new InteractionResultHolder<>(remoteResult.wasSuccessful() ? InteractionResult.SUCCESS : InteractionResult.FAIL, stack);
        }


        return super.use(levelIn, playerIn, handIn);
    }

    public static final Collection<ServerPlayer> ALL_TARGETS = new ArrayList<>();

    public abstract RemoteResult onRemoteUse(Level usedOnWorld, BlockPos posA, BlockPos posB, ItemStack stack, int colorIn, int mode, Collection<ServerPlayer> targets);

    public RemoteResult onRemoteUse(Level usedOnWorld, ItemStack stack, int colorIn, Vec3 pos, Entity user)
    {
        Tuple<BlockPos, BlockPos> coordSet = getCoordSet(stack, usedOnWorld);

        if (coordSet == null)
            return new RemoteResult(false, new TranslatableComponent("status.remote.undefined_area"));

        Collection<ServerPlayer> targets = ALL_TARGETS;

        if (stack.getTag().contains("Targets") && !stack.getTag().getString("Targets").isEmpty())
            try {
                targets = EntityArgument.players().parse(new StringReader(stack.getTag().getString("Targets"))).findPlayers(createCommandSourceStack(stack, (ServerLevel) usedOnWorld, pos, user));
            } catch (CommandSyntaxException e) {
                return new RemoteResult(false, new TextComponent(e.getMessage()));
            }

        return onRemoteUse(usedOnWorld, coordSet.getA(), coordSet.getB(), stack, colorIn, getRemoteMode(stack), targets);
    }

    public CommandSourceStack createCommandSourceStack(ItemStack stack, ServerLevel level, Vec3 pos, Entity user)
    {
        return new CommandSourceStack(this, pos, Vec2.ZERO, level, 2, getName(stack).toString(), getName(stack), level.getServer(), user);
    }

    @Override
    public void sendMessage(Component p_145747_1_, UUID p_145747_2_) {

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
        Component output;

        int commandResult = 0;
        int comparatorResult = 0;

        public RemoteResult(boolean success, Component output)
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

        public Component getOutput()
        {
            return output;
        }


    }
}
