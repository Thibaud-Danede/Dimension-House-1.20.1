package net.ravadael.dimensionhouse.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.ravadael.dimensionhouse.block.HouseReturnPortalBlock;
import net.ravadael.dimensionhouse.blockentity.HousePortalBlockEntity;

public class HousePortalBER implements BlockEntityRenderer<HousePortalBlockEntity> {

    @Override
    public void render(HousePortalBlockEntity be, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();

        Direction facing = be.getBlockState().getValue(HouseReturnPortalBlock.FACING);

        // On centre pour tourner proprement
        poseStack.translate(0.5, 0.5, 0.5);

        // Rotation selon la face
        if (facing == Direction.UP) {
            // portail horizontal au sol (normal vers le haut)
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
        } else if (facing == Direction.DOWN) {
            // portail au plafond (normal vers le bas)
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        } else {
            // portail vertical sur mur
            // quad de base fait face au SUD (normal +Z)
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        }

        // Revenir dans coords bloc
        poseStack.translate(-0.5, -0.5, -0.5);

        VertexConsumer vc = buffer.getBuffer(RenderType.endPortal());
        PoseStack.Pose last = poseStack.last();
        int overlay = OverlayTexture.NO_OVERLAY;

        // Quad de base sur z = 0.5 (plan N/S), double face
        float min = 0.0f;
        float max = 1.0f;
        float z = 1.0f;

        // Face avant (normal +Z)
        float nx = 0f, ny = 0f, nz = 1f;

        vc.vertex(last.pose(), min, min, z).color(255,255,255,255).uv(0,1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();
        vc.vertex(last.pose(), max, min, z).color(255,255,255,255).uv(1,1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();
        vc.vertex(last.pose(), max, max, z).color(255,255,255,255).uv(1,0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();
        vc.vertex(last.pose(), min, max, z).color(255,255,255,255).uv(0,0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();

        // Face arrière (normal -Z)
        nz = -1f;
        vc.vertex(last.pose(), min, max, z).color(255,255,255,255).uv(0,0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();
        vc.vertex(last.pose(), max, max, z).color(255,255,255,255).uv(1,0)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();
        vc.vertex(last.pose(), max, min, z).color(255,255,255,255).uv(1,1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();
        vc.vertex(last.pose(), min, min, z).color(255,255,255,255).uv(0,1)
                .overlayCoords(overlay).uv2(packedLight).normal(last.normal(), nx, ny, nz).endVertex();

        poseStack.popPose();
    }
}
