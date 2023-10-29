package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;

public class InkColorTileEntity extends BlockEntity implements IHasTeam
{

	private int color = ColorUtils.DEFAULT;
	private boolean inverted = false;
	private String team = "";

	public InkColorTileEntity(BlockPos pos, BlockState state)
	{
		super(SplatcraftTileEntities.colorTileEntity.get(), pos, state);
	}

	public InkColorTileEntity(BlockEntityType type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}


	@Override
	public void saveAdditional(CompoundTag nbt)
	{
		nbt.putBoolean("Inverted", inverted);
		nbt.putInt("Color", color);
		if(!team.isEmpty())
			nbt.putString("Team", team);
		super.saveAdditional(nbt);
	}

	//Nbt Read
	@Override
	public void load(@NotNull CompoundTag nbt)
	{
		super.load(nbt);
		color = ColorUtils.getColorFromNbt(nbt);
		team = nbt.getString("Team");
		inverted = nbt.getBoolean("Inverted");
	}

	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		this.load(tag);
	}



	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		// Will get tag from #getUpdateTag
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
	{
		if (level != null)
		{
			BlockState state = level.getBlockState(getBlockPos());
			level.sendBlockUpdated(getBlockPos(), state, state, 2);
			handleUpdateTag(pkt.getTag());
		}
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
}
