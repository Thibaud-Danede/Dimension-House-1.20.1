package net.ravadael.dimensionhouse.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.ravadael.dimensionhouse.DimensionHouse;

public class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DimensionHouse.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                0, // Packet ID
                TeleportToHousePacket.class,
                TeleportToHousePacket::encode,
                TeleportToHousePacket::decode,
                TeleportToHousePacket::handle
        );
    }
}

