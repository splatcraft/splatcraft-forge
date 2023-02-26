package net.splatcraft.forge.network.s2c;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameRules;
import net.splatcraft.forge.registries.SplatcraftGameRules;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class UpdateIntGamerulesPacket extends PlayToClientPacket {
    public TreeMap<Integer, Integer> intRules;

    public UpdateIntGamerulesPacket(TreeMap<Integer, Integer> intRules) {
        this.intRules = intRules;
    }

    public UpdateIntGamerulesPacket(GameRules.RuleKey<GameRules.IntegerValue> rule, int value) {
        this.intRules = new TreeMap<Integer, Integer>() {{
            put(SplatcraftGameRules.getRuleIndex(rule), value);
        }};
    }

    public static UpdateIntGamerulesPacket decode(PacketBuffer buffer) {
        TreeMap<Integer, Integer> intRules = new TreeMap<>();
        int entrySize = buffer.readInt();

        for (int i = 0; i < entrySize; i++) {
            intRules.put(buffer.readInt(), buffer.readInt());
        }

        return new UpdateIntGamerulesPacket(intRules);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        Set<Map.Entry<Integer, Integer>> entrySet = intRules.entrySet();

        buffer.writeInt(entrySet.size());

        for (Map.Entry<Integer, Integer> rule : entrySet) {
            buffer.writeInt(rule.getKey());
            buffer.writeInt(rule.getValue());
        }
    }

    @Override
    public void execute() {
        SplatcraftGameRules.intRules.putAll(intRules);
    }
}
