package net.splatcraft.forge.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.ApiStatus.AvailableSince;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.AnimationUtils;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public abstract class GeoNonLivingRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T>
		implements IGeoRenderer<T>
{
	static {
		AnimationController.addModelFetcher(animatable -> animatable instanceof Entity entity ? (IAnimatableModel<Object>)AnimationUtils.getGeoModelForEntity(entity) : null);
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected final List<GeoLayerRenderer<T>> layerRenderers = new ObjectArrayList<>();
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

	public MultiBufferSource rtb;
	public ResourceLocation whTexture;
	protected float widthScale = 1;
	protected float heightScale = 1;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoNonLivingRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);

		this.modelProvider = modelProvider;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
	                        VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
	                        float partialTicks) {
		this.animatable = animatable;
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.rtb = bufferSource;
		this.whTexture = getTextureLocation(animatable);

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	public void render(T animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
	                   int packedLight) {
		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.pushPose();


		this.dispatchedMat = poseStack.last().pose().copy();
		boolean shouldSit = false;//animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = false;//animatable.isBaby();

		float lerpBodyRot = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());

		float ageInTicks = animatable.tickCount + partialTick;
		float limbSwingAmount = 0;
		float limbSwing = 0;

		applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

		float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
		entityModelData.headPitch = -headPitch;
		entityModelData.netHeadYaw = -lerpBodyRot;

		AnimationEvent<T> predicate = new AnimationEvent<T>(animatable, limbSwing, limbSwingAmount, partialTick,
				(limbSwingAmount <= -getSwingMotionAnimThreshold() || limbSwingAmount > getSwingMotionAnimThreshold()), Collections.singletonList(entityModelData));
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));

		this.modelProvider.setCustomAnimations(animatable, getInstanceId(animatable), predicate); // TODO change to setCustomAnimations in 1.20+

		poseStack.translate(0, 0.01f, 0);
		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

		Color renderColor = getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(animatable, partialTick, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));

		if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) {
			VertexConsumer glintBuffer = bufferSource.getBuffer(RenderType.entityGlintDirect());
			VertexConsumer translucentBuffer = bufferSource
					.getBuffer(RenderType.entityTranslucentCull(getTextureLocation(animatable)));

			render(model, animatable, partialTick, renderType, poseStack, bufferSource,
					glintBuffer != translucentBuffer ? VertexMultiConsumer.create(glintBuffer, translucentBuffer)
							: null,
					packedLight, getOverlay(animatable, 0), renderColor.getRed() / 255f,
					renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
					renderColor.getAlpha() / 255f);
		}

		if (!animatable.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				renderLayer(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, ageInTicks,
						lerpBodyRot, headPitch, bufferSource, layerRenderer);
			}
		}

		if (ModList.get().isLoaded("patchouli"))
			PatchouliCompat.patchouliLoaded(poseStack);

		poseStack.popPose();

		super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
	                              int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		RenderUtils.translateMatrixToBone(poseStack, bone);
		RenderUtils.translateToPivotPoint(poseStack, bone);

		boolean rotOverride = bone.rotMat != null;

		if (rotOverride) {
			poseStack.last().pose().multiply(bone.rotMat);
			poseStack.last().normal().mul(new Matrix3f(bone.rotMat));
		}
		else {
			RenderUtils.rotateMatrixAroundBone(poseStack, bone);
		}

		RenderUtils.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.translate(new Vector3f(getRenderOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(this.animatable.position()));
			bone.setWorldSpaceXform(worldState);
		}

		RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

		if (!bone.isHidden) {
			if (!bone.cubesAreHidden()) {
				for (GeoCube geoCube : bone.childCubes) {
					poseStack.pushPose();
					renderCube(geoCube, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
					poseStack.popPose();
				}
			}

			for (GeoBone childBone : bone.childBones) {
				renderRecursively(childBone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			}
		}

		poseStack.popPose();
	}

	protected void renderLayer(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T animatable,
	                           float limbSwing, float limbSwingAmount, float partialTick, float rotFloat, float netHeadYaw,
	                           float headPitch, MultiBufferSource bufferSource2, GeoLayerRenderer<T> layerRenderer) {
		layerRenderer.render(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, rotFloat,
				netHeadYaw, headPitch);
	}


	@Override
	public int getInstanceId(T animatable) {
		return animatable.getId();
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@AvailableSince(value = "3.0.53")
	public int getOverlay(T entity, float u)
	{
		return OverlayTexture.pack(OverlayTexture.u(u),
			OverlayTexture.v(false));
	}


	protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
	                              float partialTick)
	{

	}

	protected boolean isVisible(T animatable) {
		return !animatable.isInvisible();
	}

	private static float getFacingAngle(Direction direction) {
		return switch (direction) {
			case SOUTH -> 90f;
			case NORTH -> 270f;
			case EAST -> 180f;
			default -> 0f;
		};
	}

	@Override
	public boolean shouldShowName(T animatable) {
		double nameRenderDistance = animatable.isDiscrete() ? 32d : 64d;

		if (this.entityRenderDispatcher.distanceToSqr(animatable) >= nameRenderDistance * nameRenderDistance)
			return false;

		return animatable == this.entityRenderDispatcher.crosshairPickEntity && animatable.hasCustomName() && Minecraft.renderNames();
	}

	/**
	 * Determines how far (from 0) the arm swing should be moving before counting as moving for animation purposes.
	 */
	protected float getSwingMotionAnimThreshold() {
		return 0.15f;
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return this.modelProvider.getTextureLocation(animatable);
	}

	public final boolean addLayer(GeoLayerRenderer<T> layer) {
		return this.layerRenderers.add(layer);
	}

	@Override
	public void setCurrentRTB(MultiBufferSource bufferSource) {
		this.rtb = bufferSource;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}
}
