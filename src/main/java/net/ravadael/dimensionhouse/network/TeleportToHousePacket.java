package net.ravadael.dimensionhouse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
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

        // ✅ Coordonnées bloc
        int bx = Mth.floor(pos.x);
        int bz = Mth.floor(pos.z);

        // ✅ Y sûr = surface solide la plus haute (anti-enfoncement)
        int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
        double safeY = groundY + 0.75; // petit offset au-dessus du sol

        RiftEntity rift = new RiftEntity(ModEntities.RIFT.get(), level);
        rift.setPos(pos.x, safeY, pos.z);

        // ✅ La faille fait face au joueur qui la crée
        // +180° pour que la "face avant" regarde le joueur
        rift.setYRot(player.getYRot() + 180f);
        rift.setXRot(0f);
        rift.yRotO = rift.getYRot(); // évite un snap client au 1er tick

        level.addFreshEntity(rift);
    }
}
