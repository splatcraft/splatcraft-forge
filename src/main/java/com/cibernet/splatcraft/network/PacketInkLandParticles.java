package com.cibernet.splatcraft.network;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.utils.ClientUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class PacketInkLandParticles implements IMessage
{

    private boolean messageValid;
    int color;
    int numberOfParticles;
    int source;
    double yPos;
    
    public PacketInkLandParticles() {messageValid = false;}
    public PacketInkLandParticles(int color, int numberOfParticles, double yPos, Entity source)
    {
        messageValid = true;
        this.color = color;
        this.numberOfParticles = numberOfParticles;
        this.yPos = yPos;
        this.source = source.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            color = buf.readInt();
            numberOfParticles = buf.readInt();
            yPos = buf.readDouble();
            source = buf.readInt();
    
        } catch (IndexOutOfBoundsException e)
        {
            SplatCraft.logger.info(e.toString());
        }
        messageValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if(!messageValid)
            return;
        
        buf.writeInt(color);
        buf.writeInt(numberOfParticles);
        buf.writeDouble(yPos);
        buf.writeInt(source);
    }

    public static class Handler implements IMessageHandler<PacketInkLandParticles, IMessage>
    {

        @Override
        public IMessage onMessage(PacketInkLandParticles message, MessageContext ctx) {
            if(!message.messageValid && ctx.side != Side.CLIENT)
                return null;
            Minecraft.getMinecraft().addScheduledTask(() -> process(message, ctx));
            return null;
        }

        void process(PacketInkLandParticles message, MessageContext ctx)
        {
            try
            {
                World world = ClientUtils.getClientWorld();
                
                Entity entity = world.getEntityByID(message.source);
                
                double speed = 0.15000000596046448D;
    
                int color = message.color;
    
                for(int i = 0; i < message.numberOfParticles; i++)
                {
                    double angle = entity.world.rand.nextDouble() * Math.PI;
                    SplatCraftParticleSpawner.spawnInkParticle(entity.posX, message.yPos  , entity.posZ, Math.sin(angle) * speed, speed/2.0, Math.cos(angle) * speed, color, 1.8f);
                }
                
            } catch (Exception e)
            {
                SplatCraft.logger.info(e.toString());
            }
        }
    }
}
