package com.cibernet.splatcraft.particles;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleInk extends Particle
{
    public int color;
    protected ParticleInk(World worldIn, double posXIn, double posYIn, double posZIn, int color) {
        super(worldIn, posXIn, posYIn, posZIn);
        this.color = color;
    }

    public ParticleInk(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color, float size) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.color = color;

        double r = Math.floor(color / (256*256));
        double g = Math.floor(color / 256) % 256;
        double b = color % 256;
        
        this.particleRed = Math.max(5/255f,(float) (r/255) - 5/255f);
        this.particleGreen = Math.max(5/255f,(float) (g/255) - 5/255f);
        this.particleBlue = Math.max(5/255f,(float) (b/255) - 5/255f);
        
        
        this.particleScale = Math.min(1, Math.max(0, rand.nextFloat()))*5 * size;

        this.particleGravity = 0.1f;
    }

    public void onUpdate()
    {
        super.onUpdate();
        if(particleGravity > 0)
            this.motionY -= 0.004D + 0.04D * (double)this.particleGravity;

        /*
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.002D;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8500000238418579D;
        this.motionY *= 0.8500000238418579D;
        this.motionZ *= 0.8500000238418579D;
        */
        if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() == Material.WATER)
        {
            this.setExpired();
        }
        /*
        if (this.particleMaxAge-- <= 0)
        {
            this.setExpired();
        }
        */
    }

}
