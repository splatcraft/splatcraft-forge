package net.splatcraft.forge.data.capabilities.saveinfo;

import net.minecraft.nbt.CompoundNBT;
import net.splatcraft.forge.data.Stage;

import java.util.Collection;
import java.util.HashMap;

public interface ISaveInfo
{


    Collection<Integer> getInitializedColorScores();
    void addInitializedColorScores(Integer... colors);
    void removeColorScore(Integer color);

    HashMap<String, Stage> getStages();

    CompoundNBT writeNBT(CompoundNBT nbt);

    void readNBT(CompoundNBT nbt);
}
