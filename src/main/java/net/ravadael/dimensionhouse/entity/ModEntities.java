package net.ravadael.dimensionhouse.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ravadael.dimensionhouse.DimensionHouse;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DimensionHouse.MOD_ID);

    public static final RegistryObject<EntityType<RiftEntity>> RIFT = ENTITIES.register(
            "rift",
            () -> EntityType.Builder.<RiftEntity>of(RiftEntity::new, MobCategory.MISC)
                    .sized(1.5f, 2.5f)   // hitbox de la faille (à ajuster)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("rift")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
