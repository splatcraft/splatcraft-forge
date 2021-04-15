package com.cibernet.splatcraft.client.renderer.tileentity;

import com.cibernet.splatcraft.tileentities.RemotePedestalTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class RemotePedestalTileEntityRenderer extends TileEntityRenderer<RemotePedestalTileEntity>
{
    public RemotePedestalTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(RemotePedestalTileEntity remotePedestalTileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {

        ItemStack stack = remotePedestalTileEntity.getStackInSlot(0);

        if(!stack.isEmpty())
        {

            matrixStack.push();
            matrixStack.translate(0.5F, 1F, 0.5F);
            //matrixStack.rotate(Vector3f.YP.rotation(f);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer);
            matrixStack.pop();
        }
    }
}
