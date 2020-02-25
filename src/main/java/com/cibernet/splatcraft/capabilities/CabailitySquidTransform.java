package com.cibernet.splatcraft.capabilities;

public class CabailitySquidTransform implements ICapablility<Boolean>
{
    private boolean isTransformed;

    @Override
    public void setValue(Boolean value) {
        isTransformed = value;
    }

    @Override
    public Boolean getValue() {
        return isTransformed;
    }
}
