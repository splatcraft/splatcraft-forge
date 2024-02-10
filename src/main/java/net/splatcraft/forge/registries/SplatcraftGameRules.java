package net.splatcraft.forge.registries;

import java.util.ArrayList;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.data.capabilities.saveinfo.SaveInfoCapability;
import net.splatcraft.forge.util.ClientUtils;
import org.lwjgl.system.CallbackI;

public class SplatcraftGameRules {
    public static final TreeMap<Integer, Boolean> booleanRules = new TreeMap<>();
    public static final TreeMap<Integer, Integer> intRules = new TreeMap<>();
    public static final ArrayList<GameRules.Key<?>> ruleList = new ArrayList<>();

    public static GameRules.Key<GameRules.BooleanValue> INK_DECAY;
    public static GameRules.Key<GameRules.IntegerValue> INK_DECAY_RATE;
    public static GameRules.Key<GameRules.BooleanValue> INKABLE_GROUND;
    public static GameRules.Key<GameRules.BooleanValue> INK_DESTROYS_FOLIAGE;
    public static GameRules.Key<GameRules.BooleanValue> COLORED_PLAYER_NAMES;
    public static GameRules.Key<GameRules.BooleanValue> KEEP_MATCH_ITEMS;
    public static GameRules.Key<GameRules.BooleanValue> UNIVERSAL_INK;
    public static GameRules.Key<GameRules.BooleanValue> DROP_CRATE_LOOT;
    public static GameRules.Key<GameRules.BooleanValue> WATER_DAMAGE;
    public static GameRules.Key<GameRules.BooleanValue> REQUIRE_INK_TANK;
    public static GameRules.Key<GameRules.IntegerValue> INK_MOB_DAMAGE_PERCENTAGE;
    public static GameRules.Key<GameRules.BooleanValue> INK_FRIENDLY_FIRE;
    public static GameRules.Key<GameRules.BooleanValue> INK_HEALING;
    public static GameRules.Key<GameRules.BooleanValue> INK_HEALING_CONSUMES_HUNGER;
    public static GameRules.Key<GameRules.BooleanValue> INK_DAMAGE_COOLDOWN;
    public static GameRules.Key<GameRules.BooleanValue> INFINITE_INK_IN_CREATIVE;
    public static GameRules.Key<GameRules.BooleanValue> RECHARGEABLE_INK_TANK;
    public static GameRules.Key<GameRules.BooleanValue> GLOBAL_SUPERJUMPING;
    public static GameRules.Key<GameRules.IntegerValue> SUPERJUMP_DISTANCE_LIMIT;

    public static void registerGamerules() {
        INK_DECAY = createBooleanRule("inkDecay", GameRules.Category.UPDATES, true);
        INK_DECAY_RATE = createIntRule("inkDecayRate", GameRules.Category.UPDATES, 3);
        COLORED_PLAYER_NAMES = createBooleanRule("coloredPlayerNames", GameRules.Category.PLAYER, false);
        KEEP_MATCH_ITEMS = createBooleanRule("keepMatchItems", GameRules.Category.PLAYER, false);
        UNIVERSAL_INK = createBooleanRule("universalInk", GameRules.Category.PLAYER, false);
        DROP_CRATE_LOOT = createBooleanRule("dropCrateLoot", GameRules.Category.DROPS, false);
        WATER_DAMAGE = createBooleanRule("waterDamage", GameRules.Category.PLAYER, false);
        REQUIRE_INK_TANK = createBooleanRule("requireInkTank", GameRules.Category.PLAYER, true);
        INK_FRIENDLY_FIRE = createBooleanRule("inkFriendlyFire", GameRules.Category.PLAYER, false);
        INK_HEALING = createBooleanRule("inkHealing", GameRules.Category.PLAYER, true);
        INK_HEALING_CONSUMES_HUNGER = createBooleanRule("inkHealingConsumesHunger", GameRules.Category.PLAYER, true);
        INK_DAMAGE_COOLDOWN = createBooleanRule("inkDamageCooldown", GameRules.Category.PLAYER, false);
        GLOBAL_SUPERJUMPING = createBooleanRule("globalSuperJumping", GameRules.Category.PLAYER, true);
        SUPERJUMP_DISTANCE_LIMIT = createIntRule("superJumpDistanceLimit", GameRules.Category.PLAYER, 1000);
        INK_MOB_DAMAGE_PERCENTAGE = createIntRule("inkMobDamagePercentage", GameRules.Category.MOBS, 70);
        INFINITE_INK_IN_CREATIVE = createBooleanRule("infiniteInkInCreative", GameRules.Category.PLAYER, true);
        INKABLE_GROUND = createBooleanRule("inkableGround", GameRules.Category.MISC, true);
        INK_DESTROYS_FOLIAGE = createBooleanRule("inkDestroysFoliage", GameRules.Category.MISC, true);
        RECHARGEABLE_INK_TANK = createBooleanRule("rechargeableInkTank", GameRules.Category.PLAYER, true);
    }

