package net.splatcraft.forge.registries;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.util.ClientUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeMap;

public class SplatcraftGameRules
{
    public static final TreeMap<Integer, Boolean> booleanRules = new TreeMap<>();
    public static final TreeMap<Integer, Integer> intRules = new TreeMap<>();
    public static final ArrayList<GameRules.RuleKey<?>> ruleList = new ArrayList<>();

    public static GameRules.RuleKey<GameRules.BooleanValue> INK_DECAY;
    public static GameRules.RuleKey<GameRules.BooleanValue> COLORED_PLAYER_NAMES;
    public static GameRules.RuleKey<GameRules.BooleanValue> KEEP_MATCH_ITEMS;
    public static GameRules.RuleKey<GameRules.BooleanValue> UNIVERSAL_INK;
    public static GameRules.RuleKey<GameRules.BooleanValue> DROP_CRATE_LOOT;
    public static GameRules.RuleKey<GameRules.BooleanValue> WATER_DAMAGE;
    public static GameRules.RuleKey<GameRules.BooleanValue> REQUIRE_INK_TANK;
    public static GameRules.RuleKey<GameRules.IntegerValue> INK_MOB_DAMAGE_PERCENTAGE;
    public static GameRules.RuleKey<GameRules.BooleanValue> INK_FRIENDLY_FIRE;
    public static GameRules.RuleKey<GameRules.BooleanValue> INK_HEALING;
    public static GameRules.RuleKey<GameRules.BooleanValue> INK_HEALING_CONSUMES_HUNGER;
    public static GameRules.RuleKey<GameRules.BooleanValue> INK_DAMAGE_COOLDOWN;
    public static GameRules.RuleKey<GameRules.BooleanValue> INFINITE_INK_IN_CREATIVE;
    public static GameRules.RuleKey<GameRules.BooleanValue> INKABLE_GROUND;
    public static GameRules.RuleKey<GameRules.BooleanValue> RECHARGEABLE_INK_TANK;

    public static void registerGamerules()
    {
        INK_DECAY = createBooleanRule("inkDecay", Category.UPDATES, true);
        COLORED_PLAYER_NAMES = createBooleanRule("coloredPlayerNames", Category.PLAYER, false);
        KEEP_MATCH_ITEMS = createBooleanRule("keepMatchItems", Category.PLAYER, false);
        UNIVERSAL_INK = createBooleanRule("universalInk", Category.PLAYER, false);
        DROP_CRATE_LOOT = createBooleanRule("dropCrateLoot", Category.DROPS, false);
        WATER_DAMAGE = createBooleanRule("waterDamage", Category.PLAYER, false);
        REQUIRE_INK_TANK = createBooleanRule("requireInkTank", Category.PLAYER, true);
        INK_FRIENDLY_FIRE = createBooleanRule("inkFriendlyFire", Category.PLAYER, false);
        INK_HEALING = createBooleanRule("inkHealing", Category.PLAYER, true);
        INK_HEALING_CONSUMES_HUNGER = createBooleanRule("inkHealingConsumesHunger", Category.PLAYER, true);
        INK_DAMAGE_COOLDOWN = createBooleanRule("inkDamageCooldown", Category.PLAYER, false);
        INK_MOB_DAMAGE_PERCENTAGE = createIntRule("inkMobDamagePercentage", Category.MOBS, 70);
        INFINITE_INK_IN_CREATIVE = createBooleanRule("infiniteInkInCreative", Category.PLAYER, true);
        INKABLE_GROUND = createBooleanRule("inkableGround", Category.PLAYER, true);
        RECHARGEABLE_INK_TANK = createBooleanRule("rechargeableInkTank", Category.PLAYER, true);
    }

    public static boolean getLocalizedRule(World level, BlockPos pos, GameRules.RuleKey<GameRules.BooleanValue> rule)
    {
        ArrayList<Stage> stages = new ArrayList<>(level.isClientSide ? ClientUtils.clientStages.values() : SaveInfoCapability.get(level.getServer()).getStages().values());

        Stage localStage = null;

        for(Object obj : stages.stream().filter(stage -> stage.dimID.equals(level.dimension().location()) && new AxisAlignedBB(stage.cornerA, stage.cornerB).expandTowards(1, 1, 1).contains(pos.getX(), pos.getY(), pos.getZ())).toArray())
        {
            Stage stage = (Stage) obj;
            if(localStage == null ||
                    Math.abs(stage.cornerA.getX()-stage.cornerB.getX()) < Math.abs(localStage.cornerA.getX()-localStage.cornerB.getX()) ||
                    Math.abs(stage.cornerA.getY()-stage.cornerB.getY()) < Math.abs(localStage.cornerA.getY()-localStage.cornerB.getY()) ||
                    Math.abs(stage.cornerA.getZ()-stage.cornerB.getZ()) < Math.abs(localStage.cornerA.getZ()-localStage.cornerB.getZ()))
                localStage = stage;
        }

        if(localStage != null && localStage.hasSetting(rule))
            return localStage.getSetting(rule);

        return getBooleanRuleValue(level, rule);
    }

    public static GameRules.RuleKey<GameRules.BooleanValue> createBooleanRule(String name, GameRules.Category category, boolean defaultValue)
    {
        Method booleanValueCreate = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "func_223568_b", boolean.class);
        booleanValueCreate.setAccessible(true);

        try
        {
            Object booleanValue = booleanValueCreate.invoke(GameRules.BooleanValue.class, defaultValue);
            GameRules.RuleKey<GameRules.BooleanValue> ruleKey = GameRules.register(Splatcraft.MODID + "." + name, category, (GameRules.RuleType<GameRules.BooleanValue>) booleanValue);
            ruleList.add(ruleKey);
            booleanRules.put(getRuleIndex(ruleKey), defaultValue);
            return ruleKey;

        } catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static GameRules.RuleKey<GameRules.IntegerValue> createIntRule(String name, GameRules.Category category, int defaultValue)
    {
        Method intValueCreate = ObfuscationReflectionHelper.findMethod(GameRules.IntegerValue.class, "func_223559_b", int.class);
        intValueCreate.setAccessible(true);

        try
        {
            Object intValue = intValueCreate.invoke(GameRules.IntegerValue.class, defaultValue);
            GameRules.RuleKey<GameRules.IntegerValue> ruleKey = GameRules.register(Splatcraft.MODID + "." + name, category, (GameRules.RuleType<GameRules.IntegerValue>) intValue);

            ruleList.add(ruleKey);
            intRules.put(getRuleIndex(ruleKey), defaultValue);
            return ruleKey;

        } catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRuleIndex(GameRules.RuleKey<?> rule)
    {
        return ruleList.indexOf(rule);
    }

    @SuppressWarnings("rawtypes")
    public static GameRules.RuleKey getRuleFromIndex(int index)
    {
        return ruleList.get(index);
    }

    public static boolean getBooleanRuleValue(World level, GameRules.RuleKey<GameRules.BooleanValue> rule)
    {
        return level.isClientSide ? getClientsideBooleanValue(rule) : level.getGameRules().getBoolean(rule);
    }

    public static int getIntRuleValue(World level, GameRules.RuleKey<GameRules.IntegerValue> rule)
    {
        return level.isClientSide ? getClientsideIntValue(rule) : level.getGameRules().getInt(rule);
    }

    public static boolean getClientsideBooleanValue(GameRules.RuleKey<GameRules.BooleanValue> rule)
    {
        return booleanRules.get(getRuleIndex(rule));
    }

    public static int getClientsideIntValue(GameRules.RuleKey<GameRules.IntegerValue> rule)
    {
        return intRules.get(getRuleIndex(rule));
    }
}
