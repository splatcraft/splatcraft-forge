package net.splatcraft.forge.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.FishingPredicate;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FishingLootModifier extends LootModifier
{
    protected final Item item;
    protected final int countMin;
    protected final int countMax;
    protected final float chance;
    protected final int quality;
    protected final boolean isTreasure;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected FishingLootModifier(ILootCondition[] conditionsIn, Item itemIn, int countMin, int countMax, float chance, int quality, boolean isTreasure)
    {
        super(conditionsIn);
        item = itemIn;
        this.countMin = countMin;
        this.countMax = countMax;
        this.chance = chance;
        this.quality = quality;
        this.isTreasure = isTreasure;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        if (!(context.getParamOrNull(LootParameters.THIS_ENTITY) instanceof FishingBobberEntity) || isTreasure && !FishingPredicate.inOpenWater(true).matches(Objects.requireNonNull(context.getParamOrNull(LootParameters.THIS_ENTITY))))
        {
            return generatedLoot;
        }

        float chanceMod = 0;
        if (context.getParamOrNull(LootParameters.KILLER_ENTITY) instanceof LivingEntity)
        {
            LivingEntity entity = (LivingEntity) context.getParamOrNull(LootParameters.KILLER_ENTITY);
            assert entity != null;
            ItemStack stack = entity.getUseItem();
            int fishingLuck = EnchantmentHelper.getFishingLuckBonus(stack);
            float luck = entity instanceof PlayerEntity ? ((PlayerEntity) entity).getLuck() : 0;

            if (isTreasure)
            {
                chanceMod += fishingLuck;
            }
            chanceMod += luck;

            chanceMod *= quality * (chance / 2);
        }

        if (context.getRandom().nextInt(100) <= (chance + chanceMod) * 100)
        {
            if (generatedLoot.size() <= 1)
            {
                generatedLoot.clear();
            }
            generatedLoot.add(new ItemStack(item, (countMax - countMin <= 0 ? 0 : context.getRandom().nextInt(countMax - countMin)) + countMin));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<FishingLootModifier>
    {

        protected static int getInt(JsonObject json, String memberName)
        {
            if (json.has(memberName))
            {
                return getInt(json.get(memberName), memberName);
            } else
            {
                throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
            }
        }

        protected static boolean isBoolean(JsonObject json, String memberName)
        {
            return JSONUtils.isValidPrimitive(json, memberName) && json.getAsJsonPrimitive(memberName).isBoolean();
        }

        protected static int getInt(JsonElement json, String memberName)
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber())
            {
                return json.getAsInt();
            } else
            {
                throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + JSONUtils.convertToString(json, "???"));
            }
        }

        @Override
        public FishingLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
        {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getAsString(object, "item")));
            int countMin = JSONUtils.getAsInt(object, "countMin");
            int countMax = JSONUtils.getAsInt(object, "countMax");
            float chance = JSONUtils.getAsFloat(object, "chance");
            int quality = getInt(object, "quality");
            boolean isTreasure = isBoolean(object, "isTreasure") && JSONUtils.getAsBoolean(object, "isTreasure");
            return new FishingLootModifier(ailootcondition, item, countMin, countMax, chance, quality, isTreasure);
        }

        @Override
        public JsonObject write(FishingLootModifier instance)
        {
            JsonObject result = new JsonObject();
            result.addProperty("item", Objects.requireNonNull(instance.item.getRegistryName()).toString());
            result.addProperty("countMin", instance.countMin);
            result.addProperty("countMax", instance.countMax);
            return result;
        }
    }
}
