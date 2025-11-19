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

    // NBT keys pour stocker la position de retour
    private static final String NBT_RETURN_DIM = "dimensionhouse:return_dim";
    private static final String NBT_RETURN_X = "dimensionhouse:return_x";
    private static final String NBT_RETURN_Y = "dimensionhouse:return_y";
    private static final String NBT_RETURN_Z = "dimensionhouse:return_z";
    private static final String NBT_RETURN_YAW = "dimensionhouse:return_yaw";
    private static final String NBT_RETURN_PITCH = "dimensionhouse:return_pitch";

    /**
     * Appelé quand le joueur appuie sur H (côté serveur via le packet).
     * Si le joueur est dans la dimension house → retour.
     * Sinon → on enregistre sa position et on l'envoie dans la house.
     */
    public static void handleTeleportKey(ServerPlayer player) {
        if (player.level().dimension().equals(HOUSE_DIMENSION)) {
            teleportBack(player);
        } else {
            teleportToHouseFromCurrent(player);
        }
    }

    /**
     * Sauvegarde la position actuelle du joueur puis le tp dans la dimension house
     * à des coordonnées fixes (toujours les mêmes).
     */
    private static void teleportToHouseFromCurrent(ServerPlayer player) {
        // On enregistre la position actuelle comme point de retour
        var nbt = player.getPersistentData();

        ResourceKey<Level> currentDim = player.level().dimension();
        nbt.putString(NBT_RETURN_DIM, currentDim.location().toString());
        nbt.putDouble(NBT_RETURN_X, player.getX());
        nbt.putDouble(NBT_RETURN_Y, player.getY());
        nbt.putDouble(NBT_RETURN_Z, player.getZ());
        nbt.putFloat(NBT_RETURN_YAW, player.getYRot());
        nbt.putFloat(NBT_RETURN_PITCH, player.getXRot());

        // On téléporte dans la dimension house, à des coordonnées fixes
        ServerLevel targetWorld = player.server.getLevel(HOUSE_DIMENSION);
        if (targetWorld != null) {
            // ICI tes coordonnées fixes dans la dimension house
            double x = -14.552;
            double y = -54;
            double z = -9.573;

            double yaw = -56.1;
            double pitch = -11.7;

            player.teleportTo(targetWorld, x, y, z,(float) yaw,(float) pitch);
        } else {
            System.out.println("House dimension not loaded!");
        }
    }

    /**
     * Téléporte le joueur à la position sauvegardée (en général overworld)
     */
    private static void teleportBack(ServerPlayer player) {
        var nbt = player.getPersistentData();

        if (!nbt.contains(NBT_RETURN_DIM)) {
            System.out.println("No saved return position for player " + player.getGameProfile().getName());
            return;
        }

        String dimId = nbt.getString(NBT_RETURN_DIM);
        ResourceLocation dimLoc = ResourceLocation.tryParse(dimId);
        if (dimLoc == null) {
            System.out.println("Invalid saved dimension id: " + dimId);
            return;
        }

        ResourceKey<Level> targetDim = ResourceKey.create(Registries.DIMENSION, dimLoc);
        ServerLevel targetWorld = player.server.getLevel(targetDim);
        if (targetWorld == null) {
            System.out.println("Target return dimension is not loaded: " + dimId);
            return;
        }

        double x = nbt.getDouble(NBT_RETURN_X);
        double y = nbt.getDouble(NBT_RETURN_Y);
        double z = nbt.getDouble(NBT_RETURN_Z);
        float yaw = nbt.getFloat(NBT_RETURN_YAW);
        float pitch = nbt.getFloat(NBT_RETURN_PITCH);

        player.teleportTo(targetWorld, x, y, z, yaw, pitch);

        // Optionnel : tu peux nettoyer les données après retour si tu veux
        // nbt.remove(NBT_RETURN_DIM);
        // nbt.remove(NBT_RETURN_X);
        // nbt.remove(NBT_RETURN_Y);
        // nbt.remove(NBT_RETURN_Z);
        // nbt.remove(NBT_RETURN_YAW);
        // nbt.remove(NBT_RETURN_PITCH);
    }
}
