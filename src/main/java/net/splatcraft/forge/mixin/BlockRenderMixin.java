package net.splatcraft.forge.mixin;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
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

	@Mixin(ModelBlockRenderer.AmbientOcclusionFace.class)
	public interface AOFace
	{
		@Accessor
		int[] getLightmap();
		@Accessor
		float[] getBrightness();
	}

	@Mixin(ModelBlockRenderer.class)
	public interface ModelRenderer
	{
		@Invoker("calculateShape")
		void invokeCalculateShape(BlockAndTintGetter world, BlockState state, BlockPos pos, int[] vertices, Direction direction, @Nullable float[] afloat, BitSet bitset);
	}

}
