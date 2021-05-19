package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.blocks.ColoredBarrierBlock;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ClientUtils;
import com.cibernet.splatcraft.util.ColorUtils;
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

        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).grow(0.05)))
        {

            if(entity instanceof LivingEntity && ColorUtils.getEntityColor(entity) > -1 && (getBlockState().getBlock() instanceof ColoredBarrierBlock &&
                    ((ColoredBarrierBlock) getBlockState().getBlock()).canAllowThrough(getPos(), entity)))
            resetActiveTime();
        }

        if (world.isRemote && ClientUtils.getClientPlayer().isCreative())
        {
            boolean canRender = true;
            PlayerEntity player = ClientUtils.getClientPlayer();
            int renderDistance = SplatcraftConfig.Client.barrierRenderDistance.get();

            if(player.getDistanceSq(getPos().getX(), getPos().getY(), getPos().getZ()) > renderDistance*renderDistance)
                canRender = false;
            else if (SplatcraftConfig.Client.holdBarrierToRender.get())
            {
                canRender = player.getHeldItemMainhand().getItem().isIn(SplatcraftTags.Items.REVEALS_BARRIERS) ||
                        player.getHeldItemMainhand().getItem().isIn(SplatcraftTags.Items.REVEALS_BARRIERS);
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
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        setColor(ColorUtils.getColorFromNbt(nbt));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        compound.putInt("Color", getColor());
        return super.write(compound);
    }
}
