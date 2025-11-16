package net.ravadael.dimensionhouse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.network.NetworkEvent;
import net.ravadael.dimensionhouse.DimensionHouse;

import java.util.function.Supplier;

public class TeleportToHousePacket {
    public TeleportToHousePacket() {} // Constructor

    public static void encode(TeleportToHousePacket msg, FriendlyByteBuf buf) {
        // Nothing to encode, it's empty
    }

    public static TeleportToHousePacket decode(FriendlyByteBuf buf) {
        return new TeleportToHousePacket(); // No data
    }

    public static void handle(TeleportToHousePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ResourceKey<net.minecraft.world.level.Level> houseDimension =
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation(DimensionHouse.MOD_ID, "house"));

            ServerLevel targetWorld = player.server.getLevel(houseDimension);
            if (targetWorld == null) {
                System.out.println("Target dimension not loaded!");
                return;
            }

            System.out.println("Target dimension loaded! Teleporting...");
            BlockPos targetPos = new BlockPos(0, 100, 0);
            player.teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        });

        ctx.get().setPacketHandled(true);
    }
}
