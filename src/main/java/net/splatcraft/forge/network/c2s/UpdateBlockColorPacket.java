package net.splatcraft.forge.network.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.splatcraft.forge.tileentities.InkVatTileEntity;
import net.splatcraft.forge.util.ColorUtils;

public class UpdateBlockColorPacket extends PlayC2SPacket
{
    BlockPos pos;
    int color;
    int inkVatPointer = -1;

    public UpdateBlockColorPacket(BlockPos pos, int color)
    {
        this.color = color;
        this.pos = pos;
    }

    public UpdateBlockColorPacket(BlockPos pos, int color, int pointer)
    {
        this(pos, color);
        inkVatPointer = pointer;
    }

    public static UpdateBlockColorPacket decode(FriendlyByteBuf buffer)
    {
        return new UpdateBlockColorPacket(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void execute(Player player)
    {
        BlockEntity te = player.level.getBlockEntity(pos);

        if (te instanceof InkVatTileEntity)
        {
            ((InkVatTileEntity) te).pointer = inkVatPointer;
        }

        ColorUtils.setInkColor(te, color);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(pos.getX());
        buffer.writeInt(pos.getY());
        buffer.writeInt(pos.getZ());
        buffer.writeInt(color);
        buffer.writeInt(inkVatPointer);
    }
}
