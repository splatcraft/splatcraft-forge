package net.splatcraft.forge.registries;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.splatcraft.forge.Splatcraft.MODID;

public class SplatcraftAttributes
{
	protected static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

	public static final RegistryObject<Attribute> inkSwimSpeed = REGISTRY.register("ink_swim_speed", () -> new RangedAttribute("attribute.splatcraft.ink_swim_speed", 0.075F, 0.0D, 1024.0D).setSyncable(true));
	public static final RegistryObject<Attribute> superJumpTravelTime = REGISTRY.register("super_jump_travel_time", () -> new RangedAttribute("attribute.splatcraft.super_jump_travel_time",  73, 0.0D, 1200.0D).setSyncable(true));
	public static final RegistryObject<Attribute> superJumpWindupTime = REGISTRY.register("super_jump_windup_time", () -> new RangedAttribute("attribute.splatcraft.super_jump_windup_time",  27, 0.0D, 1200.0D).setSyncable(true));
	public static final RegistryObject<Attribute> superJumpHeight = REGISTRY.register("super_jump_height", () -> new RangedAttribute("attribute.splatcraft.super_jump_height",  50, -256.0D, 256.0D).setSyncable(true));

}
