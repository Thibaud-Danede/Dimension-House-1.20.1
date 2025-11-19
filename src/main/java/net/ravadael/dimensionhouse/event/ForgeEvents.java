package net.ravadael.dimensionhouse.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ravadael.dimensionhouse.DimensionHouse;
import net.ravadael.dimensionhouse.world.HouseRegionCopier;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

@Mod.EventBusSubscriber(modid = DimensionHouse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        // Uniquement notre dimension house
        if (!level.dimension().equals(HouseTeleporter.HOUSE_DIMENSION)) {
            return;
        }

        DimensionHouse.LOGGER.info("[DimensionHouse] Chargement de la dimension house, vérification des fichiers region...");
        HouseRegionCopier.ensureRegionsPresent(level);
    }
}
