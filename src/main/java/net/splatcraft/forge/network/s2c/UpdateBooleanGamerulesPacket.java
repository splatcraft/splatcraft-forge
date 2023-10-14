package net.splatcraft.forge.network.s2c;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameRules;
import net.splatcraft.forge.registries.SplatcraftGameRules;

public class UpdateBooleanGamerulesPacket extends PlayS2CPacket
{
    public TreeMap<Integer, Boolean> booleanRules;

    public UpdateBooleanGamerulesPacket(TreeMap<Integer, Boolean> booleanRules)
    {
        this.booleanRules = booleanRules;
    }

    public UpdateBooleanGamerulesPacket(GameRules.Key<GameRules.BooleanValue> rule, boolean value)
    {
        this.booleanRules = new TreeMap<Integer, Boolean>()
        {{
            put(SplatcraftGameRules.getRuleIndex(rule), value);
        }};
    }

    public static UpdateBooleanGamerulesPacket decode(FriendlyByteBuf buffer)
    {
        TreeMap<Integer, Boolean> booleanRules = new TreeMap<>();
        int entrySize = buffer.readInt();

        for (int i = 0; i < entrySize; i++)
        {
            booleanRules.put(buffer.readInt(), buffer.readBoolean());
        }

        return new UpdateBooleanGamerulesPacket(booleanRules);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        Set<Map.Entry<Integer, Boolean>> entrySet = booleanRules.entrySet();

        buffer.writeInt(entrySet.size());

        for (Map.Entry<Integer, Boolean> rule : entrySet) {
            buffer.writeInt(rule.getKey());
            buffer.writeBoolean(rule.getValue());
        }
    }

    @Override
    public void execute() {
        SplatcraftGameRules.booleanRules.putAll(booleanRules);
    }
}
