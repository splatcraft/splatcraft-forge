package com.cibernet.splatcraft.particles;

import com.google.common.collect.Maps;

import java.util.Set;

public enum EnumSplatCraftParticles
{
    INK("ink", false, 1)
    ;
    private final String particleName;
    private final boolean shouldIgnoreRange;
    private final int argumentCount;

    private EnumSplatCraftParticles(String particleNameIn, boolean shouldIgnoreRangeIn, int argumentCountIn)
    {
        this.particleName = particleNameIn;
        this.shouldIgnoreRange = shouldIgnoreRangeIn;
        this.argumentCount = argumentCountIn;
    }


    private EnumSplatCraftParticles(String particleNameIn,  boolean shouldIgnoreRangeIn)
    {
        this(particleNameIn,  shouldIgnoreRangeIn, 0);
    }

    public String getParticleName()
    {
        return this.particleName;
    }

    public int getArgumentCount()
    {
        return this.argumentCount;
    }

    public boolean getShouldIgnoreRange()
    {
        return this.shouldIgnoreRange;
    }
}
