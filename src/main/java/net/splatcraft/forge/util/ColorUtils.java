package net.splatcraft.forge.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.IColoredEntity;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.items.ColoredBlockItem;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerColorPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.tileentities.InkColorTileEntity;

import java.util.Objects;
import java.util.Random;

public class ColorUtils
{
    public static final int ORANGE = 0xDF641A;
    public static final int BLUE = 0x26229F;
    public static final int GREEN = 0x409d3b;
    public static final int PINK = 0xc83d79;

    public static final int DEFAULT = 0x1F1F2D;

    public static final int[] STARTER_COLORS = new int[]{ORANGE, BLUE, GREEN, PINK};

    public static int getColorFromNbt(CompoundNBT nbt) {
        if (!nbt.contains("Color"))
            return -1;

        String str = nbt.getString("Color");

        if(!str.isEmpty())
        {
            if (CommonUtils.isResourceNameValid(str)) {
                InkColor colorObj = SplatcraftInkColors.REGISTRY.getValue(new ResourceLocation(str));

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
        if (entity instanceof PlayerEntity)
        {
            return getPlayerColor((PlayerEntity) entity);
        } else if (entity instanceof IColoredEntity)
        {
            return ((IColoredEntity) entity).getColor();
        } else
        {
            return -1;
        }
    }

    public static int getPlayerColor(PlayerEntity player)
    {
        if(PlayerInfoCapability.hasCapability(player))
            return PlayerInfoCapability.get(player).getColor();
        return 0;
    }

    public static void setPlayerColor(PlayerEntity player, int color, boolean updateClient)
    {
        if (PlayerInfoCapability.hasCapability(player))
        {
            PlayerInfoCapability.get(player).setColor(color);
            ScoreboardHandler.updatePlayerColorScore(player, color);
        }

        World level = player.level;
        if (!level.isClientSide && updateClient)
        {
            SplatcraftPacketHandler.sendToAll(new PlayerColorPacket(player, color));
        }
    }

    public static void setPlayerColor(PlayerEntity player, int color)
    {
        setPlayerColor(player, color, true);
    }

    public static int getInkColor(ItemStack stack)
    {
        return getColorFromNbt(stack.getOrCreateTag());
    }

    public static ItemStack setInkColor(ItemStack stack, int color)
    {
        stack.getOrCreateTag().putInt("Color", color);
        return stack;
    }

    public static int getInkColor(TileEntity te)
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

    public static boolean setInkColor(TileEntity te, int color)
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

    public static TextComponent getColorName(int color)
    {
        InkColor colorObj = InkColor.getByHex(color);

        // String colorFormatting = ""; // TextFormatting.fromColorIndex(color).toString();

        if (colorObj != null)
        {
            return new StringTextComponent(colorObj.getLocalizedName());
        }

        String fallbackUnloc;
        String fallbackName;

        try
        {
            fallbackUnloc = "ink_color." + String.format("%06X", color).toLowerCase();
            fallbackName = new TranslationTextComponent(fallbackUnloc).getString();
            if (!fallbackName.equals(fallbackName))
            {
                return new StringTextComponent(fallbackUnloc);
            }
        } catch (NoClassDefFoundError ignored)
        {
        }


        colorObj = InkColor.getByHex(0xFFFFFF - color);
        if (colorObj != null)
        {
            return new TranslationTextComponent("ink_color.invert", colorObj.getLocalizedName());
        }

        try
        {
            fallbackUnloc = "ink_color." + String.format("%06X", 0xFFFFFF - color).toLowerCase();
            fallbackName = new TranslationTextComponent(fallbackUnloc).getString();

            if (!fallbackName.equals(fallbackUnloc))
            {
                return new TranslationTextComponent("ink_color.invert", fallbackName);
            }
        } catch (NoClassDefFoundError ignored)
        {
        }


        return new StringTextComponent("#" + String.format("%06X", color).toUpperCase());

    }

    public static String getColorId(int color)
    {
        InkColor colorObj = InkColor.getByHex(color);
        if (colorObj != null)
            return colorObj.getLocalizedName();
        else return String.format("#%06X", color).toLowerCase();
    }

    public static ITextComponent getFormatedColorName(int color, boolean colorless)
    {
        return color == ColorUtils.DEFAULT
                ? new StringTextComponent((colorless ? TextFormatting.GRAY : "") + getColorName(color).getString())
                :getColorName(color).withStyle(getColorName(color).getStyle().withColor(Color.fromRgb(color)));
    }

    public static boolean colorEquals(World level, int colorA, int colorB)
    {
        return SplatcraftGameRules.getBooleanRuleValue(level, SplatcraftGameRules.UNIVERSAL_INK) || colorA == colorB;
    }

    public static boolean colorEquals(Entity entity, TileEntity te)
    {
        int entityColor = getEntityColor(entity);
        int inkColor = getInkColor(te);

        if (entityColor == -1 || inkColor == -1)
            return false;
        return colorEquals(entity.level, entityColor, inkColor);
    }

    public static boolean colorEquals(LivingEntity entity, ItemStack stack)
    {
        int entityColor = getEntityColor(entity);
        int inkColor = getInkColor(stack);

        if (entityColor == -1 || inkColor == -1)
            return false;
        return colorEquals(entity.level, entityColor, inkColor);
    }

    public static ItemStack setColorLocked(ItemStack stack, boolean isLocked)
    {
        stack.getOrCreateTag().putBoolean("ColorLocked", isLocked);
        return stack;
    }

    public static boolean isColorLocked(ItemStack stack)
    {
        CompoundNBT nbt = stack.getTag();

        if (nbt == null || !nbt.contains("ColorLocked"))
            return stack.getItem() instanceof ColoredBlockItem;
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
        return SplatcraftTags.InkColors.STARTER_COLORS.getValues().isEmpty()
                ? SplatcraftInkColors.undyed.getColor()
                : SplatcraftTags.InkColors.STARTER_COLORS.getRandomElement(new Random()).getColor();
    }

    public static void addInkSplashParticle(World level, LivingEntity source, float size)
    {
        int color = DEFAULT;
        if (PlayerInfoCapability.hasCapability(source))
        {
            color = PlayerInfoCapability.get(source).getColor();
        }


        addInkSplashParticle(level, color, source.getX(), source.getEyePosition((level.getRandom().nextFloat() * 0.3f)), source.getZ(), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(ServerWorld level, LivingEntity source, float size)
    {
        int color = DEFAULT;
        if (PlayerInfoCapability.hasCapability(source))
        {
            color = PlayerInfoCapability.get(source).getColor();
        }


        addInkSplashParticle(level, color, source.getX(), source.getEyePosition(level.getRandom().nextFloat() * 0.3f), source.getZ(), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addStandingInkSplashParticle(World level, LivingEntity entity, float size)
    {
        int color = DEFAULT;
        BlockPos pos = InkBlockUtils.getBlockStandingOnPos(entity);
        if (entity.level.getBlockState(pos).getBlock() instanceof IColoredBlock)
        {
            color = ((IColoredBlock) entity.level.getBlockState(pos).getBlock()).getColor(level, pos);
        }
        addInkSplashParticle(level, color, entity.getX() + (level.getRandom().nextFloat() * 0.8 - 0.4), entity.getEyePosition(level.getRandom().nextFloat() * 0.3f), entity.getZ() + (level.getRandom().nextFloat() * 0.8 - 0.4), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(World level, int color, double x, double y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        level.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void addInkSplashParticle(World level, int color, double x, Vector3d y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        level.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y.y, z, 0.0D, 0.0D, 0.0D);
    }

}
