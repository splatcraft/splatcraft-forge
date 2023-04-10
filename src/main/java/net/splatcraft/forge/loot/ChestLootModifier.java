package net.splatcraft.forge.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ChestLootModifier extends LootModifier
{
    protected final Item item;
    protected final int countMin;
    protected final int countMax;
    protected final float chance;
    protected final ResourceLocation parentTable;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected ChestLootModifier(LootItemCondition[] conditionsIn, Item itemIn, int countMin, int countMax, float chance, ResourceLocation parentTable)
    {
        super(conditionsIn);
        item = itemIn;
        this.countMin = countMin;
        this.countMax = countMax;
        this.chance = chance;
        this.parentTable = parentTable;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (!context.getQueriedLootTableId().equals(parentTable))
        {
            return generatedLoot;
        }

        float c = context.getRandom().nextFloat();

        if (c <= chance)
        {
            generatedLoot.add(new ItemStack(item, (countMax - countMin <= 0 ? 0 : context.getRandom().nextInt(countMax - countMin)) + countMin));
        }

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ChestLootModifier>
    {
        @Override
        public ChestLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition)
        {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(object, "item")));
            int countMin = GsonHelper.getAsInt(object, "countMin");
            int countMax = GsonHelper.getAsInt(object, "countMax");
            float chance = GsonHelper.getAsFloat(object, "chance");
            ResourceLocation partentTable = new ResourceLocation(GsonHelper.getAsString(object, "parent"));
            return new ChestLootModifier(ailootcondition, item, countMin, countMax, chance, partentTable);
        }

        @Override
        public JsonObject write(ChestLootModifier instance)
        {
            JsonObject result = new JsonObject();
            result.addProperty("item", Objects.requireNonNull(instance.item.getRegistryName()).toString());
            result.addProperty("countMin", instance.countMin);
            result.addProperty("countMax", instance.countMax);
            result.addProperty("chance", instance.chance);
            result.addProperty("parent", instance.parentTable.toString());

            return result;
        }
    }
}
