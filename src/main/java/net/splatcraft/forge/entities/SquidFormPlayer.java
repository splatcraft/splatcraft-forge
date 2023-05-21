package net.splatcraft.forge.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class SquidFormPlayer implements IAnimatable
{
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	@Override
	public void registerControllers(AnimationData data) {

	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
}
