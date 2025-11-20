package net.ravadael.dimensionhouse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ravadael.dimensionhouse.blockentity.HousePortalBlockEntity;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

import javax.annotation.Nullable;

public class HouseReturnPortalBlock extends Block implements EntityBlock {

    private static final String NBT_PORTAL_CD = "dimensionhouse:portal_cd";

    // ✅ Propriété d'orientation horizontale
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HouseReturnPortalBlock(Properties props) {
        super(props);
        // ✅ Valeur par défaut
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // ✅ Orientation au placement : le bloc fait face au joueur
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // comme les boutons/torches : direction la plus regardée
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    // ✅ Support rotation / miroir (utile si tu places via structure, etc.)
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // ✅ Enregistre la propriété dans le BlockState
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // ✅ Bloc traversable : collision vide
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    // ✅ Pas d’occlusion
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    // ✅ Aussi vide pour le rendu/selection
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    // ✅ Téléport au contact (serveur only)
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        if (!player.level().dimension().equals(HouseTeleporter.HOUSE_DIMENSION)) return;

        var nbt = player.getPersistentData();
        long gameTime = level.getGameTime();

        if (nbt.contains(NBT_PORTAL_CD) && gameTime < nbt.getLong(NBT_PORTAL_CD)) return;
        nbt.putLong(NBT_PORTAL_CD, gameTime + 20);

        HouseTeleporter.handleTeleportKey(player);
    }

    // ✅ BlockEntity pour le rendu End-Portal
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HousePortalBlockEntity(pos, state);
    }

    // Pas de tick BE nécessaire
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    // ✅ On cache le modèle JSON : seul le BER rend le portail
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
