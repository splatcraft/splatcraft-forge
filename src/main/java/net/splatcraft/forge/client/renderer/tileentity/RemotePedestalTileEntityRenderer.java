package net.splatcraft.forge.client.renderer.tileentity;

import net.splatcraft.forge.tileentities.RemotePedestalTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class RemotePedestalTileEntityRenderer extends TileEntityRenderer<RemotePedestalTileEntity>
{
    public RemotePedestalTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(RemotePedestalTileEntity remotePedestalTileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {

        ItemStack stack = remotePedestalTileEntity.getItem(0);

        if(!stack.isEmpty())
        {

            matrixStack.pushPose();
            matrixStack.translate(0.5F, 1F, 0.5F);
            //matrixStack.rotate(Vector3f.YP.rotation(f);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
            matrixStack.popPose();
        }
    }
}
