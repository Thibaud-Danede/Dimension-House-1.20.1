package net.ravadael.dimensionhouse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

import java.util.function.Supplier;

public class TeleportToHousePacket {

    public TeleportToHousePacket() {}

    public static void encode(TeleportToHousePacket msg, FriendlyByteBuf buf) {
        // rien à encoder
    }

    public static TeleportToHousePacket decode(FriendlyByteBuf buf) {
        return new TeleportToHousePacket();
    }

    public static void handle(TeleportToHousePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                // → c'est ici que tout se joue
                HouseTeleporter.handleTeleportKey(player);
            }
        });
        context.setPacketHandled(true);
    }
}
