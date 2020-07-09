package com.cibernet.splatcraft.capabilities;

import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerColorCapability implements ICapabilitySerializable<IntNBT>
{
	@CapabilityInject(IPlayerColor.class)
	public static final Capability<IPlayerColor> CAPABILITY = null;
	private LazyOptional<IPlayerColor> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IPlayerColor.class, new PlayerColorStorage(), PlayerColor::new);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		return CAPABILITY.orEmpty(cap, instance);
	}
	
	@Override
	public IntNBT serializeNBT()
	{
		return (IntNBT) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
	}
	
	@Override
	public void deserializeNBT(IntNBT nbt)
	{
		CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
	}
}
