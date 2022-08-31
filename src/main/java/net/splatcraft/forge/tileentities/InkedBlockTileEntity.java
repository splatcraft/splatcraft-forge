package net.splatcraft.forge.tileentities;

import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.util.InkBlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InkedBlockTileEntity extends InkColorTileEntity
{
    private BlockState savedState = Blocks.AIR.defaultBlockState();
    private int savedColor = -1;
    private int permanentColor = -1;
    private InkBlockUtils.InkType permanentInkType = InkBlockUtils.InkType.NORMAL;

    public InkedBlockTileEntity()
    {
        super(SplatcraftTileEntitites.inkedTileEntity);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return Minecraft.getInstance().options.renderDistance*12D; //probably a bad idea
    }



    //Read NBT
    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        savedState = NBTUtil.readBlockState(nbt.getCompound("SavedState"));
        savedColor = nbt.getInt("SavedColor");

        if(nbt.contains("PermanentColor"))
        {
            setPermanentColor(nbt.getInt("PermanentColor"));
            setPermanentInkType(InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("PermanentInkType")), InkBlockUtils.InkType.NORMAL));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.put("SavedState", NBTUtil.writeBlockState(savedState));
        if(hasSavedColor())
            nbt.putInt("SavedColor", savedColor);
        if(hasPermanentColor())
        {
            nbt.putInt("PermanentColor", permanentColor);
            nbt.putString("PermanentInkType", permanentInkType.getSerializedName());
        }
        return super.save(nbt);
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
