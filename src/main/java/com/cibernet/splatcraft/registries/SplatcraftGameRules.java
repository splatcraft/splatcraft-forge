package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class SplatcraftGameRules
{
	public static GameRules.RuleKey<GameRules.BooleanValue> INK_DECAY;
	public static GameRules.RuleKey<GameRules.BooleanValue> COLORED_NAMEPLATES;
	
	public static void registerGamerules()
	{
		INK_DECAY = createBooleanRule("inkDecay", Category.UPDATES, true);
		COLORED_NAMEPLATES = createBooleanRule("coloredNameplates", Category.PLAYER, true);
	}
	
	public static GameRules.RuleKey<GameRules.BooleanValue> createBooleanRule(String name, GameRules.Category category, boolean defaultValue)
	{
			Method booleanValueCreate = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "func_223568_b", boolean.class);
			booleanValueCreate.setAccessible(true);
			
				try
				{
					Object booleanValue = booleanValueCreate.invoke(GameRules.BooleanValue.class, defaultValue);
					GameRules.RuleKey<GameRules.BooleanValue> ruleKey = GameRules.func_234903_a_(Splatcraft.MODID + "." + name, category, (GameRules.RuleType<GameRules.BooleanValue>) booleanValue);
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
}
