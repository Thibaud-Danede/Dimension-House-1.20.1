package net.ravadael.dimensionhouse.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class HouseTeleporter {
    public static final ResourceKey<Level> HOUSE_DIMENSION = ResourceKey.create(
            Registries.DIMENSION, new ResourceLocation("dimensionhouse", "house"));

    public static void teleportToHouse(ServerPlayer player) {
        ServerLevel targetWorld = player.server.getLevel(HOUSE_DIMENSION);
        if (targetWorld != null) {
            player.teleportTo(targetWorld, 0.5, 100, 0.5, player.getYRot(), player.getXRot());
        }
    }
}
