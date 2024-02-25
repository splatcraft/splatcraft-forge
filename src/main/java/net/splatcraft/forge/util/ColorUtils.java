package net.splatcraft.forge.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.client.particles.InkSplashParticleData;
import net.splatcraft.forge.client.particles.InkTerrainParticleData;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.IColoredEntity;
import net.splatcraft.forge.handlers.DataHandler;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.PlayerColorPacket;
import net.splatcraft.forge.registries.SplatcraftGameRules;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.registries.SplatcraftStats;
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
        if(player.level.isClientSide && player instanceof Player p)
            return ClientUtils.getClientPlayerColor(p.getGameProfile().getId());
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

    public static boolean isInverted(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean("Inverted");
    }

    public static ItemStack setInverted(ItemStack stack, boolean inverted)
    {
        if(stack.hasTag() || inverted)
            stack.getOrCreateTag().putBoolean("Inverted", inverted);
        return stack;
    }

    public static int getInkColor(ItemStack stack)
    {
        return getColorFromNbt(stack.getOrCreateTag());
    }

    public static int getInkColorOrInverted(ItemStack stack)
    {
        int color = getInkColor(stack);
        return isInverted(stack) ? 0xFFFFFF - color : color;
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
        return getInkColor(te.getLevel(), te.getBlockPos());
    }

    public static int getInkColor(Level level, BlockPos pos)
    {
        if (level.getBlockState(pos).getBlock() instanceof IColoredBlock coloredBlock)
            return coloredBlock.getColor(level, pos);

        return -1;
    }

    public static int getInkColorOrInverted(Level level, BlockPos pos)
    {
        int color = getInkColor(level, pos);
       return isInverted(level, pos) ? 0xFFFFFF - color : color;
    }

    public static boolean isInverted(Level level, BlockPos pos)
    {
        return level.getBlockState(pos).getBlock() instanceof IColoredBlock coloredBlock && coloredBlock.isInverted(level, pos);
    }

    public static void setInverted(Level level, BlockPos pos, boolean inverted)
    {
        if(level.getBlockState(pos).getBlock() instanceof IColoredBlock coloredBlock)
            coloredBlock.setInverted(level, pos, inverted);
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
        if(entity == null || te == null)
            return false;

        int entityColor = getEntityColor(entity);
        int inkColor = getInkColorOrInverted(te.getLevel(), te.getBlockPos());

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

    private static final Random random = new Random();

    public static int getRandomStarterColor()
    {
        return DataHandler.InkColorTagsListener.STARTER_COLORS.getRandom(random);
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
        if(InkBlockUtils.isInked(level, pos))
            color = InkBlockUtils.getInk(level, pos).color();
        else if (entity.level.getBlockState(pos).getBlock() instanceof IColoredBlock)
            color = ((IColoredBlock) entity.level.getBlockState(pos).getBlock()).getColor(level, pos);
        addInkSplashParticle(level, color, entity.getX() + (level.getRandom().nextFloat() * 0.8 - 0.4), entity.getY(level.getRandom().nextFloat() * 0.3f), entity.getZ() + (level.getRandom().nextFloat() * 0.8 - 0.4), size + (level.getRandom().nextFloat() * 0.2f - 0.1f));
    }

    public static void addInkSplashParticle(Level level, int color, double x, double y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        if(level instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0F);
        else level.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void addInkSplashParticle(Level level, int color, double x, Vec3 y, double z, float size)
    {
        float[] rgb = hexToRGB(color);
        level.addParticle(new InkSplashParticleData(rgb[0], rgb[1], rgb[2], size), x, y.y, z, 0.0D, 0.0D, 0.0D);
    }
    public static void addInkTerrainParticle(Level level, int color, double x, double y, double z, double dx, double dy, double dz, float maxSpeed)
    {
        if(level instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(new InkTerrainParticleData(color), x, y, z, 1, dx, dy, dz, maxSpeed);
        level.addParticle(new InkTerrainParticleData(color), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void addInkDestroyParticle(Level level, BlockPos pos, int color)
    {
        BlockState state = level.getBlockState(pos);
        VoxelShape voxelshape = state.getShape(level, pos);

        if(voxelshape.isEmpty())
            voxelshape = Shapes.block();

        double d0 = 0.25D;
        voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
            double d1 = Math.min(1.0D, p_172276_ - p_172273_);
            double d2 = Math.min(1.0D, p_172277_ - p_172274_);
            double d3 = Math.min(1.0D, p_172278_ - p_172275_);
            int i = Math.max(2, Mth.ceil(d1 / 0.25D));
            int j = Math.max(2, Mth.ceil(d2 / 0.25D));
            int k = Math.max(2, Mth.ceil(d3 / 0.25D));

            for(int l = 0; l < i; ++l) {
                for(int i1 = 0; i1 < j; ++i1) {
                    for(int j1 = 0; j1 < k; ++j1) {
                        double d4 = ((double)l + 0.5D) / (double)i;
                        double d5 = ((double)i1 + 0.5D) / (double)j;
                        double d6 = ((double)j1 + 0.5D) / (double)k;
                        double d7 = d4 * d1 + p_172273_;
                        double d8 = d5 * d2 + p_172274_;
                        double d9 = d6 * d3 + p_172275_;


                        addInkTerrainParticle(level, color, (double)pos.getX() + d7, (double)pos.getY() + d8, (double)pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, 1);
                        //this.add(new TerrainParticle(this.level, (double)p_107356_.getX() + d7, (double)p_107356_.getY() + d8, (double)p_107356_.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, p_107357_, p_107356_).updateSprite(p_107357_, p_107356_));
                    }
                }
            }

        });

    }
}
