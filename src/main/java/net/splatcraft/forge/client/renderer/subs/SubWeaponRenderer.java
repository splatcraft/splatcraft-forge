package net.splatcraft.forge.client.renderer.subs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.splatcraft.forge.SplatcraftConfig;
import net.splatcraft.forge.client.models.AbstractSubWeaponModel;
import net.splatcraft.forge.entities.subs.AbstractSubWeaponEntity;
import net.splatcraft.forge.items.weapons.SubWeaponItem;
import net.splatcraft.forge.registries.SplatcraftEntities;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class SubWeaponRenderer<E extends AbstractSubWeaponEntity, M extends AbstractSubWeaponModel<E>> extends EntityRenderer<E>
{

	protected SubWeaponRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(E entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
	{
		int color = entityIn.getColor();
		if (SplatcraftConfig.Client.getColorLock())
			color = ColorUtils.getLockedColor(color);

		float r = (float) (Math.floor((float) color / (256 * 256)) / 255f);
		float g = (float) (Math.floor((float) color / 256) % 256 / 255f);
		float b = (color % 256) / 255f;

		M model = getModel();
		ResourceLocation texture = getTextureLocation(entityIn);
		ResourceLocation inkTexture = getInkTextureLocation(entityIn);
		ResourceLocation overlay = getOverlayTextureLocation(entityIn);


		ItemStack stack = entityIn.getItem();
		if(stack.getItem() instanceof SubWeaponItem && entityIn.getType().equals(((SubWeaponItem) stack.getItem()).entityType.get()))
		{
			SubWeaponItem sub = (SubWeaponItem) stack.getItem();

			String customModelData = "";

			if(stack.hasTag() && stack.getTag().contains("CustomModelData") && Minecraft.getInstance().getResourceManager().hasResource(new ResourceLocation(sub.getRegistryName().getNamespace(),
					"textures/models/" + sub.getRegistryName().getPath() + "_" + stack.getTag().getInt("CustomModelData") + ".png")))
				customModelData = "_" + stack.getTag().getInt("CustomModelData");

			texture = new ResourceLocation(sub.getRegistryName().getNamespace(), "textures/weapons/sub/"+sub.getRegistryName().getPath()+customModelData+".png");
			inkTexture = new ResourceLocation(sub.getRegistryName().getNamespace(), "textures/weapons/sub/"+sub.getRegistryName().getPath()+customModelData+"_ink.png");

			if(overlay != null)
				overlay = new ResourceLocation(sub.getRegistryName().getNamespace(), "textures/weapons/sub/"+sub.getRegistryName().getPath()+customModelData+"_overlay.png");
		}

		model.setupAnim(entityIn, 0, 0, this.handleRotationFloat(entityIn, partialTicks), entityYaw, entityIn.getXRot());
		model.prepareMobModel(entityIn, 0, 0, partialTicks);
		int i = OverlayTexture.pack(OverlayTexture.u(getOverlayProgress(entityIn, partialTicks)), OverlayTexture.v(false));
		model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(inkTexture)), packedLightIn, i, r, g, b, 1);
		model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(texture)), packedLightIn, i, 1, 1, 1, 1);

		if(overlay != null)
		{
			float[] overlayRgb = getOverlayColor(entityIn, partialTicks);
			model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(model.renderType(overlay)), packedLightIn, i, overlayRgb[0], overlayRgb[1], overlayRgb[2], 1);
		}


		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	protected float getOverlayProgress(E entity, float partialTicks)
	{
		return 0;
	}

	public abstract M getModel();
	public abstract ResourceLocation getInkTextureLocation(E entity);

	@Nullable
	public ResourceLocation getOverlayTextureLocation(E entity)
	{
		return null;
	}

	public float[] getOverlayColor(E entity, float partialTicks)
	{
		return new float[] {1,1,1};
	}

	protected float handleRotationFloat(E livingBase, float partialTicks)
	{
		return (float) livingBase.tickCount + partialTicks;
	}
}
