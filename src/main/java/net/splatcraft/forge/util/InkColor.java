package net.splatcraft.forge.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.splatcraft.forge.Splatcraft;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.TreeMap;

public class InkColor implements Comparable<InkColor>
{
    private static final TreeMap<Integer, InkColor> colorMap = new TreeMap<>();
    private static int idIndex = 0;
    private final int hexCode;
    private final String name;
    private final DyeColor dyeColor;
    private final int ID;

    public InkColor(String name, int color, @Nullable DyeColor dyeColor)
    {
        hexCode = color;
        this.name = name;
        this.dyeColor = dyeColor;

        ID = idIndex++;
        colorMap.put(color, this);
        //setRegistryName(name);
    }

    public InkColor(String name, int color)
    {
        this(name, color, null);
    }

    public static InkColor getByHex(int hexCode)
    {
        return colorMap.get(hexCode);
    }

    public String getLocalizedName()
    {
        return I18n.get(getUnlocalizedName());
    }

    public String getUnlocalizedName()
    {
        return "ink_color." + name;
    }

    public String getHexCode()
    {
        return String.format("%06X", hexCode);
    }

    public int getColor()
    {
        return hexCode;
    }

    public @Nullable
    DyeColor getDyeColor()
    {
        return dyeColor;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name + ": #" + getHexCode().toUpperCase();
    }

    @Override
    public int compareTo(InkColor other)
    {
        return ID - other.ID;
    }

    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Splatcraft.MODID, name);
    }

    public static class DummyType extends InkColor
    {
        public DummyType()
        {
            super("dummy", ColorUtils.DEFAULT);
        }
    }
}
