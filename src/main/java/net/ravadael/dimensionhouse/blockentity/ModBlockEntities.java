package net.ravadael.dimensionhouse.blockentity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ravadael.dimensionhouse.DimensionHouse;
import net.ravadael.dimensionhouse.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DimensionHouse.MOD_ID);

    public static final RegistryObject<BlockEntityType<HousePortalBlockEntity>> HOUSE_PORTAL_BE =
            BLOCK_ENTITIES.register("house_portal",
                    () -> BlockEntityType.Builder.of(HousePortalBlockEntity::new,
                            ModBlocks.HOUSE_PORTAL.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
