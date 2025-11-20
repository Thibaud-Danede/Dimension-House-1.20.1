package net.ravadael.dimensionhouse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.ravadael.dimensionhouse.entity.ModEntities;
import net.ravadael.dimensionhouse.entity.RiftEntity;
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
            if (player == null) return;

            // ✅ Si le joueur est dans la dimension house : H ne fait rien
            if (player.level().dimension().equals(HouseTeleporter.HOUSE_DIMENSION)) {
                return;
            }

            // ✅ Sinon : on spawn la faille
            spawnRift(player);
        });
        context.setPacketHandled(true);
    }

    private static void spawnRift(ServerPlayer player) {
        var level = player.serverLevel();

        // Position : 2 blocs devant le joueur
        var look = player.getLookAngle().scale(2.0);
        var pos = player.position().add(look);

        RiftEntity rift = new RiftEntity(ModEntities.RIFT.get(), level);
        rift.setPos(pos.x, pos.y, pos.z);

        level.addFreshEntity(rift);
    }
}
