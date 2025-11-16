package net.ravadael.dimensionhouse.client;

import net.minecraft.client.Minecraft;
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
            ModPackets.sendToServer(new TeleportToHousePacket());
        }
    }
}
