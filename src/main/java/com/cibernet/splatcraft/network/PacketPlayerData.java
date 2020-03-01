package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;

public class PacketPlayerData extends SplatCraftPacket
{
    Data dataType;
    int info;

    @Override
    public SplatCraftPacket generatePacket(Object... dat) {
        data.writeInt( ((Data)dat[0]).ordinal());
        data.writeInt((Integer) dat[1]);

        return this;
    }

    @Override
    public SplatCraftPacket consumePacket(ByteBuf dat)
    {
        dataType = Data.values()[dat.readInt()];
        info = dat.readInt();

        return this;
    }

    @Override
    public void execute(EntityPlayer playerIn)
    {
        switch (dataType)
        {
            case COLOR:
                SplatCraftPlayerData.setInkColor(playerIn, (int) info);
            break;
            case IS_SQUID:
                SplatCraftPlayerData.setIsSquid(playerIn, info == 1);
            break;
        }
    }

    @Override
    public EnumSet<Side> getSenderSide() {
        return EnumSet.of(Side.CLIENT);
    }

    public enum Data
    {
        COLOR,
        IS_SQUID;
    }
}
