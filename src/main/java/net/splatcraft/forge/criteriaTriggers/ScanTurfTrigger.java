package net.splatcraft.forge.criteriaTriggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.registries.SplatcraftInkColors;
import net.splatcraft.forge.util.InkColor;

public class ScanTurfTrigger extends SimpleCriterionTrigger<ScanTurfTrigger.TriggerInstance>
{
	static final ResourceLocation ID = new ResourceLocation(Splatcraft.MODID, "scan_turf");

	public ResourceLocation getId() {
		return ID;
	}

	public ScanTurfTrigger.TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite composite, DeserializationContext context)
	{
		return new ScanTurfTrigger.TriggerInstance(composite, GsonHelper.getAsInt(json, "blocks_inked", 0), GsonHelper.getAsBoolean(json, "winner", false));
	}

	public void trigger(ServerPlayer player, int blocksInked, boolean winner) {
		this.trigger(player, (instance) -> instance.matches(blocksInked, winner));
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final int blocksInked;
		private final boolean winner;

		public TriggerInstance(EntityPredicate.Composite p_27688_, int blocksInked, boolean winner)
		{
			super(ScanTurfTrigger.ID, p_27688_);
			this.blocksInked = blocksInked;
			this.winner = winner;
		}

		public boolean matches(int blocksInked, boolean winner)
		{
			return blocksInked >= this.blocksInked && (winner || !this.winner);
		}

		public JsonObject serializeToJson(SerializationContext context)
		{
			JsonObject jsonobject = super.serializeToJson(context);
			jsonobject.addProperty("blocks_inked", blocksInked);
			jsonobject.addProperty("winner", winner);
			return jsonobject;
		}
	}
}
