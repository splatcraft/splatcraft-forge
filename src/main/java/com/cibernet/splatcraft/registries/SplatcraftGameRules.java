package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class SplatcraftGameRules
{
	public static final TreeMap<Integer, Boolean> booleanRules = new TreeMap<>();
	public static final ArrayList<GameRules.RuleKey> ruleList = new ArrayList<>();
	
	public static GameRules.RuleKey<GameRules.BooleanValue> INK_DECAY;
	public static GameRules.RuleKey<GameRules.BooleanValue> COLORED_NAMEPLATES;
	public static GameRules.RuleKey<GameRules.BooleanValue> UNIVERSAL_INK;
	
	public static void registerGamerules()
	{
		INK_DECAY = createBooleanRule("inkDecay", Category.UPDATES, true);
		COLORED_NAMEPLATES = createBooleanRule("coloredNameplates", Category.PLAYER, false);
		UNIVERSAL_INK = createBooleanRule("universalInk", Category.PLAYER, false);
	}
	
	public static GameRules.RuleKey<GameRules.BooleanValue> createBooleanRule(String name, GameRules.Category category, boolean defaultValue)
	{
			Method booleanValueCreate = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "func_223568_b", boolean.class);
			booleanValueCreate.setAccessible(true);
			
				try
				{
					Object booleanValue = booleanValueCreate.invoke(GameRules.BooleanValue.class, defaultValue);
					GameRules.RuleKey<GameRules.BooleanValue> ruleKey = GameRules.func_234903_a_(Splatcraft.MODID + "." + name, category, (GameRules.RuleType<GameRules.BooleanValue>) booleanValue);
					ruleList.add(ruleKey);
					booleanRules.put(getRuleIndex(ruleKey), defaultValue);
					return ruleKey;
					
				} catch(IllegalAccessException e)
				{
					e.printStackTrace();
				} catch(InvocationTargetException e)
				{
					e.printStackTrace();
				}
		return null;
	}
	
	public static int getRuleIndex(GameRules.RuleKey rule)
	{
		return ruleList.indexOf(rule);
	}
	
	public static GameRules.RuleKey getRuleFromIndex(int index)
	{
		return ruleList.get(index);
	}
	
	public static boolean getBooleanRuleValue(World world, GameRules.RuleKey<GameRules.BooleanValue> rule)
	{
		return world.isRemote ? getClientsideValue(rule) : world.getGameRules().getBoolean(rule);
	}
	
	public static boolean getClientsideValue(GameRules.RuleKey<GameRules.BooleanValue> rule)
	{
		return booleanRules.get(getRuleIndex(rule));
	}
}
