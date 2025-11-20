package net.ravadael.dimensionhouse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.ravadael.dimensionhouse.DimensionHouse;
import net.ravadael.dimensionhouse.entity.RiftEntity;

public class RiftRenderer extends EntityRenderer<RiftEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(DimensionHouse.MOD_ID, "textures/entity/rift.png");

    public RiftRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RiftEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        poseStack.translate(0.0, entity.getBbHeight() / 2.0, 0.0);

        // Billboard vers la caméra
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());

        float scale = 2.0F;
        poseStack.scale(scale, scale, scale);

        // ✅ RenderType End Portal (shader vanilla)
        VertexConsumer vc = buffer.getBuffer(RenderType.endPortal());
        PoseStack.Pose last = poseStack.last();

        float half = 0.5F;
        int overlay = OverlayTexture.NO_OVERLAY;
        float nx = 0f, ny = 0f, nz = 1f;

        // Quad double-face (on le dessine deux fois, une normale, une inversée)
        putQuad(vc, last, half, packedLight, overlay, nx, ny, nz);
        putQuadFlipped(vc, last, half, packedLight, overlay, -nx, -ny, -nz);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void putQuad(VertexConsumer vc, PoseStack.Pose last, float half,
                                int packedLight, int overlay, float nx, float ny, float nz) {

        vc.vertex(last.pose(), -half, -half, 0)
                .color(255, 255, 255, 255)
                .uv(0, 1)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();

        vc.vertex(last.pose(),  half, -half, 0)
                .color(255, 255, 255, 255)
                .uv(1, 1)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();

        vc.vertex(last.pose(),  half,  half, 0)
                .color(255, 255, 255, 255)
                .uv(1, 0)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();

        vc.vertex(last.pose(), -half,  half, 0)
                .color(255, 255, 255, 255)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();
    }

    private static void putQuadFlipped(VertexConsumer vc, PoseStack.Pose last, float half,
                                       int packedLight, int overlay, float nx, float ny, float nz) {
        // même quad mais ordre inversé pour voir l’arrière
        vc.vertex(last.pose(), -half,  half, 0)
                .color(255, 255, 255, 255)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();

        vc.vertex(last.pose(),  half,  half, 0)
                .color(255, 255, 255, 255)
                .uv(1, 0)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();

        vc.vertex(last.pose(),  half, -half, 0)
                .color(255, 255, 255, 255)
                .uv(1, 1)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();

        vc.vertex(last.pose(), -half, -half, 0)
                .color(255, 255, 255, 255)
                .uv(0, 1)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(last.normal(), nx, ny, nz)
                .endVertex();
    }



    @Override
    public ResourceLocation getTextureLocation(RiftEntity entity) {
        return TEXTURE;
    }
}
