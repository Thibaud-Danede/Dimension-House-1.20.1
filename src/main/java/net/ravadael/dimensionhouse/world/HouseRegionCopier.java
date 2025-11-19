package net.ravadael.dimensionhouse.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.ravadael.dimensionhouse.DimensionHouse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;

public class HouseRegionCopier {

    // Liste des fichiers region embarqués dans le jar
    // 🠒 A TOI de remplir avec les bons noms de fichiers
    private static final String[] REGION_FILES = {
            "r.-1.-1.mca",
            "r.-1.0.mca",
            "r.0.-1.mca",
            "r.0.0.mca"
    };

    public static void ensureRegionsPresent(ServerLevel level) {
        // Sûreté : on ne fait ça que pour NOTRE dimension
        if (!level.dimension().equals(HouseTeleporter.HOUSE_DIMENSION)) {
            return;
        }

        // Racine du monde (ex: .minecraft/saves/MonMonde/)
        var server = level.getServer();

        // Racine du monde : .../saves/<monde>/
        Path worldRoot = server.getWorldPath(LevelResource.ROOT);

        // Infos sur TA dimension : namespace + path
        var dimLoc = HouseTeleporter.HOUSE_DIMENSION.location();

        // Dossier .../saves/<monde>/dimensions/<namespace>/<path>/region
        Path regionDir = worldRoot
                .resolve("dimensions")
                .resolve(dimLoc.getNamespace())
                .resolve(dimLoc.getPath())
                .resolve("region");


        try {
            Files.createDirectories(regionDir);
        } catch (IOException e) {
            DimensionHouse.LOGGER.error("[DimensionHouse] Impossible de créer le dossier region : " + regionDir, e);
            return;
        }

        // Si des .mca existent déjà, on considère que la dimension est déjà initialisée
        boolean hasMca = false;
        try {
            if (Files.exists(regionDir)) {
                try (var stream = Files.list(regionDir)) {
                    hasMca = stream.anyMatch(p -> p.getFileName().toString().endsWith(".mca"));
                }
            }
        } catch (IOException e) {
            DimensionHouse.LOGGER.error("[DimensionHouse] Erreur en listant le dossier region : " + regionDir, e);
        }

        if (hasMca) {
            DimensionHouse.LOGGER.info("[DimensionHouse] Région déjà initialisée, pas de copie.");
            return;
        }

        DimensionHouse.LOGGER.info("[DimensionHouse] Copie des fichiers region depuis le jar vers : " + regionDir);

        ClassLoader cl = HouseRegionCopier.class.getClassLoader();

        for (String fileName : REGION_FILES) {
            String resourcePath = "assets/" + DimensionHouse.MOD_ID + "/regions/" + fileName;
            Path targetPath = regionDir.resolve(fileName);

            try (InputStream in = cl.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    DimensionHouse.LOGGER.error("[DimensionHouse] Ressource introuvable dans le jar : " + resourcePath);
                    continue;
                }

                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                DimensionHouse.LOGGER.info("[DimensionHouse] Copié " + resourcePath + " -> " + targetPath);
            } catch (IOException e) {
                DimensionHouse.LOGGER.error("[DimensionHouse] Erreur en copiant " + resourcePath, e);
            }
        }

        DimensionHouse.LOGGER.info("[DimensionHouse] Copie des régions terminée. Fichiers : " + Arrays.toString(REGION_FILES));
    }
}
