package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.NotNull;

public class InkedBlockTileEntity extends InkColorTileEntity
{
    private BlockState savedState = Blocks.AIR.defaultBlockState();
    private int savedColor = -1;
    private int permanentColor = -1;
    private InkBlockUtils.InkType permanentInkType = InkBlockUtils.InkType.NORMAL;

    public InkedBlockTileEntity(BlockPos pos, BlockState state)
    {
        super(SplatcraftTileEntities.inkedTileEntity.get(), pos, state);
    }

    //Read NBT
    @Override
    public void load(@NotNull CompoundTag nbt)
    {
        super.load(nbt);
        savedState = NbtUtils.readBlockState(nbt.getCompound("SavedState"));
        savedColor = nbt.getInt("SavedColor");

        if(nbt.contains("PermanentColor"))
        {
            setPermanentColor(nbt.getInt("PermanentColor"));
            setPermanentInkType(InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("PermanentInkType")), InkBlockUtils.InkType.NORMAL));
        }
    }

    @Override
    public @NotNull void saveAdditional(CompoundTag nbt)
    {
        nbt.put("SavedState", NbtUtils.writeBlockState(savedState));
        if(hasSavedColor())
            nbt.putInt("SavedColor", savedColor);
        if(hasPermanentColor())
        {
            nbt.putInt("PermanentColor", permanentColor);
            nbt.putString("PermanentInkType", permanentInkType.getSerializedName());
        }
        super.saveAdditional(nbt);
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
