package net.splatcraft.forge.items;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.AdvancementCommands;
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
import net.splatcraft.forge.crafting.WeaponWorkbenchSubtypeRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlueprintItem extends Item
{
	public BlueprintItem()
	{
		super(new Properties().stacksTo(1));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag)
	{
		super.appendHoverText(stack, level, components, flag);

		if(stack.hasTag())
		{
			Advancement advancement = Minecraft.getInstance().player.connection.getAdvancements().getAdvancements().get(new ResourceLocation(stack.getTag().getString("Advancement")));

			if(advancement != null)
			{
				components.add(advancement.getDisplay().getTitle());
				return;
			}
		}

		components.add(new TranslatableComponent("item.splatcraft.blueprint.tooltip"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(!(player instanceof ServerPlayer serverPlayer))
			return super.use(level, player, hand);

		ItemStack stack = player.getItemInHand(hand);

		if(stack.hasTag())
		{
			Advancement advancement = level.getServer().getAdvancements().getAdvancement(new ResourceLocation(stack.getTag().getString("Advancement")));

			if(advancement != null)
			{
				AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);

				if(!progress.isDone())
				{
					for (String key : progress.getRemainingCriteria())
						serverPlayer.getAdvancements().award(advancement, key);

					if(advancement.getDisplay() != null && !advancement.getDisplay().shouldShowToast())
						player.displayClientMessage(new TranslatableComponent("status.blueprint.unlock", advancement.getDisplay().getTitle()), true);

					stack.shrink(1);
					return InteractionResultHolder.consume(stack);
				}

				player.displayClientMessage(new TranslatableComponent("status.blueprint.already_unlocked"), true);
				return super.use(level, player, hand);
			}
		}

		player.displayClientMessage(new TranslatableComponent("status.blueprint.invalid"), true);
		return super.use(level, player, hand);
	}
}
