package net.splatcraft.forge.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.IColoredEntity;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerColorPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.registries.SplatcraftStats;
import net.splatcraft.forge.tileentities.InkColorTileEntity;

import java.util.Objects;

public class ColorUtils
{
    public static final int ORANGE = 0xDF641A;
    public static final int BLUE = 0x26229F;
    public static final int GREEN = 0x409d3b;
    public static final int PINK = 0xc83d79;

    public static final int DEFAULT = 0x1F1F2D;

    public static final int[] STARTER_COLORS = new int[]{ORANGE, BLUE, GREEN, PINK};

    public static int getColorFromNbt(CompoundTag nbt) {
        if (!nbt.contains("Color"))
            return -1;

        String str = nbt.getString("Color");

        if(!str.isEmpty())
        {
            if (CommonUtils.isResourceNameValid(str)) {
                InkColor colorObj = SplatcraftInkColors.REGISTRY.get().getValue(new ResourceLocation(str));

                if (colorObj != null)
                    return colorObj.getColor();
            }

            if (str.charAt(0) == '#')
                return Integer.parseInt(str.substring(1), 16);
        }

        return nbt.getInt("Color");
    }

    public static int getEntityColor(Entity entity)
    {
        if (entity instanceof Player)
            return getPlayerColor((LivingEntity) entity);
        else if (entity instanceof IColoredEntity)
            return ((IColoredEntity) entity).getColor();
        else return -1;
    }

    public static int getPlayerColor(LivingEntity player)
    {
        if(PlayerInfoCapability.hasCapability(player))
            return PlayerInfoCapability.get(player).getColor();
        return 0;
    }

    public static void setPlayerColor(Player player, int color, boolean updateClient)
    {
        if (PlayerInfoCapability.hasCapability(player) && PlayerInfoCapability.get(player).getColor() != color)
        {
            if(player instanceof ServerPlayer serverPlayer)
                SplatcraftStats.CHANGE_INK_COLOR_TRIGGER.trigger(serverPlayer);

            PlayerInfoCapability.get(player).setColor(color);
            ScoreboardHandler.updatePlayerScore(ScoreboardHandler.COLOR, player, color);

        }

        Level level = player.level;
        if (!level.isClientSide && updateClient)
        {
            SplatcraftPacketHandler.sendToAll(new PlayerColorPacket(player, color));
        }
    }

    public static void setPlayerColor(Player player, int color)
    {
        setPlayerColor(player, color, true);
    }

    public static int getInkColor(ItemStack stack)
    {
        return getColorFromNbt(stack.getOrCreateTag());
    }

    public static ItemStack setInkColor(ItemStack stack, int color)
    {
        if(color == -1)
            stack.getOrCreateTag().remove("Color");
        else stack.getOrCreateTag().putInt("Color", color);
        return stack;
    }

    public static int getInkColor(BlockEntity te)
    {
        if (te == null)
        {
            return -1;
        }

        if (te instanceof InkColorTileEntity)
        {
            return ((InkColorTileEntity) te).getColor();
        }

        te.getBlockState();
        if (te.getBlockState().getBlock() instanceof IColoredBlock)
        {
            return ((IColoredBlock) te.getBlockState().getBlock()).getColor(Objects.requireNonNull(te.getLevel()), te.getBlockPos());
        }
        return -1;
    }

