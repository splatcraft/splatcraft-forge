package net.splatcraft.forge.handlers;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkColor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ScoreboardHandler
{
    public static final ObjectiveCriteria COLOR = createObjectiveCriteria(Splatcraft.MODID + ".inkColor");
    public static final ObjectiveCriteria TURF_WAR_SCORE = createObjectiveCriteria(Splatcraft.MODID + ".turfWarScore");

    protected static final Map<Integer, CriteriaInkColor[]> COLOR_CRITERIA = Maps.newHashMap();

    protected static ObjectiveCriteria createObjectiveCriteria(String id)
    {
        try {
            return ObfuscationReflectionHelper.findConstructor(ObjectiveCriteria.class, String.class).newInstance(id);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updatePlayerScore(ObjectiveCriteria criteria, Player player, int color)
    {
        player.getScoreboard().forAllObjectives(criteria, player.getScoreboardName(), p_195397_1_ -> p_195397_1_.setScore(color));
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

    public static class CriteriaInkColor extends ObjectiveCriteria
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
            //TODO make sure this works
            @NotNull Map<String, ObjectiveCriteria> CRITERIA_BY_NAME;
            try {
                CRITERIA_BY_NAME = (Map<String, ObjectiveCriteria>) ObfuscationReflectionHelper.findField(ObjectiveCriteria.class, "f_166108_").get(null);
            } catch (IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
                return;
            }

            CRITERIA_BY_NAME.remove(name);
        }
    }
}
