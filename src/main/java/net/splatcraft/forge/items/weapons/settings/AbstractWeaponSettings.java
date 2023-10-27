package net.splatcraft.forge.items.weapons.settings;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public abstract class AbstractWeaponSettings<CODEC>{
    public String name;

    public AbstractWeaponSettings(String name)
    {
        this.name = name;
    }

    public abstract float calculateDamage(int tickCount, boolean airborne, float charge, boolean isOnRollCooldown);
    public abstract float getMinDamage();

    //public abstract SELF deserializeJson(JsonObject json);

    public abstract Codec<CODEC> getCodec();

    public static float getJsonFloat(JsonObject json, String id)
    {
        return json.has(id) ? GsonHelper.getAsFloat(json, id) : 0;
    }

    public static int getJsonInt(JsonObject json, String id)
    {
        return json.has(id) ? GsonHelper.getAsInt(json, id) : 0;
    }
    public static boolean getJsonBoolean(JsonObject json, String id)
    {
        return json.has(id) && GsonHelper.getAsBoolean(json, id);
    }

    public void castAndDeserialize(Object o)
    {
        try {
            deserialize((CODEC) o);
        } catch (ClassCastException ignored){};
    }

    public abstract void deserialize(CODEC o);

    public abstract CODEC serialize();

    public void serializeToBuffer(FriendlyByteBuf buffer)
    {
        buffer.writeWithCodec(getCodec(), serialize());
    }
}
