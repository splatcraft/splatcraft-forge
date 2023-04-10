package net.splatcraft.forge.loot;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
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
    protected FishingLootModifier(LootItemCondition[] conditionsIn, Item itemIn, int countMin, int countMax, float chance, int quality, boolean isTreasure)
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
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof FishingHook) || isTreasure && !FishingHookPredicate.inOpenWater(true).matches(Objects.requireNonNull(context.getParamOrNull(LootContextParams.THIS_ENTITY))))
        {
            return generatedLoot;
        }

        float chanceMod = 0;
        if (context.getParamOrNull(LootContextParams.KILLER_ENTITY) instanceof LivingEntity entity)
        {
            ItemStack stack = entity.getUseItem();
            int fishingLuck = EnchantmentHelper.getFishingLuckBonus(stack);
            float luck = entity instanceof Player ? ((Player) entity).getLuck() : 0;

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

        @Override
        public FishingLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition)
        {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(object, "item")));
            int countMin = GsonHelper.getAsInt(object, "countMin");
            int countMax = GsonHelper.getAsInt(object, "countMax");
            float chance = GsonHelper.getAsFloat(object, "chance");
            int quality = GsonHelper.getAsInt(object, "quality");
            boolean isTreasure = GsonHelper.isBooleanValue(object, "isTreasure") && GsonHelper.getAsBoolean(object, "isTreasure");
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
