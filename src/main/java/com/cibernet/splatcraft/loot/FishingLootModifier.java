package com.cibernet.splatcraft.loot;

import com.google.gson.JsonObject;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context)
    {
        if(!(context.get(LootParameters.THIS_ENTITY) instanceof FishingBobberEntity) || (isTreasure && !(FishingPredicate.func_234640_a_(true).func_234638_a_(context.get(LootParameters.THIS_ENTITY)))))
            return generatedLoot;

        float chanceMod = 0;
        if(context.get(LootParameters.KILLER_ENTITY) instanceof LivingEntity)
        {
            LivingEntity entity = (LivingEntity) context.get(LootParameters.KILLER_ENTITY);
            ItemStack stack = entity.getActiveItemStack();
            int fishingLuck = EnchantmentHelper.getFishingLuckBonus(stack);
            float luck = entity instanceof PlayerEntity ? ((PlayerEntity) entity).getLuck() : 0;

            if(isTreasure)
                chanceMod += fishingLuck;
            chanceMod += luck;

            chanceMod *= quality * (chance/2);
        }

        if(context.getRandom().nextInt(100) <= (chance+chanceMod)*100)
        {
            if(generatedLoot.size() <= 1)
                generatedLoot.clear();
            generatedLoot.add(new ItemStack(item, (countMax-countMin <= 0 ? 0 : context.getRandom().nextInt(countMax-countMin))+countMin));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<FishingLootModifier>
    {

        @Override
        public FishingLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
        {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation((JSONUtils.getString(object, "item"))));
            int countMin = JSONUtils.getInt(object, "countMin");
            int countMax = JSONUtils.getInt(object, "countMax");
            float chance = JSONUtils.getFloat(object, "chance");
            int quality = JSONUtils.getInt(object, "quality");
            boolean isTreasure = (!JSONUtils.isJsonPrimitive(object, "isTreasure") ? false : object.getAsJsonPrimitive("isTreasure").isBoolean()) ? JSONUtils.getBoolean(object, "isTreasure") : false;
            return new FishingLootModifier(ailootcondition, item, countMin, countMax, chance, quality, isTreasure);
        }

        @Override
        public JsonObject write(FishingLootModifier instance)
        {
            JsonObject result = new JsonObject();
            result.addProperty("item", instance.item.getRegistryName().toString());
            result.addProperty("countMin", instance.countMin);
            result.addProperty("countMax", instance.countMax);
            return result;
        }
    }
}
