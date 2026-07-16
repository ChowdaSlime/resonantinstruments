package net.chowdaslime.resonantinstruments.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;

public class RitualRings {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("resonantinstruments", "ritualrings"), "main"
    );

    private final ModelPart ring;

    public RitualRings(ModelPart root) {
        this.ring = root.getChild("ring");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition ring = partdefinition.addOrReplaceChild("ring", CubeListBuilder.create()
                        .texOffs(16, 16).addBox(-1.0F, -1.5913F, -8.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(15, 16).addBox(-1.0F, -1.5913F, 7.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 16).addBox(-1.0F, 7.0F, -1.5913F, 2.0F, 1.0F, 3.1826F, new CubeDeformation(0.0F))
                        .texOffs(11, 9).addBox(-1.0F, -8.0F, -1.5913F, 2.0F, 1.0F, 3.1826F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ring.addOrReplaceChild("hexadecagon_r1", CubeListBuilder.create()
                        .texOffs(11, 10).addBox(-2.0F, -8.0F, -1.5913F, 2.0F, 1.0F, 3.1826F, new CubeDeformation(0.0F))
                        .texOffs(18, 20).addBox(-2.0F, 7.0F, -1.5913F, 2.0F, 1.0F, 3.1826F, new CubeDeformation(0.0F))
                        .texOffs(18, 15).addBox(-2.0F, -1.5913F, 7.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 14).addBox(-2.0F, -1.5913F, -8.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ring.addOrReplaceChild("hexadecagon_r2", CubeListBuilder.create()
                        .texOffs(11, 7).addBox(-2.0F, -8.0F, -1.5913F, 2.0F, 1.0F, 3.1826F, new CubeDeformation(0.0F))
                        .texOffs(18, 18).addBox(-2.0F, 7.0F, -1.5913F, 2.0F, 1.0F, 3.1826F, new CubeDeformation(0.0F))
                        .texOffs(13, 10).addBox(-2.0F, -1.5913F, 7.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 19).addBox(-2.0F, -1.5913F, -8.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ring.addOrReplaceChild("hexadecagon_r3", CubeListBuilder.create()
                        .texOffs(19, 18).addBox(-2.0F, -1.5913F, 7.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(15, 8).addBox(-2.0F, -1.5913F, -8.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ring.addOrReplaceChild("hexadecagon_r4", CubeListBuilder.create()
                        .texOffs(13, 6).addBox(-2.0F, -1.5913F, 7.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(18, 19).addBox(-2.0F, -1.5913F, -8.0F, 2.0F, 3.1826F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        ring.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public ModelPart getRing() {
        return this.ring;
    }
}