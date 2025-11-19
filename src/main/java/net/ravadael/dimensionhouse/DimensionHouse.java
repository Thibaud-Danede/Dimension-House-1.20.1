package net.ravadael.dimensionhouse;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ravadael.dimensionhouse.network.ModPackets;
import org.slf4j.Logger;

// IMPORTANT : l’ID doit être le même que dans mods.toml et dans tes jsons
@Mod(DimensionHouse.MOD_ID)
public class DimensionHouse {

    public static final String MOD_ID = "dimensionhouse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DimensionHouse() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // setup côté commun (serveur + client)
        modEventBus.addListener(this::commonSetup);

        // événements Forge (ServerStarting, etc.)
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // On enregistre les paquets réseau ici
        event.enqueueWork(ModPackets::register);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("DimensionHouse: serveur en cours de démarrage");
    }

    // Événements spécifiques côté client, si tu en as besoin plus tard
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // pour l’instant vide
        }
    }
}