    public static boolean setInkColor(BlockEntity te, int color)
    {
        if (te instanceof InkColorTileEntity)
        {
            ((InkColorTileEntity) te).setColor(color);
            return true;
        }

        te.getBlockState();
        if (te.getBlockState().getBlock() instanceof IColoredBlock)
        {
            return ((IColoredBlock) te.getBlockState().getBlock()).setColor(te.getLevel(), te.getBlockPos(), color);
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getLockedColor(int color)
    {
        return Minecraft.getInstance().player != null
                ? ColorUtils.getPlayerColor(Minecraft.getInstance().player) == color
                ? SplatcraftInkColors.colorLockA.getColor()
                : SplatcraftInkColors.colorLockB.getColor()
                : -1;
    }

    public static MutableComponent getColorName(int color)
    {
        InkColor colorObj = InkColor.getByHex(color);

        // String colorFormatting = ""; // ChatFormatting.fromColorIndex(color).toString();

        if (colorObj != null)
        {
            return colorObj.getLocalizedName();
        }

        String fallbackUnloc;
        String fallbackName;

        try
        {
            fallbackUnloc = "ink_color." + String.format("%06X", color).toLowerCase();
            fallbackName = new TranslatableComponent(fallbackUnloc).getString();
            if (!fallbackName.equals(fallbackName))
            {
                return new TextComponent(fallbackUnloc);
            }
        } catch (NoClassDefFoundError ignored)
        {
        }


        colorObj = InkColor.getByHex(0xFFFFFF - color);
        if (colorObj != null)
        {
            return new TranslatableComponent("ink_color.invert", colorObj.getLocalizedName());
        }

        try
        {
            fallbackUnloc = "ink_color." + String.format("%06X", 0xFFFFFF - color).toLowerCase();
            fallbackName = new TranslatableComponent(fallbackUnloc).getString();

            if (!fallbackName.equals(fallbackUnloc))
            {
                return new TranslatableComponent("ink_color.invert", fallbackName);
            }
        } catch (NoClassDefFoundError ignored)
        {
        }


        return new TextComponent("#" + String.format("%06X", color).toUpperCase());

    }

    public static String getColorId(int color)
    {
        InkColor colorObj = InkColor.getByHex(color);
        if (colorObj != null)
            return colorObj.getUnlocalizedName();
        else return String.format("#%06X", color).toLowerCase();
    }

    public static MutableComponent getFormatedColorName(int color, boolean colorless)
    {
        return color == ColorUtils.DEFAULT
                ? new TextComponent((colorless ? ChatFormatting.GRAY : "") + getColorName(color).getString())
                : getColorName(color).withStyle(getColorName(color).getStyle().withColor(TextColor.fromRgb(color)));
    }

    public static boolean colorEquals(Level level, BlockPos pos, int colorA, int colorB)
    {
        return SplatcraftGameRules.getLocalizedRule(level, pos, SplatcraftGameRules.UNIVERSAL_INK) || colorA == colorB;
    }

    public static boolean colorEquals(Entity entity, BlockEntity te)
    {
        int entityColor = getEntityColor(entity);
        int inkColor = getInkColor(te);

        if (entityColor == -1 || inkColor == -1)
            return false;
        return colorEquals(entity.level, te.getBlockPos(), entityColor, inkColor);
    }

    public static boolean colorEquals(LivingEntity entity, ItemStack stack)
    {
        int entityColor = getEntityColor(entity);
        int inkColor = getInkColor(stack);

        if (entityColor == -1 || inkColor == -1)
            return false;
        return colorEquals(entity.level, entity.blockPosition(), entityColor, inkColor);
    }

    public static ItemStack setColorLocked(ItemStack stack, boolean isLocked)
    {
        stack.getOrCreateTag().putBoolean("ColorLocked", isLocked);
        return stack;
    }

    public static boolean isColorLocked(ItemStack stack)
    {
        CompoundTag nbt = stack.getTag();

        if (nbt == null || !nbt.contains("ColorLocked"))
            return false;
        return nbt.getBoolean("ColorLocked");
    }

    public static float[] hexToRGB(int color)
    {
        float r = ((color & 16711680) >> 16) / 255.0f;
        float g = ((color & '\uff00') >> 8) / 255.0f;
        float b = (color & 255) / 255.0f;

        return new float[]{r, g, b};
    }

    public static int getRandomStarterColor()
    {
        return 14640154;
        /* TODO data driven ink
        return SplatcraftTags.InkColors.STARTER_COLORS.getValues().isEmpty()
                ? SplatcraftInkColors.undyed.getColor()
                : SplatcraftTags.InkColors.STARTER_COLORS.getRandomElement(new Random()).getColor();
        */
    }

    public static void addInkSplashParticle(Level level, LivingEntity source, float size)
    {
        int color = DEFAULT;
        if (PlayerInfoCapability.hasCapability(source))
        {
            color = PlayerInfoCapability.get(source).getColor();
        }


        addInkSplashParticle(level, color, source.getX(), source.getEyePosition((level.getRandom().nextFloat() * 0.3f)), source.getZ(), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(ServerLevel level, LivingEntity source, float size)
    {
        int color = DEFAULT;
        if (PlayerInfoCapability.hasCapability(source))
        {
            color = PlayerInfoCapability.get(source).getColor();
        }


        addInkSplashParticle(level, color, source.getX(), source.getEyePosition(level.getRandom().nextFloat() * 0.3f), source.getZ(), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addStandingInkSplashParticle(Level level, LivingEntity entity, float size)
    {
        int color = DEFAULT;
        BlockPos pos = InkBlockUtils.getBlockStandingOnPos(entity);
        if (entity.level.getBlockState(pos).getBlock() instanceof IColoredBlock)
        {
            color = ((IColoredBlock) entity.level.getBlockState(pos).getBlock()).getColor(level, pos);
        }
        addInkSplashParticle(level, color, entity.getX() + (level.getRandom().nextFloat() * 0.8 - 0.4), entity.getY(level.getRandom().nextFloat() * 0.3f), entity.getZ() + (level.getRandom().nextFloat() * 0.8 - 0.4), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(Level level, int color, double x, double y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        level.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void addInkSplashParticle(Level level, int color, double x, Vec3 y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        level.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y.y, z, 0.0D, 0.0D, 0.0D);
    }

}
