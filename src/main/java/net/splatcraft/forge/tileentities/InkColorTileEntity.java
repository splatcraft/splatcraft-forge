package net.splatcraft.forge.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InkColorTileEntity extends TileEntity
{

    private int color = ColorUtils.DEFAULT;

    public InkColorTileEntity()
    {
        super(SplatcraftTileEntities.colorTileEntity);
    }

    public InkColorTileEntity(TileEntityType type)
    {
        super(type);
    }

    @Override
    public @NotNull CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putInt("Color", color);
        return super.save(nbt);
    }

    //Nbt Read
    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt)
    {
        super.load(state, nbt);
        color = ColorUtils.getColorFromNbt(nbt);
    }

    @Override
    public @NotNull CompoundNBT getUpdateTag()
    {
        return this.save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
    {
        this.load(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(getBlockPos(), 2, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        if (level != null)
        {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 2);
            handleUpdateTag(state, pkt.getTag());
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

}