    public static boolean getLocalizedRule(Level level, BlockPos pos, GameRules.Key<GameRules.BooleanValue> rule) {
        ArrayList<Stage> stages = Stage.getStagesForPosition(level, new Vec3(pos.getX(), pos.getY(), pos.getZ()));

        Stage localStage = null;
        AABB localStageBounds = null;


        for (Stage stage : stages)
        {
            AABB stageBounds = stage.getBounds();

            if (localStage == null || stageBounds.getSize() < localStageBounds.getSize())
            {
                localStage = stage;
                localStageBounds = stage.getBounds();
            }
        }

        if (localStage != null && localStage.hasSetting(rule))
            return localStage.getSetting(rule);

        return getBooleanRuleValue(level, rule);
    }

    public static GameRules.Key<GameRules.BooleanValue> createBooleanRule(String name, GameRules.Category category, boolean defaultValue) {
        GameRules.Type<GameRules.BooleanValue> booleanValue = GameRules.BooleanValue.create(defaultValue);
        GameRules.Key<GameRules.BooleanValue> ruleKey = GameRules.register(Splatcraft.MODID + "." + name, category, booleanValue);
        ruleList.add(ruleKey);
        booleanRules.put(getRuleIndex(ruleKey), defaultValue);
        return ruleKey;
    }

    public static GameRules.Key<GameRules.IntegerValue> createIntRule(String name, GameRules.Category category, int defaultValue) {
        GameRules.Type<GameRules.IntegerValue> intValue = GameRules.IntegerValue.create(defaultValue);
        GameRules.Key<GameRules.IntegerValue> ruleKey = GameRules.register(Splatcraft.MODID + "." + name, category, intValue);

        ruleList.add(ruleKey);
        intRules.put(getRuleIndex(ruleKey), defaultValue);
        return ruleKey;
    }

    public static int getRuleIndex(GameRules.Key<?> rule) {
        return ruleList.indexOf(rule);
    }

    public static <T extends GameRules.Value<T>> GameRules.Key<T> getRuleFromIndex(int index) {
        return (GameRules.Key<T>) ruleList.get(index);
    }

    public static boolean getBooleanRuleValue(Level level, GameRules.Key<GameRules.BooleanValue> rule) {
        return level.isClientSide ? getClientsideBooleanValue(rule) : level.getGameRules().getBoolean(rule);
    }

    public static int getIntRuleValue(Level level, GameRules.Key<GameRules.IntegerValue> rule) {
        return level.isClientSide ? getClientsideIntValue(rule) : level.getGameRules().getInt(rule);
    }

    public static boolean getClientsideBooleanValue(GameRules.Key<GameRules.BooleanValue> rule) {
        return booleanRules.get(getRuleIndex(rule));
    }

    public static int getClientsideIntValue(GameRules.Key<GameRules.IntegerValue> rule) {
        return intRules.get(getRuleIndex(rule));
    }
}
