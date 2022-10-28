package net.splatcraft.forge.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.BitSet;

@OnlyIn(Dist.CLIENT)
public class BlockRenderMixin
{
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
}
