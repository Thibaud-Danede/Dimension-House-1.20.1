package net.ravadael.dimensionhouse.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ravadael.dimensionhouse.entity.ModEntities;
import net.ravadael.dimensionhouse.entity.RiftEntity;

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

        double savedX = nbt.getDouble(NBT_RETURN_X);
        double savedY = nbt.getDouble(NBT_RETURN_Y);
        double savedZ = nbt.getDouble(NBT_RETURN_Z);
        float yaw = nbt.getFloat(NBT_RETURN_YAW);
        float pitch = nbt.getFloat(NBT_RETURN_PITCH);

        BlockPos portalBase = BlockPos.containing(savedX, savedY, savedZ);

        // Position de la faille au point retour
        Vec3 portalPos = Vec3.atBottomCenterOf(portalBase);

        // Position safe à 6–12 blocs de distance
        Vec3 safePos = findNearbySafePos(targetWorld, portalBase, 2, 2);

        // Yaw du joueur = dos à la faille
        float yawAway = yawAwayFromPortal(safePos, portalPos);

        // Pitch tu peux garder 0 (horizontale) ou reprendre celui sauvegardé
        float pitchOut = 0.0F;

        // Téléport du joueur
        player.teleportTo(targetWorld, safePos.x, safePos.y, safePos.z, yawAway, pitchOut);

        // Spawn de la faille de retour (30s), sauf si déjà là
        spawnReturnRiftIfAbsent(targetWorld, portalPos);

    }

    private static Vec3 findNearbySafePos(ServerLevel level, BlockPos base, int minRadius, int maxRadius) {

        // On commence à minRadius pour forcer une distance minimale à la faille
        for (int r = minRadius; r <= maxRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {

                    // On ne garde que "l'anneau" à distance r (pas l'intérieur),
                    // sinon on retombe sur des positions trop proches
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    BlockPos candidate = base.offset(dx, 0, dz);

                    // on teste quelques hauteurs autour du Y
                    for (int dy = 0; dy <= 3; dy++) {
                        BlockPos feet = candidate.above(dy);
                        if (isStandable(level, feet)) {
                            return Vec3.atBottomCenterOf(feet);
                        }
                    }
                }
            }
        }

        return Vec3.atBottomCenterOf(base); // fallback
    }

    private static boolean isStandable(ServerLevel level, BlockPos feet) {
        BlockState below = level.getBlockState(feet.below());
        if (!below.isSolid()) return false; // il faut un sol

        // deux blocs d'air pour tenir debout
        if (!level.isEmptyBlock(feet)) return false;
        if (!level.isEmptyBlock(feet.above())) return false;

        return true;
    }

    private static void spawnReturnRiftIfAbsent(ServerLevel level, Vec3 portalPos) {
        // évite de spammer plusieurs failles si on revient plusieurs fois
        AABB box = new AABB(portalPos, portalPos).inflate(1.5);
        boolean already = !level.getEntitiesOfClass(RiftEntity.class, box).isEmpty();
        if (already) return;

        RiftEntity rift = new RiftEntity(ModEntities.RIFT.get(), level);
        rift.setPos(portalPos.x, portalPos.y, portalPos.z);
        level.addFreshEntity(rift);
    }

    private static float yawAwayFromPortal(Vec3 playerPos, Vec3 portalPos) {
        double dx = portalPos.x - playerPos.x;
        double dz = portalPos.z - playerPos.z;

        // yaw qui regarde VERS le portail
        float yawToPortal = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90F);

        // yaw qui regarde A L'OPPOSE du portail
        float yawAway = yawToPortal + 180F;

        // normalisation optionnelle dans [-180; 180]
        while (yawAway > 180F) yawAway -= 360F;
        while (yawAway < -180F) yawAway += 360F;

        return yawAway;
    }
}
