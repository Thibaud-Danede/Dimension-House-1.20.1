package net.ravadael.dimensionhouse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

import java.util.function.Supplier;

public class TeleportToHousePacket {

    // Pas de données à envoyer : paquet "vide"
    public TeleportToHousePacket() {}

    public static void encode(TeleportToHousePacket msg, FriendlyByteBuf buf) {
        // rien à encoder
    }

    public static TeleportToHousePacket decode(FriendlyByteBuf buf) {
        // paquet sans données -> on retourne juste une nouvelle instance
        return new TeleportToHousePacket();
    }

    public static void handle(TeleportToHousePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                HouseTeleporter.teleportToHouse(player);
            }
        });

        context.setPacketHandled(true);
    }
}
