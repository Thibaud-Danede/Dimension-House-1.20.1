package net.ravadael.dimensionhouse.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ravadael.dimensionhouse.DimensionHouse;
import net.ravadael.dimensionhouse.client.render.HousePortalBER;
import net.ravadael.dimensionhouse.client.render.RiftRenderer;
import net.ravadael.dimensionhouse.blockentity.ModBlockEntities;
import net.ravadael.dimensionhouse.entity.ModEntities;

@Mod.EventBusSubscriber(modid = DimensionHouse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Entité faille
        event.registerEntityRenderer(ModEntities.RIFT.get(), RiftRenderer::new);

        // ✅ BlockEntity renderer du portail bloc
        event.registerBlockEntityRenderer(
                ModBlockEntities.HOUSE_PORTAL_BE.get(),
                ctx -> new HousePortalBER()
        );
    }
}
