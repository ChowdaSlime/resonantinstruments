package net.chowdaslime.resonantinstruments.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.chowdaslime.resonantinstruments.entity.ChronosAcceleratorEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class ChronosAcceleratorRenderer extends EntityRenderer<ChronosAcceleratorEntity, ChronosAcceleratorRenderer.ChronosRenderState> {

    public ChronosAcceleratorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(ChronosAcceleratorEntity entity, Frustum culler, double camX, double camY, double camZ) {
        return true;
    }

    public static class ChronosRenderState extends EntityRenderState {
        public int multiplier;
        public int ticksRemaining;
    }

    @Override
    public ChronosRenderState createRenderState() {
        return new ChronosRenderState();
    }

    @Override
    public void extractRenderState(ChronosAcceleratorEntity entity, ChronosRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.multiplier = entity.getMultiplier();
        state.ticksRemaining = entity.getTicksRemaining();
    }

    @Override
    public void submit(ChronosRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        if (state.multiplier <= 1) return;

        int multiplier = state.multiplier;
        int ticks = state.ticksRemaining;
        int seconds = ticks / 20;

        String multText = multiplier + "x";
        String timeText = String.format("0:%02d", seconds);

        Font font = this.getFont();
        float multWidth = font.width(multText);
        float timeWidth = font.width(timeText);

        int fullBright = 15728880;
        int amethystColor = 0xFFAA55FF;
        int goldColor = 0xFFFFAA00;
        float scale = 0.015F;

        Direction[] faces = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};

        for (Direction face : faces) {
            poseStack.pushPose();

            if (face == Direction.UP) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            } else {
                poseStack.mulPose(Axis.YP.rotationDegrees(-face.toYRot()));
            }

            poseStack.translate(0.0D, 0.0D, 0.51D);
            poseStack.scale(scale, -scale, scale);

            submitNodeCollector.submitText(
                    poseStack, -multWidth / 2.0f, -10.0f,
                    Component.literal(multText).getVisualOrderText(),
                    false, Font.DisplayMode.NORMAL, fullBright, amethystColor, 0, 0
            );

            submitNodeCollector.submitText(
                    poseStack, -timeWidth / 2.0f, 5.0f,
                    Component.literal(timeText).getVisualOrderText(),
                    false, Font.DisplayMode.NORMAL, fullBright, goldColor, 0, 0
            );

            poseStack.popPose();
        }
    }
}