package net.ravadael.dimensionhouse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

import java.util.function.Supplier;

public class TeleportToHousePacket {
    public static void encode(TeleportToHousePacket msg, FriendlyByteBuf buf) {}
    public static TeleportToHousePacket decode(FriendlyByteBuf buf) {
        return new TeleportToHousePacket();
    }

    public static void handle(TeleportToHousePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                HouseTeleporter.teleportToHouse(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
