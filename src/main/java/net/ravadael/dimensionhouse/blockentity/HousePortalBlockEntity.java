package net.ravadael.dimensionhouse.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ravadael.dimensionhouse.blockentity.ModBlockEntities;

public class HousePortalBlockEntity extends BlockEntity {
    public HousePortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOUSE_PORTAL_BE.get(), pos, state);
    }
}
