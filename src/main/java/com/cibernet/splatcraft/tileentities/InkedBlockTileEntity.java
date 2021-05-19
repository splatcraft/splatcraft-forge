package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.InkBlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;

public class InkedBlockTileEntity extends InkColorTileEntity
{
    private BlockState savedState = Blocks.AIR.getDefaultState();
    private int savedColor = -1;
    private int permanentColor = -1;
    private InkBlockUtils.InkType permanentInkType = InkBlockUtils.InkType.NORMAL;

    public InkedBlockTileEntity()
    {
        super(SplatcraftTileEntitites.inkedTileEntity);
    }

    

    //Read NBT
    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        savedState = NBTUtil.readBlockState(nbt.getCompound("SavedState"));
        savedColor = nbt.getInt("SavedColor");

        if(nbt.contains("PermanentColor"))
        {
            setPermanentColor(nbt.getInt("PermanentColor"));
            setPermanentInkType(InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("PermanentInkType")), InkBlockUtils.InkType.NORMAL));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("SavedState", NBTUtil.writeBlockState(savedState));
        if(hasSavedColor())
            nbt.putInt("SavedColor", savedColor);
        if(hasPermanentColor())
        {
            nbt.putInt("PermanentColor", permanentColor);
            nbt.putString("PermanentInkType", permanentInkType.getString());
        }
        return super.write(nbt);
    }

    public BlockState getSavedState()
    {
        return savedState;
    }

    public void setSavedState(BlockState savedState)
    {
        this.savedState = savedState;
    }

    public boolean hasSavedState()
    {
        return savedState != null && savedState.getBlock() != Blocks.AIR;
    }

    public int getSavedColor()
    {
        return savedColor;
    }
    public void setSavedColor(int color)
    {
        this.savedColor = color;
    }
    public boolean hasSavedColor()
    {
        return savedColor != -1;
    }

    public int getPermanentColor() {return permanentColor;}
    public void setPermanentColor(int permanentColor) { this.permanentColor = permanentColor; }
    public boolean hasPermanentColor() { return permanentColor != -1; }

    public InkBlockUtils.InkType getPermanentInkType() { return permanentInkType; }
    public void setPermanentInkType(InkBlockUtils.InkType permanentInkType) { this.permanentInkType = permanentInkType; }
}
