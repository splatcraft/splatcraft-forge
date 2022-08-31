package net.splatcraft.forge.handlers;

import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkColor;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreCriteria;

import java.util.*;

public class ScoreboardHandler
{
    public static final ScoreCriteria COLOR = new ScoreCriteria(Splatcraft.MODID + ".inkColor");
    public static final ScoreCriteria INK = new ScoreCriteria(Splatcraft.MODID + ".inkUnits");
    protected static final Map<Integer, CriteriaInkColor[]> COLOR_CRITERIA = Maps.newHashMap();

    public static void updatePlayerColorScore(PlayerEntity player, int color)
    {
        player.getScoreboard().forAllObjectives(COLOR, player.getScoreboardName(), p_195397_1_ -> p_195397_1_.setScore(color));
    }

    public static void createColorCriterion(int color)
    {
        COLOR_CRITERIA.put(color, new CriteriaInkColor[]
                {
                        new CriteriaInkColor("colorKills", color),
                        new CriteriaInkColor("deathsAsColor", color),
                        new CriteriaInkColor("killsAsColor", color),
                        new CriteriaInkColor("winsAsColor", color),
                        new CriteriaInkColor("lossesAsColor", color),
                });
    }

    public static void clearColorCriteria()
    {
        for (int color : COLOR_CRITERIA.keySet())
        {
            for (CriteriaInkColor c : COLOR_CRITERIA.get(color))
            {
                c.remove();
            }

        }
        COLOR_CRITERIA.clear();
    }

    public static void removeColorCriterion(int color)
    {
        if (hasColorCriterion(color))
        {
            for (CriteriaInkColor c : COLOR_CRITERIA.get(color))
            {
                c.remove();
            }
            COLOR_CRITERIA.remove(color);
        }
    }

    public static boolean hasColorCriterion(int color)
    {
        return COLOR_CRITERIA.containsKey(color);
    }

    public static Iterable<String> getCriteriaSuggestions()
    {
        List<String> suggestions = new ArrayList<>();

        COLOR_CRITERIA.keySet().forEach(key ->
        {
            InkColor colorObj = InkColor.getByHex(key);

            if (colorObj != null)
            {
                suggestions.add(Objects.requireNonNull(colorObj.getRegistryName()).toString());
            } else
            {
                suggestions.add(ColorUtils.getColorId(key));
            }
        });

        return suggestions;
    }


    public static Set<Integer> getCriteriaKeySet()
    {
        return COLOR_CRITERIA.keySet();
    }


    public static CriteriaInkColor getColorKills(int color)
    {
        return COLOR_CRITERIA.get(color)[0];
    }

    public static CriteriaInkColor getDeathsAsColor(int color)
    {
        return COLOR_CRITERIA.get(color)[1];
    }

    public static CriteriaInkColor getKillsAsColor(int color)
    {
        return COLOR_CRITERIA.get(color)[2];
    }

    public static CriteriaInkColor getColorWins(int color)
    {
        return COLOR_CRITERIA.get(color)[3];
    }

    public static CriteriaInkColor getColorLosses(int color)
    {
        return COLOR_CRITERIA.get(color)[4];
    }

    public static CriteriaInkColor[] getAllFromColor(int color)
    {
        return COLOR_CRITERIA.get(color);
    }

    public static void register()
    {
    }

    public static String getColorIdentifier(int color)
    {
        return InkColor.getByHex(color) == null ? String.format("%06X", color).toLowerCase() : Objects.requireNonNull(InkColor.getByHex(color).getRegistryName()).getPath();
    }

    public static class CriteriaInkColor extends ScoreCriteria
    {
        private final String name;
        private final int color;

        public CriteriaInkColor(String name, int color)
        {
            super((InkColor.getByHex(color) == null ? Splatcraft.MODID : Objects.requireNonNull(InkColor.getByHex(color).getRegistryName()).getNamespace())
                    + "." + name + "." + getColorIdentifier(color));
            this.name = (InkColor.getByHex(color) == null ? Splatcraft.MODID : Objects.requireNonNull(InkColor.getByHex(color).getRegistryName()).getNamespace())
                    + "." + name + "." + getColorIdentifier(color);
            this.color = color;

        }

        public void remove()
        {
            ScoreCriteria.CRITERIA_BY_NAME.remove(name);
        }
    }
}
