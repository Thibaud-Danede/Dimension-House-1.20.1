package net.ravadael.dimensionhouse.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.ravadael.dimensionhouse.DimensionHouse;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

import java.util.function.Supplier;

public class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DimensionHouse.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(0, TeleportToHousePacket.class,
                TeleportToHousePacket::encode,
                TeleportToHousePacket::decode,
                TeleportToHousePacket::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }
}
