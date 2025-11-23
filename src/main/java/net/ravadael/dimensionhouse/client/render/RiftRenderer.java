package net.ravadael.dimensionhouse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ravadael.dimensionhouse.DimensionHouse;
import net.ravadael.dimensionhouse.entity.RiftEntity;

public class RiftRenderer extends EntityRenderer<RiftEntity> {

    // ========= Textures gardées =========

    // Sprite animée du portail (160x32 = 10 frames de 16x32)
    private static final ResourceLocation PORTAL_TEX =
            new ResourceLocation(DimensionHouse.MOD_ID, "textures/entity/portal.png");
    private static final int PORTAL_FRAMES = 10;
    private static final int PORTAL_FRAME_TICKS = 3;      // vitesse anim

    // 3 frames de cassure
    private static final ResourceLocation CRACK0_TEX =
            new ResourceLocation(DimensionHouse.MOD_ID, "textures/entity/destroy_stage_0.png");
    private static final ResourceLocation CRACK2_TEX =
            new ResourceLocation(DimensionHouse.MOD_ID, "textures/entity/destroy_stage_2.png");
    private static final ResourceLocation CRACK4_TEX =
            new ResourceLocation(DimensionHouse.MOD_ID, "textures/entity/destroy_stage_4.png");

    private static final ResourceLocation[] CRACK_FRAMES = {
            CRACK0_TEX, CRACK2_TEX, CRACK4_TEX
    };

    // ========= Timing =========

    private static final int LIFETIME_TICKS = 20 * 30; // 30s

    private static final float STAR_END = 0.07f; // fin de la cassure
    private static final float OPEN_END = 0.16f; // ouverture complète
    private static final float CLOSE_START = 0.88f;
    private static final float CLOSE_DUR = 0.10f;

    // ========= Tailles simples (pas de forme spéciale) =========
    private static final float PORTAL_HALF_W = 0.4125f;  // largeur/2 du quad portail
    private static final float PORTAL_HALF_H = 1.0125f;  // hauteur/2 du quad portail

    private static final float CRACK_HALF_SIZE = 0.85f; // quad carré cassure

    public RiftRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RiftEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // centre vertical
        poseStack.translate(0.0, entity.getBbHeight() / 2.0, 0.0);

        // orientation yaw réel
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));

        float time = entity.tickCount + partialTicks;
        float life = clamp01(time / LIFETIME_TICKS);

        PoseStack.Pose last = poseStack.last();
        int overlay = OverlayTexture.NO_OVERLAY;

        // =========================
        // PHASE 0 : CASSURE (destroy_stage)
        // =========================
        if (life < STAR_END) {
            float t = clamp01(life / STAR_END);

            int idx = Math.min((int)(t * CRACK_FRAMES.length), CRACK_FRAMES.length - 1);

            int fullBright = 0xF000F0;
            VertexConsumer crackVc = buffer.getBuffer(RenderType.entityTranslucent(CRACK_FRAMES[idx]));

            renderQuad(crackVc, last,
                    CRACK_HALF_SIZE, CRACK_HALF_SIZE,
                    fullBright, overlay,
                    1.0f, 0.03f);

            poseStack.popPose();
            return;
        }

        // =========================
        // PHASE 1 : PORTAIL (portal.png)
        // =========================

        float openT = clamp01((life - STAR_END) / (OPEN_END - STAR_END));
        float open = easeOutCubic(openT);

        float closeT = clamp01((life - CLOSE_START) / CLOSE_DUR);
        float close = 1.0f - easeInCubic(closeT);

        float aperture = clamp01(open * close);
        if (aperture < 0.005f) {
            poseStack.popPose();
            return;
        }

        float scale = 2.0f * aperture;
        poseStack.scale(scale, scale, scale);

        last = poseStack.last();

        int fullBright = 0xF000F0;

        // ✅ Animation inversée
        int baseFrame = ((int)(time / PORTAL_FRAME_TICKS)) % PORTAL_FRAMES;
        int frame = (PORTAL_FRAMES - 1) - baseFrame;

        float uMin = frame / (float) PORTAL_FRAMES;
        float uMax = (frame + 1) / (float) PORTAL_FRAMES;

        VertexConsumer portalVc = buffer.getBuffer(RenderType.entityTranslucent(PORTAL_TEX));

        renderQuadFrame(portalVc, last,
                PORTAL_HALF_W, PORTAL_HALF_H,
                uMin, uMax, 0f, 1f,
                fullBright, overlay,
                1.0f, 0.0f);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    // ============================================================
    // Quad plein UV 0..1
    // ============================================================
    private static void renderQuad(VertexConsumer vc, PoseStack.Pose last,
                                   float rx, float ry,
                                   int packedLight, int overlay,
                                   float alpha, float zOffset) {

        int a = (int)(alpha * 255);
        int r = 255, g = 255, b = 255;

        float x0 = -rx, x1 = rx;
        float y0 = -ry, y1 = ry;

        vc.vertex(last.pose(), x0, y0, zOffset).color(r,g,b,a).uv(0, 1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
        vc.vertex(last.pose(), x1, y0, zOffset).color(r,g,b,a).uv(1, 1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
        vc.vertex(last.pose(), x1, y1, zOffset).color(r,g,b,a).uv(1, 0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
        vc.vertex(last.pose(), x0, y1, zOffset).color(r,g,b,a).uv(0, 0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
    }

    // ============================================================
    // Quad avec UV custom (frame sprite sheet)
    // ============================================================
    private static void renderQuadFrame(VertexConsumer vc, PoseStack.Pose last,
                                        float rx, float ry,
                                        float u0, float u1, float v0, float v1,
                                        int packedLight, int overlay,
                                        float alpha, float zOffset) {

        int a = (int)(alpha * 255);
        int r = 255, g = 255, b = 255;

        float x0 = -rx, x1 = rx;
        float y0 = -ry, y1 = ry;

        vc.vertex(last.pose(), x0, y0, zOffset).color(r,g,b,a).uv(u0, v1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
        vc.vertex(last.pose(), x1, y0, zOffset).color(r,g,b,a).uv(u1, v1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
        vc.vertex(last.pose(), x1, y1, zOffset).color(r,g,b,a).uv(u1, v0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
        vc.vertex(last.pose(), x0, y1, zOffset).color(r,g,b,a).uv(u0, v0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), 0,0,1).endVertex();
    }

    // ============================================================
    // Easing simple
    // ============================================================
    private static float easeOutCubic(float t) {
        float u = 1f - t;
        return 1f - u*u*u;
    }

    private static float easeInCubic(float t) {
        return t*t*t;
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    @Override
    public ResourceLocation getTextureLocation(RiftEntity entity) {
        return PORTAL_TEX;
    }
}
