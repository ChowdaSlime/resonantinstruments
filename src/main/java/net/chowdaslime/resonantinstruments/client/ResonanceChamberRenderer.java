package net.chowdaslime.resonantinstruments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.chowdaslime.resonantinstruments.block.entity.ResonanceChamberBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ResonanceChamberRenderer implements BlockEntityRenderer<ResonanceChamberBlockEntity, ResonanceChamberRenderer.ResonanceChamberRenderState> {

    private final ItemModelResolver itemModelResolver;

    public ResonanceChamberRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class ResonanceChamberRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemState = new ItemStackRenderState();
        public boolean hasItem;
        public long gameTime;
        public float partialTick;
    }

    @Override
    public ResonanceChamberRenderState createRenderState() {
        return new ResonanceChamberRenderState();
    }

    @Override
    public void extractRenderState(ResonanceChamberBlockEntity blockEntity, ResonanceChamberRenderState state,
                                   float partialTick, Vec3 cameraPos,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);

        ItemStack stack = blockEntity.getStoredItem();
        state.hasItem = !stack.isEmpty();
        state.partialTick = partialTick;
        state.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;

        if (state.hasItem) {
            this.itemModelResolver.updateForTopItem(state.itemState, stack, ItemDisplayContext.FIXED,
                    blockEntity.getLevel(), null, (int) blockEntity.getBlockPos().asLong());
        } else {
            state.itemState.clear();
        }
    }

    @Override
    public void submit(ResonanceChamberRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!state.hasItem || state.itemState.isEmpty()) return;

        poseStack.pushPose();
        try {
            poseStack.translate(0.5D, 1.15D, 0.5D);

            float time = state.gameTime + state.partialTick;
            float bobOffset = (float) Math.sin(time * 0.05f) * 0.04f;
            poseStack.translate(0.0, bobOffset, 0.0);

            float rotationDegrees = (time * 2.0f) % 360f;
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