package com.cibernet.splatcraft.loot;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseLootModifier extends LootModifier {
    protected final Item item;
    protected final int countMin;
    protected final int countMax;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected BaseLootModifier(ILootCondition[] conditionsIn, Item itemIn, int countMin, int countMax) {
        super(conditionsIn);
        item = itemIn;
        this.countMin = countMin;
        this.countMax = countMax;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        List<ItemStack> result = new ArrayList<>();
        result.add(new ItemStack(item, (countMax - countMin <= 0 ? 0 : context.getRandom().nextInt(countMax - countMin)) + countMin));
        return result;
    }

    public static class Serializer extends GlobalLootModifierSerializer<BaseLootModifier> {

        @Override
        public BaseLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getString(object, "item")));
            int countMin = JSONUtils.getInt(object, "countMin");
            int countMax = JSONUtils.getInt(object, "countMax");
            return new BaseLootModifier(ailootcondition, item, countMin, countMax);
        }

        @Override
        public JsonObject write(BaseLootModifier instance) {
            JsonObject result = new JsonObject();
            result.addProperty("item", Objects.requireNonNull(instance.item.getRegistryName()).toString());
            result.addProperty("countMin", instance.countMin);
            result.addProperty("countMax", instance.countMax);
            return result;
        }
    }
}
