package net.ravadael.dimensionhouse.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.ravadael.dimensionhouse.world.HouseTeleporter;

public class RiftEntity extends Entity {

    private static final int LIFETIME_TICKS = 20 * 30; // 30 secondes
    private static final String NBT_RIFT_CD = "dimensionhouse:rift_cd";

    public RiftEntity(EntityType<? extends RiftEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // expire au bout de 30s tant que le chunk reste chargé
            if (this.tickCount >= LIFETIME_TICKS) {
                this.discard();
                return;
            }

            var players = level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox());
            for (ServerPlayer p : players) {

                long now = level().getGameTime();
                var nbt = p.getPersistentData();

                // cooldown 1s pour éviter TP en boucle
                if (nbt.contains(NBT_RIFT_CD) && now < nbt.getLong(NBT_RIFT_CD)) {
                    continue;
                }
                nbt.putLong(NBT_RIFT_CD, now + 20);

                HouseTeleporter.handleTeleportKey(p);
                // on NE discard PAS : la faille reste active durant sa vie
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // rien à lire
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // rien à sauvegarder
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
