package net.ravadael.dimensionhouse.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ravadael.dimensionhouse.network.ModPackets;
import net.ravadael.dimensionhouse.network.TeleportToHousePacket;

@Mod.EventBusSubscriber(modid = "dimensionhouse", value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.TELEPORT_KEY.consumeClick()) {
            System.out.println("H key pressed! Sending teleport packet...");
            // Safety: Only send if channel is initialized
            if (ModPackets.CHANNEL != null) {
                ModPackets.CHANNEL.sendToServer(new TeleportToHousePacket());
            }
        }
    }
}
