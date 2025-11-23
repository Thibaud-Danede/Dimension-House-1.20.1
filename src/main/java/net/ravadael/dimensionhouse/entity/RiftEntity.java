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

        // =========================
        // ✅ CLIENT : rien à faire
        // =========================
        if (level().isClientSide) {
            return;
        }

        // =========================
        // ✅ SERVEUR : lifetime + TP
        // =========================
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

            // Téléportation
            HouseTeleporter.handleTeleportKey(p);

            // ✅ Faille à usage unique : disparaît après traversée
            this.discard();
            return; // stoppe le tick car l'entité est supprimée
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
