package net.splatcraft.forge.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.blocks.ColoredBarrierBlock;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.entities.SpawnShieldEntity;
import net.splatcraft.forge.registries.SplatcraftTileEntities;
import net.splatcraft.forge.util.ClientUtils;
import net.splatcraft.forge.util.ColorUtils;

public class ColoredBarrierTileEntity extends StageBarrierTileEntity implements IHasTeam
{
    protected int color = ColorUtils.DEFAULT;
    private boolean inverted = false;
    private String team = "";

    public ColoredBarrierTileEntity(BlockPos pos, BlockState state)
    {
        super(SplatcraftTileEntities.colorBarrierTileEntity.get(), pos, state);
    }

    @Override
    public void tick()
    {
        if (activeTime > 0)
        {
            activeTime--;
        }

        for (Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(getBlockPos()).inflate(0.05)))
        {
            if(entity instanceof SpawnShieldEntity)
                continue;

            if(ColorUtils.getEntityColor(entity) > -1 && (getBlockState().getBlock() instanceof ColoredBarrierBlock &&
                    !((ColoredBarrierBlock) getBlockState().getBlock()).canAllowThrough(getBlockPos(), entity)))
                    resetActiveTime();
        }

        if (level.isClientSide && ClientUtils.getClientPlayer().isCreative())
        {
            boolean canRender = true;
            Player player = ClientUtils.getClientPlayer();
            int renderDistance = SplatcraftConfig.Client.barrierRenderDistance.get();

            if(player.distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()) > renderDistance*renderDistance)
                canRender = false;
            else if (SplatcraftConfig.Client.holdBarrierToRender.get())
            {
                canRender = player.getMainHandItem().is(SplatcraftTags.Items.REVEALS_BARRIERS) ||
                        player.getMainHandItem().is(SplatcraftTags.Items.REVEALS_BARRIERS);
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
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        setColor(ColorUtils.getColorFromNbt(nbt));
        setTeam(nbt.getString("Team"));
        setInverted(nbt.getBoolean("Inverted"));
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        compound.putInt("Color", getColor());
        compound.putString("Team", getTeam());
        compound.putBoolean("Inverted", inverted);
        super.saveAdditional(compound);
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    @Override
    public String getTeam() {
        return team;
    }

    @Override
    public void setTeam(String team) {
        this.team = team;
    }
}
