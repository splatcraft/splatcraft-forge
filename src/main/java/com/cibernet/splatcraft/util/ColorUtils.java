package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.client.particles.InkSplashParticleData;
import com.cibernet.splatcraft.commands.arguments.InkColorArgument;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.entities.IColoredEntity;
import com.cibernet.splatcraft.handlers.ScoreboardHandler;
import com.cibernet.splatcraft.items.ColoredBlockItem;
import com.cibernet.splatcraft.network.PlayerColorPacket;
import com.cibernet.splatcraft.network.SplatcraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatcraftGameRules;
import com.cibernet.splatcraft.registries.SplatcraftInkColors;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

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

    public static int getColorFromNbt(CompoundNBT nbt)
    {
        if(!nbt.contains("Color"))
            return DEFAULT;

        String str = nbt.getString("Color");

        if(ResourceLocation.isResouceNameValid(str))
        {
            if(str.indexOf(':') <= 0)
            {
                if(str.indexOf(':') < 0)
                    str = ':' + str;
                str = Splatcraft.MODID + str;
            }

            InkColor colorObj = SplatcraftInkColors.REGISTRY.getValue(new ResourceLocation(str));

            if(colorObj != null)
                return colorObj.getColor();
        }

        if(str.charAt(0) == '#')
            return Integer.parseInt(str.substring(1), 16);

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
        try
        {
            return PlayerInfoCapability.get(player).getColor();
        } catch (NullPointerException e)
        {
            return 0;
        }
    }

    public static void setPlayerColor(PlayerEntity player, int color, boolean updateClient)
    {
        if (PlayerInfoCapability.hasCapability(player))
        {
            PlayerInfoCapability.get(player).setColor(color);
            ScoreboardHandler.updatePlayerColorScore(player, color);
        }

        World world = player.world;
        if (!world.isRemote && updateClient)
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
            return ((IColoredBlock) te.getBlockState().getBlock()).getColor(Objects.requireNonNull(te.getWorld()), te.getPos());
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
            return ((IColoredBlock) te.getBlockState().getBlock()).setColor(te.getWorld(), te.getPos(), color);
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
                : getColorName(color).setStyle(Style.EMPTY.setColor(Color.fromInt(color)));
    }

    public static boolean colorEquals(World world, int colorA, int colorB)
    {
        return SplatcraftGameRules.getBooleanRuleValue(world, SplatcraftGameRules.UNIVERSAL_INK) || colorA == colorB;
    }

    public static boolean colorEquals(Entity entity, TileEntity te)
    {
        int entityColor = getEntityColor(entity);
        int inkColor = getInkColor(te);

        if (entityColor == -1 || inkColor == -1)
            return false;
        return colorEquals(entity.world, entityColor, inkColor);
    }

    public static boolean colorEquals(LivingEntity entity, ItemStack stack)
    {
        int entityColor = getEntityColor(entity);
        int inkColor = getInkColor(stack);

        if (entityColor == -1 || inkColor == -1)
            return false;
        return colorEquals(entity.world, entityColor, inkColor);
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
        return SplatcraftTags.InkColors.STARTER_COLORS.getAllElements().isEmpty()
                ? SplatcraftInkColors.undyed.getColor()
                : SplatcraftTags.InkColors.STARTER_COLORS.getRandomElement(new Random()).getColor();
    }

    public static void addInkSplashParticle(World world, LivingEntity source, float size)
    {
        int color = DEFAULT;
        if (PlayerInfoCapability.hasCapability(source))
        {
            color = PlayerInfoCapability.get(source).getColor();
        }


        addInkSplashParticle(world, color, source.getPosX(), source.getPosYHeight(world.rand.nextDouble() * 0.3), source.getPosZ(), size + (world.rand.nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(ServerWorld world, LivingEntity source, float size)
    {
        int color = DEFAULT;
        if (PlayerInfoCapability.hasCapability(source))
        {
            color = PlayerInfoCapability.get(source).getColor();
        }


        addInkSplashParticle(world, color, source.getPosX(), source.getPosYHeight(world.rand.nextDouble() * 0.3), source.getPosZ(), size + (world.rand.nextFloat() * 0.2f - 0.1f));
    }

    public static void addStandingInkSplashParticle(World world, LivingEntity entity, float size)
    {
        int color = DEFAULT;
        BlockPos pos = InkBlockUtils.getBlockStandingOnPos(entity);
        if (entity.world.getBlockState(pos).getBlock() instanceof IColoredBlock)
        {
            color = ((IColoredBlock) entity.world.getBlockState(pos).getBlock()).getColor(world, pos);
        }
        addInkSplashParticle(world, color, entity.getPosX() + (world.rand.nextFloat() * 0.8 - 0.4), entity.getPosYHeight(world.rand.nextDouble() * 0.3), entity.getPosZ() + (world.rand.nextFloat() * 0.8 - 0.4), size + (world.rand.nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(World world, int color, double x, double y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        world.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void addInkSplashParticle(ServerWorld world, int color, double x, double y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        world.spawnParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y, z, 1, 0, 0, 0, 0);
    }

}
