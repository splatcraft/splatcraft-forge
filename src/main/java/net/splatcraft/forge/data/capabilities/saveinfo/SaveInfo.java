package net.splatcraft.forge.data.capabilities.saveinfo;

import net.minecraft.nbt.CompoundTag;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.handlers.ScoreboardHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SaveInfo
{
    private ArrayList<Integer> colorScores = new ArrayList<>();
    private HashMap<String, Stage> stages = new HashMap<>();

    boolean stagesLoaded = false;
    
    public Collection<Integer> getInitializedColorScores()
    {
        return colorScores;
    }

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

    public void removeColorScore(Integer color)
    {
        colorScores.remove(color);
    }

    public HashMap<String, Stage> getStages()
    {
        return stages;
    }
    
    public CompoundTag writeNBT(CompoundTag nbt)
    {
        int[] arr = new int[colorScores.size()];
        for (int i = 0; i < colorScores.size(); i++)
        {
            arr[i] = colorScores.get(i);
        }

        nbt.putIntArray("StoredCriteria", arr);

        CompoundTag stageNbt = new CompoundTag();

        for(Map.Entry<String, Stage> e : stages.entrySet())
            stageNbt.put(e.getKey(), e.getValue().writeData());

        nbt.put("Stages", stageNbt);

        return nbt;
    }

    public void readNBT(CompoundTag nbt)
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
            stages.put(key,new Stage(nbt.getCompound("Stages").getCompound(key), key));
    }


}
