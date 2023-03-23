package net.splatcraft.forge.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpawnPadTileEntity extends InkColorTileEntity
{
	private UUID spawnShieldUuid;

	public SpawnPadTileEntity()
	{
		super(SplatcraftTileEntities.spawnPadTileEntity);
	}

	public boolean isSpawnShield(SpawnShieldEntity otherShield)
	{
		return otherShield != null && spawnShieldUuid.equals(otherShield.getUUID());
	}

	public SpawnShieldEntity getSpawnShield()
	{
		if(level.isClientSide() || spawnShieldUuid == null)
			return null;

		Entity res = ((ServerWorld) level).getEntity(spawnShieldUuid);
			return (res instanceof SpawnShieldEntity) ? (SpawnShieldEntity) res : null;
	}

	public void setSpawnShield(SpawnShieldEntity shield)
	{
		if(shield == null)
			spawnShieldUuid = null;
		else spawnShieldUuid = shield.getUUID();

	}

	@Override
	public @NotNull CompoundNBT save(CompoundNBT nbt)
	{
		if(spawnShieldUuid != null)
			nbt.putUUID("SpawnShield", spawnShieldUuid);
		return super.save(nbt);
	}

	@Override
	public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt)
	{
		super.load(state, nbt);

		if(nbt.hasUUID("SpawnShield"))
			spawnShieldUuid = nbt.getUUID("SpawnShield");
	}
}
