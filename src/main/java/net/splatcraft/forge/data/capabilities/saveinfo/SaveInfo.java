package net.splatcraft.forge.data.capabilities.saveinfo;

import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SaveInfo implements ISaveInfo
{
    private ArrayList<Integer> colorScores = new ArrayList<>();
    private HashMap<String, Stage> stages = new HashMap<>();


    @Override
    public Collection<Integer> getInitializedColorScores()
    {
        return colorScores;
    }

    @Override
    public void addInitializedColorScores(Integer... colors)
    {
        for (Integer color : colors)
        {
            if (!colorScores.contains(color))
            {
                colorScores.add(color);
            }
        }
    }

    @Override
    public void removeColorScore(Integer color)
    {
        colorScores.remove(color);
    }

    @Override
    public HashMap<String, Stage> getStages()
    {
        return stages;
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT nbt)
    {
        int[] arr = new int[colorScores.size()];
        for (int i = 0; i < colorScores.size(); i++)
        {
            arr[i] = colorScores.get(i);
        }

        nbt.putIntArray("StoredCriteria", arr);

        CompoundNBT stageNbt = new CompoundNBT();

        for(Map.Entry<String, Stage> e : stages.entrySet())
            stageNbt.put(e.getKey(), e.getValue().writeData());

        nbt.put("Stages", stageNbt);

        return nbt;
    }

    @Override
    public void readNBT(CompoundNBT nbt)
    {
        colorScores = new ArrayList<>();
        ScoreboardHandler.clearColorCriteria();

        for (int i : nbt.getIntArray("StoredCriteria"))
        {
            colorScores.add(i);
            ScoreboardHandler.createColorCriterion(i);
        }

        stages.clear();
        for(String key : nbt.getCompound("Stages").getAllKeys())
            stages.put(key,new Stage(nbt.getCompound("Stages").getCompound(key)));
    }
}
