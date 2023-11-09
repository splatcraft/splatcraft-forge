package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.blocks.InkVatBlock;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.ColorUtils;
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

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }
    //Used to port Inked Blocks to World Ink system
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T te)
    {
        if (!level.isClientSide && te instanceof InkedBlockTileEntity inkedBlock)
        {
            if(inkedBlock.hasSavedState())
            {
                level.setBlock(pos, inkedBlock.savedState, 2);
                if(inkedBlock.hasPermanentColor())
                    WorldInkCapability.get(level, pos).setPermanentInk(pos, inkedBlock.getPermanentColor(), inkedBlock.getPermanentInkType());
                InkBlockUtils.inkBlock(level, pos, inkedBlock.getColor(), 0, getInkType(state));
            }
        }
    }

    @Deprecated //Only used for parity purposes
    public static InkBlockUtils.InkType getInkType(BlockState state)
    {
        if(state.is(SplatcraftBlocks.clearInkedBlock.get()))
            return InkBlockUtils.InkType.CLEAR;
        if(state.is(SplatcraftBlocks.glowingInkedBlock.get()))
            return InkBlockUtils.InkType.GLOWING;
        return InkBlockUtils.InkType.NORMAL;
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
