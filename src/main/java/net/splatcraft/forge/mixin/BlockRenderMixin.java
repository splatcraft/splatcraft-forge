package net.splatcraft.forge.mixin;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRenderMixin
{
	/*
	@Mixin(BlockModelRenderer.AmbientOcclusionFace.class)
	public interface AOFace
	{
		@Accessor
		int[] getLightmap();
		@Accessor
		float[] getBrightness();
	}

	@Mixin(BlockModelRenderer.class)
	public interface ModelRenderer
	{
		@Invoker("calculateShape")
		void invokeCalculateShape(IBlockDisplayReader world, BlockState state, BlockPos pos, int[] vertices, Direction direction, @Nullable float[] afloat, BitSet bitset);
	}
	*/
}
