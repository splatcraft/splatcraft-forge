package net.splatcraft.forge.items;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlueprintItem extends Item
{
	public BlueprintItem()
	{
		super(new Properties().stacksTo(16));
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
