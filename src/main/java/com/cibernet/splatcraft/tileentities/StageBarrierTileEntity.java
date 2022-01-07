package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.SplatcraftConfig;
import com.cibernet.splatcraft.blocks.StageBarrierBlock;
import com.cibernet.splatcraft.data.SplatcraftTags;
import com.cibernet.splatcraft.registries.SplatcraftTileEntitites;
import com.cibernet.splatcraft.util.ClientUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;

import static com.cibernet.splatcraft.util.InkDamageUtils.VOID_DAMAGE;

public class StageBarrierTileEntity extends TileEntity implements ITickableTileEntity
{
    public final int maxActiveTime = 20;
    protected int activeTime = maxActiveTime;


    public StageBarrierTileEntity()
    {
        super(SplatcraftTileEntitites.stageBarrierTileEntity);
    }

    public StageBarrierTileEntity(TileEntityType<? extends StageBarrierTileEntity> type)
    {
        super(type);
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
            resetActiveTime();
            if (getBlockState().getBlock() instanceof StageBarrierBlock && ((StageBarrierBlock) getBlockState().getBlock()).damagesPlayer &&
                    entity instanceof PlayerEntity)
            {
                entity.hurt(VOID_DAMAGE, Float.MAX_VALUE);
            }

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

    @Override
    public double getViewDistance() {
        return SplatcraftConfig.Client.barrierRenderDistance.get();
    }

    protected void resetActiveTime()
    {
        activeTime = maxActiveTime;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);

        if (nbt.contains("ActiveTime"))
        {
            activeTime = nbt.getInt("ActiveTime");
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        compound.putInt("ActiveTime", activeTime);
        return super.save(compound);
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


    public float getMaxActiveTime()
    {
        return maxActiveTime;
    }

    public float getActiveTime()
    {
        return activeTime;
    }
}
