package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpawnPadTileEntity extends InkColorTileEntity
{
	private UUID spawnShieldUuid;

	public SpawnPadTileEntity(BlockPos pos, BlockState state)
	{
		super(SplatcraftTileEntities.spawnPadTileEntity.get(), pos, state);
	}

	public boolean isSpawnShield(SpawnShieldEntity otherShield)
	{
		return otherShield != null && spawnShieldUuid.equals(otherShield.getUUID());
	}

	public SpawnShieldEntity getSpawnShield()
	{
		if(level.isClientSide() || spawnShieldUuid == null)
			return null;

		Entity res = ((ServerLevel) level).getEntity(spawnShieldUuid);
			return (res instanceof SpawnShieldEntity) ? (SpawnShieldEntity) res : null;
	}

	public void setSpawnShield(SpawnShieldEntity shield)
	{
		if(shield == null)
			spawnShieldUuid = null;
		else spawnShieldUuid = shield.getUUID();

	}

	@Override
	public @NotNull void saveAdditional(CompoundTag nbt)
	{
		if(spawnShieldUuid != null)
			nbt.putUUID("SpawnShield", spawnShieldUuid);
		super.saveAdditional(nbt);
	}

	@Override
	public void load(@NotNull CompoundTag nbt)
	{
		super.load(nbt);

		if(nbt.hasUUID("SpawnShield"))
			spawnShieldUuid = nbt.getUUID("SpawnShield");
	}
}
