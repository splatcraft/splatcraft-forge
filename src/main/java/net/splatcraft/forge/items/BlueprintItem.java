package net.splatcraft.forge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.splatcraft.forge.data.SplatcraftTags;
import net.splatcraft.forge.items.weapons.*;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BlueprintItem extends Item
{
	public static final HashMap<String, Predicate<Item>> weaponPools = new HashMap<>()
	{{
		put("shooters", instanceOf(ShooterItem.class));
		put("blasters", instanceOf(BlasterItem.class));
		put("rollers", instanceOf(RollerItem.class));
		put("chargers", instanceOf(ChargerItem.class));
		put("sloshers", instanceOf(SlosherItem.class));
		put("dualies", instanceOf(DualieItem.class));
		put("sub_weapons", instanceOf(SubWeaponItem.class));
		put("ink_tanks", instanceOf(InkTankItem.class));
		put("wildcard", item -> true);
	}};

	public static Predicate<Item> instanceOf(Class<? extends Item> clazz)
	{
		return clazz::isInstance;
	}

	public BlueprintItem()
	{
		super(new Properties().stacksTo(16).tab(SplatcraftItemGroups.GROUP_GENERAL));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, components, flag);

		if(stack.hasTag())
		{
			CompoundTag nbt = stack.getTag();

			if(nbt.getBoolean("HideTooltip"))
				return;

			if(nbt.contains("Advancements"))
			{
				components.add(new TranslatableComponent("item.splatcraft.blueprint.tooltip"));
				return;
			}
		}

		components.add(new TranslatableComponent("item.splatcraft.blueprint.tooltip.empty"));
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list)
	{
		if(tab == CreativeModeTab.TAB_SEARCH)
			weaponPools.forEach((key, value) -> list.add(setPoolFromWeaponType(new ItemStack(this), key)));
		else if(allowdedIn(tab))
		{
			list.add(setPoolFromWeaponType(new ItemStack(this), "wildcard"));
		}
	}
	public static ItemStack addToAdvancementPool(ItemStack blueprint, String... advancementIds)
	{
		return addToAdvancementPool(blueprint, Arrays.stream(advancementIds));
	}
	public static ItemStack setPoolFromWeaponType(ItemStack blueprint, String weaponType)
	{
		if(!weaponPools.containsKey(weaponType))
			return blueprint;

		Predicate<Item> predicate = weaponPools.get(weaponType);

		ListTag lore = new ListTag();
		lore.add(StringTag.valueOf(Component.Serializer.toJson(new TranslatableComponent("item.splatcraft.blueprint.tooltip." + weaponType)
				.withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(false)))));

		blueprint.getOrCreateTagElement("display").put("Lore",lore);

		return addToAdvancementPool(blueprint, SplatcraftItems.weapons.stream().filter(predicate.and(item ->
				!item.builtInRegistryHolder().is(SplatcraftTags.Items.BLUEPRINT_EXCLUDED))).map(ForgeRegistryEntry::getRegistryName).map(registryName ->
				new ResourceLocation(registryName.getNamespace(), "unlocks/" + registryName.getPath())).map(ResourceLocation::toString));
	}
	public static ItemStack addToAdvancementPool(ItemStack blueprint, Stream<String> advancementIds)
	{
		CompoundTag nbt = blueprint.getOrCreateTag();
		ListTag pool = nbt.contains("Advancements", Tag.TAG_LIST) ? nbt.getList("Advancements", Tag.TAG_STRING) : new ListTag();

		advancementIds.map(StringTag::valueOf).forEach(pool::add);

		nbt.put("Advancements", pool);
		return blueprint;
	}

	public static List<Advancement> getAdvancementPool(Level level, ItemStack blueprint)
	{
		List<Advancement> output = new ArrayList<>();

		if(blueprint.hasTag())
			blueprint.getTag().getList("Advancements", Tag.TAG_STRING).forEach(
					tag ->
					{
						Advancement advancement;
						advancement = level.getServer().getAdvancements().getAdvancement(new ResourceLocation(tag.getAsString()));

						if(advancement != null)
							output.add(advancement);
					}
			);

		return output;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(!(player instanceof ServerPlayer serverPlayer))
			return super.use(level, player, hand);

		ItemStack stack = player.getItemInHand(hand);

		if(stack.hasTag())
		{
			List<Advancement> pool = getAdvancementPool(level, stack);
			int count = pool.size();

			if(count > 0)
			{
				pool.removeIf(advancement -> serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone());

				if (!pool.isEmpty()) {
					Advancement advancement = pool.get(level.random.nextInt(pool.size()));

					for (String key : serverPlayer.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria())
						serverPlayer.getAdvancements().award(advancement, key);

					if (advancement.getDisplay() != null && !advancement.getDisplay().shouldShowToast())
						player.displayClientMessage(new TranslatableComponent("status.blueprint.unlock", advancement.getDisplay().getTitle()), true);

					stack.shrink(1);
					return InteractionResultHolder.consume(stack);
				}

				player.displayClientMessage(new TranslatableComponent("status.blueprint.already_unlocked" + (count > 1 ? "" : ".single")), true);
				return super.use(level, player, hand);
			}
		}


		player.displayClientMessage(new TranslatableComponent("status.blueprint.invalid"), true);
		return super.use(level, player, hand);
	}
}
