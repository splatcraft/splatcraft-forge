package net.splatcraft.forge.data.capabilities.saveinfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.data.Stage;
import net.splatcraft.forge.handlers.ScoreboardHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.UpdateStageListPacket;

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

    public boolean createStage(Level level, String stageId, BlockPos corner1, BlockPos corner2, Component stageName)
    {
        if(level.isClientSide)
            return false;

        if(stages.containsKey(stageId))
            return false;

        stages.put(stageId, new Stage(level, corner1, corner2, stageId, stageName));
        SplatcraftPacketHandler.sendToAll(new UpdateStageListPacket(stages));
        return true;
    }

    public boolean createStage(Level level, String stageId, BlockPos corner1, BlockPos corner2) {
        return createStage(level, stageId, corner1, corner2, new TextComponent(stageId));
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
