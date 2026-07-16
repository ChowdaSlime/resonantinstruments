package net.chowdaslime.resonantinstruments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.chowdaslime.resonantinstruments.block.entity.ResonanceChamberBlockEntity;
import net.chowdaslime.resonantinstruments.entity.RitualRings;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ResonanceChamberRenderer implements BlockEntityRenderer<ResonanceChamberBlockEntity, ResonanceChamberRenderer.ResonanceChamberRenderState> {

    private static final float MAX_LIFT = 1.3f;
    private static final float SPIN_BASE_SPEED = 8.0f;
    private static final float PEAK_SPIN_SPEED = 24.0f;
    private static final float LIFT_RISE_DURATION = 40f;
    private static final float RING_PIVOT_Y = 24.0F / 16.0F;
    private static final float RING_RETRACT_DURATION = 10f;
    private static final float RING_APPEAR_DELAY = 5f;
    private static final float RING_APPEAR_TIME = LIFT_RISE_DURATION + RING_APPEAR_DELAY;
    private static final float SPIN_RAMP_FRACTION = 0.35f;

    private static final Identifier LIGHTNING_TEXTURE = Identifier.fromNamespaceAndPath("resonantinstruments", "textures/entity/ritual_lightning.png");
    private static final Identifier RINGS_TEXTURE = Identifier.fromNamespaceAndPath("resonantinstruments", "textures/entity/ritual_rings.png");

    private final ItemModelResolver itemModelResolver;
    private final RitualRings ringsModel;

    public ResonanceChamberRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.ringsModel = new RitualRings(context.bakeLayer(RitualRings.LAYER_LOCATION));
    }

    public static class ResonanceChamberRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemState = new ItemStackRenderState();
        public boolean hasItem;
        public float partialTick;
        public ResonanceChamberBlockEntity.Phase phase;
        public float elapsed;
        public float lift;
        public float spinElapsed;
        public float ringSpinElapsed;
        public final List<BlockPos> activeNodes = new ArrayList<>();
        public float lightningProgress = 0.0f;
        public BlockPos chamberPos;
    }

    @Override
    public ResonanceChamberRenderState createRenderState() {
        return new ResonanceChamberRenderState();
    }

    private static float smoothstepIntegral(float x) {
        x = Math.max(0f, Math.min(1f, x));
        return x * x * x - (x * x * x * x) / 2f;
    }

    @Override
    public void extractRenderState(ResonanceChamberBlockEntity blockEntity, ResonanceChamberRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);

        ItemStack stack = blockEntity.getStoredItem();
        state.hasItem = !stack.isEmpty();
        state.partialTick = partialTick;

        long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
        state.phase = blockEntity.getPhase();
        state.elapsed = (gameTime - blockEntity.getPhaseStartTime()) + partialTick;
        state.activeNodes.clear();
        state.activeNodes.addAll(blockEntity.getActiveNodes());
        state.chamberPos = blockEntity.getBlockPos();

        switch (state.phase) {
            case RITUAL -> {
                float t = Math.min(1.0f, state.elapsed / LIFT_RISE_DURATION);
                state.lift = t * MAX_LIFT;
            }
            case HOLD -> state.lift = MAX_LIFT;
            case DESCEND -> {
                float fallElapsed = Math.max(0f, state.elapsed - RING_RETRACT_DURATION);
                float fallDuration = Math.max(1f, ResonanceChamberBlockEntity.getDescendDuration() - RING_RETRACT_DURATION);
                float t = Math.min(1.0f, fallElapsed / fallDuration);
                state.lift = (1.0f - t) * MAX_LIFT;
            }
            default -> state.lift = 0.0f;
        }

        if (state.phase == ResonanceChamberBlockEntity.Phase.GATHER) {
            state.lightningProgress = Math.min(1.0f, state.elapsed / ResonanceChamberBlockEntity.GATHER_DURATION);
        } else if (state.phase == ResonanceChamberBlockEntity.Phase.RITUAL || state.phase == ResonanceChamberBlockEntity.Phase.HOLD) {
            state.lightningProgress = 1.0f;
        } else {
            state.lightningProgress = 0.0f;
        }

        float rotationDuration = (float) Math.sin(0);
        float ritualDuration = ResonanceChamberBlockEntity.getRitualDuration();
        float holdDuration = ResonanceChamberBlockEntity.getHoldDuration();

        switch (state.phase) {
            case RITUAL -> state.spinElapsed = SPIN_BASE_SPEED * state.elapsed;
            case HOLD, DESCEND -> state.spinElapsed = SPIN_BASE_SPEED * (ritualDuration + state.elapsed);
            default -> state.spinElapsed = (gameTime + partialTick) * SPIN_BASE_SPEED;
        }

        float ringSpinWindow = Math.max(1f, ritualDuration - RING_APPEAR_TIME) + holdDuration;
        float ringRampDur = ringSpinWindow * SPIN_RAMP_FRACTION;
        float ringHoldDur = Math.max(0f, ringSpinWindow - (2f * ringRampDur));

        float angleAtRampUpEnd = PEAK_SPIN_SPEED * ringRampDur * smoothstepIntegral(1f);
        float angleAtHoldEnd = angleAtRampUpEnd + PEAK_SPIN_SPEED * ringHoldDur;
        float rampDownIntegralFull = 1f - smoothstepIntegral(1f);
        float angleAtRampDownEnd = angleAtHoldEnd + PEAK_SPIN_SPEED * ringRampDur * rampDownIntegralFull;

        float ringElapsed;
        if (state.phase == ResonanceChamberBlockEntity.Phase.RITUAL) {
            ringElapsed = Math.max(0f, state.elapsed - RING_APPEAR_TIME);
        } else if (state.phase == ResonanceChamberBlockEntity.Phase.HOLD) {
            ringElapsed = Math.max(0f, (ritualDuration - RING_APPEAR_TIME)) + state.elapsed;
        } else {
            ringElapsed = ringSpinWindow;
        }

        if (ringElapsed < ringRampDur) {
            float x = ringElapsed / ringRampDur;
            state.ringSpinElapsed = PEAK_SPIN_SPEED * ringRampDur * smoothstepIntegral(x);
        } else if (ringElapsed < ringRampDur + ringHoldDur) {
            state.ringSpinElapsed = angleAtRampUpEnd + PEAK_SPIN_SPEED * (ringElapsed - ringRampDur);
        } else {
            float t2 = ringElapsed - ringRampDur - ringHoldDur;
            float x2 = Math.min(1f, t2 / ringRampDur);
            float rampDownIntegral = x2 - smoothstepIntegral(x2);
            state.ringSpinElapsed = angleAtHoldEnd + PEAK_SPIN_SPEED * ringRampDur * rampDownIntegral;
        }

        if (state.hasItem) {
            this.itemModelResolver.updateForTopItem(state.itemState, stack, ItemDisplayContext.FIXED,
                    blockEntity.getLevel(), null, (int) blockEntity.getBlockPos().asLong());
        } else {
            state.itemState.clear();
        }
    }

    @Override
    public void submit(ResonanceChamberRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (state.lightningProgress > 0.0f && !state.activeNodes.isEmpty() && state.chamberPos != null) {
            float startDist = 2.75f;
            float currentEdge = startDist - (startDist * state.lightningProgress);

            for (BlockPos nodePos : state.activeNodes) {
                poseStack.pushPose();

                poseStack.translate(0.5D, 0.01D, 0.5D);

                int dx = nodePos.getX() - state.chamberPos.getX();
                int dz = nodePos.getZ() - state.chamberPos.getZ();

                if (dx > 0) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(0));
                } else if (dx < 0) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(180));
                } else if (dz > 0) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(270));
                } else if (dz < 0) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                }

                submitNodeCollector.submitCustomGeometry(
                        poseStack,
                        RenderTypes.entityTranslucentEmissive(LIGHTNING_TEXTURE),
                        (PoseStack.Pose pose, VertexConsumer buffer) -> {
                            buffer.addVertex(pose.pose(), startDist, 0f, -0.5f).setColor(-1).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0f, 1f, 0f);
                            buffer.addVertex(pose.pose(), currentEdge, 0f, -0.5f).setColor(-1).setUv(state.lightningProgress, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0f, 1f, 0f);
                            buffer.addVertex(pose.pose(), currentEdge, 0f, 0.5f).setColor(-1).setUv(state.lightningProgress, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0f, 1f, 0f);
                            buffer.addVertex(pose.pose(), startDist, 0f, 0.5f).setColor(-1).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0f, 1f, 0f);
                        });

                poseStack.popPose();
            }
        }

        if (!state.hasItem || state.itemState.isEmpty()) return;

        boolean showRings = state.phase == ResonanceChamberBlockEntity.Phase.RITUAL ||
                state.phase == ResonanceChamberBlockEntity.Phase.HOLD;

        if (showRings) {
            boolean ringsVisible;

            if (state.phase == ResonanceChamberBlockEntity.Phase.RITUAL) {
                ringsVisible = state.elapsed >= RING_APPEAR_TIME;
            } else {
                ringsVisible = true;
            }

            if (ringsVisible) {
                final double RING_ANCHOR_Y = 1.15D + MAX_LIFT;

                poseStack.pushPose();
                poseStack.translate(0.5D, RING_ANCHOR_Y, 0.5D);
                try {
                    poseStack.pushPose();
                    poseStack.translate(0.0D, -2.35D, 0.0D);
                    poseStack.scale(1.6f, 1.6f, 1.6f);
                    ringsModel.getRing().resetPose();
                    ringsModel.getRing().yRot = (float) Math.toRadians(state.ringSpinElapsed);
                    submitNodeCollector.submitModelPart(
                            ringsModel.getRing(), poseStack,
                            RenderTypes.entityCutout(RINGS_TEXTURE),
                            15728880, OverlayTexture.NO_OVERLAY, null);
                    poseStack.popPose();

                    poseStack.pushPose();
                    poseStack.translate(0.0D, -2.0D, 0.0D);
                    poseStack.scale(1.4f, 1.4f, 1.4f);
                    poseStack.translate(0.0, RING_PIVOT_Y, 0.0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                    poseStack.translate(0.0, -RING_PIVOT_Y, 0.0);
                    ringsModel.getRing().resetPose();
                    ringsModel.getRing().yRot = (float) Math.toRadians(state.ringSpinElapsed * 1.25f);
                    submitNodeCollector.submitModelPart(
                            ringsModel.getRing(), poseStack,
                            RenderTypes.entityCutout(RINGS_TEXTURE),
                            15728880, OverlayTexture.NO_OVERLAY, null);
                    poseStack.popPose();

                    poseStack.pushPose();
                    poseStack.translate(0.0D, -1.65D, 0.0D);
                    poseStack.scale(1.2f, 1.2f, 1.2f);
                    poseStack.translate(0.0, RING_PIVOT_Y, 0.0);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                    poseStack.translate(0.0, -RING_PIVOT_Y, 0.0);
                    ringsModel.getRing().resetPose();
                    ringsModel.getRing().yRot = (float) Math.toRadians(-state.ringSpinElapsed * 1.75f);
                    submitNodeCollector.submitModelPart(
                            ringsModel.getRing(), poseStack,
                            RenderTypes.entityCutout(RINGS_TEXTURE),
                            15728880, OverlayTexture.NO_OVERLAY, null);
                    poseStack.popPose();
                } finally {
                    poseStack.popPose();
                }
            }
        }

        poseStack.pushPose();
        try {
            poseStack.translate(0.5D, 1.15D + state.lift, 0.5D);

            float bobOffset = (float) Math.sin(state.spinElapsed * 0.025f) * 0.04f;
            poseStack.translate(0.0, bobOffset, 0.0);

            float rotationDegrees = state.spinElapsed % 360f;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));

            float scale = 0.5f;
            poseStack.scale(scale, scale, scale);

            AABB box = state.itemState.getModelBoundingBox();
            if (box != null) {
                double offsetX = -(box.maxX + box.minX) / 2.0;
                double offsetY = -(box.maxY + box.minY) / 2.0;
                double offsetZ = -(box.maxZ + box.minZ) / 2.0;
                poseStack.translate(offsetX, offsetY, offsetZ);
            }

            state.itemState.submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
        } finally {
            poseStack.popPose();
        }
    }
}