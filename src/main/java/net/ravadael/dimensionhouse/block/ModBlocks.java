package net.ravadael.dimensionhouse.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ravadael.dimensionhouse.DimensionHouse;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DimensionHouse.MOD_ID);

    // ✅ registre d'items dans la même classe
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DimensionHouse.MOD_ID);

    public static final RegistryObject<Block> HOUSE_PORTAL = BLOCKS.register(
            "house_portal",
            () -> new HouseReturnPortalBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0F, 3600000.0F)
                            .noOcclusion()
            )
    );

    // ✅ BlockItem associé
    public static final RegistryObject<Item> HOUSE_PORTAL_ITEM = ITEMS.register(
            "house_portal",
            () -> new BlockItem(HOUSE_PORTAL.get(), new Item.Properties())
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus); // <- important
    }
}
