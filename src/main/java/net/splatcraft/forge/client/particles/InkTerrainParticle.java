package net.splatcraft.forge.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.registries.SplatcraftBlocks;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class InkTerrainParticle extends TextureSheetParticle {
	private final BlockPos pos;
	private final float uo;
	private final float vo;

	public InkTerrainParticle(ClientLevel p_108282_, double p_108283_, double p_108284_, double p_108285_, double p_108286_, double p_108287_, double p_108288_, float r, float g, float b) {
		this(p_108282_, p_108283_, p_108284_, p_108285_, p_108286_, p_108287_, p_108288_, new BlockPos(p_108283_, p_108284_, p_108285_), r, g, b);
	}

	public InkTerrainParticle(ClientLevel p_172451_, double p_172452_, double p_172453_, double p_172454_, double p_172455_, double p_172456_, double p_172457_, BlockPos p_172459_, float r, float g, float b) {
		super(p_172451_, p_172452_, p_172453_, p_172454_, p_172455_, p_172456_, p_172457_);
		this.pos = p_172459_;
		this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(SplatcraftBlocks.inkedBlock.get().defaultBlockState()));
		this.gravity = 1.0F;
		this.rCol = 0.6F * r;
		this.gCol = 0.6F * g;
		this.bCol = 0.6F * b;

		this.quadSize /= 2.0F;
		this.uo = this.random.nextFloat() * 3.0F;
		this.vo = this.random.nextFloat() * 3.0F;
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.TERRAIN_SHEET;
	}

	protected float getU0() {
		return this.sprite.getU((double)((this.uo + 1.0F) / 4.0F * 16.0F));
	}

	protected float getU1() {
		return this.sprite.getU((double)(this.uo / 4.0F * 16.0F));
	}

	protected float getV0() {
		return this.sprite.getV((double)(this.vo / 4.0F * 16.0F));
	}

	protected float getV1() {
		return this.sprite.getV((double)((this.vo + 1.0F) / 4.0F * 16.0F));
	}

	public int getLightColor(float p_108291_) {
		int i = super.getLightColor(p_108291_);
		return i == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : i;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<BlockParticleOption> {
		public Particle createParticle(BlockParticleOption p_108304_, ClientLevel p_108305_, double p_108306_, double p_108307_, double p_108308_, double p_108309_, double p_108310_, double p_108311_) {
			BlockState blockstate = p_108304_.getState();
			return !blockstate.isAir() && !blockstate.is(Blocks.MOVING_PISTON) ? (new net.minecraft.client.particle.TerrainParticle(p_108305_, p_108306_, p_108307_, p_108308_, p_108309_, p_108310_, p_108311_, blockstate)).updateSprite(blockstate, p_108304_.getPos()) : null;
		}
	}

	public Particle updateSprite(BlockState state, BlockPos pos) { //FORGE: we cannot assume that the x y z of the particles match the block pos of the block.
		if (pos != null) // There are cases where we are not able to obtain the correct source pos, and need to fallback to the non-model data version
			this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, level, pos));
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<InkTerrainParticleData>
	{

		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite)
		{
			this.spriteSet = sprite;
		}

		@Nullable
		@Override
		public Particle createParticle(InkTerrainParticleData typeIn, ClientLevel levelIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
		{
			return new InkTerrainParticle(levelIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.red, typeIn.green, typeIn.blue);
		}
	}
}

