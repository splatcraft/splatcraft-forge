package net.splatcraft.forge.tileentities;

import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public class InkColorTileEntity extends TileEntity
{

    private int color = ColorUtils.DEFAULT;

    public InkColorTileEntity()
    {
        super(SplatcraftTileEntitites.colorTileEntity);
    }

    public InkColorTileEntity(TileEntityType type)
    {
        super(type);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putInt("Color", color);
        return super.save(nbt);
    }

    //Nbt Read
    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        color = ColorUtils.getColorFromNbt(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag()
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
