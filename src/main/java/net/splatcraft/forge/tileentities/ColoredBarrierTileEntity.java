package net.splatcraft.forge.tileentities;

import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.registries.SplatcraftTileEntitites;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;

public class ColoredBarrierTileEntity extends StageBarrierTileEntity
{
    protected int color = ColorUtils.DEFAULT;

    public ColoredBarrierTileEntity()
    {
        super(SplatcraftTileEntitites.colorBarrierTileEntity);
    }

    @Override
    public void tick()
    {
        if (activeTime > 0)
        {
            activeTime--;
        }

        for (Entity entity : level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(getBlockPos()).inflate(0.05)))
        {

            if(ColorUtils.getEntityColor(entity) > -1 && (getBlockState().getBlock() instanceof ColoredBarrierBlock &&
                    !((ColoredBarrierBlock) getBlockState().getBlock()).canAllowThrough(getBlockPos(), entity)))
            resetActiveTime();
        }

        if (level.isClientSide && ClientUtils.getClientPlayer().isCreative())
        {
            boolean canRender = true;
            PlayerEntity player = ClientUtils.getClientPlayer();
            int renderDistance = SplatcraftConfig.Client.barrierRenderDistance.get();

            if(player.distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()) > renderDistance*renderDistance)
                canRender = false;
            else if (SplatcraftConfig.Client.holdBarrierToRender.get())
            {
                canRender = player.getMainHandItem().getItem().is(SplatcraftTags.Items.REVEALS_BARRIERS) ||
                        player.getMainHandItem().getItem().is(SplatcraftTags.Items.REVEALS_BARRIERS);
            }
            if (canRender)
                resetActiveTime();
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

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);
        setColor(ColorUtils.getColorFromNbt(nbt));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        compound.putInt("Color", getColor());
        return super.save(compound);
    }
}
