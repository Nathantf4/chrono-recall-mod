package com.nathan.chronorecall;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStateTracker {

    public static final int BUFFER_SIZE = 100; // 5 seconds at 20 tps

    private static final Map<UUID, Deque<PlayerSnapshot>> snapshots = new HashMap<>();

    public static class PlayerSnapshot {
        public final double x, y, z;
        public final float health;
        public final int foodLevel;
        public final float saturation;
        public final float yaw, pitch;

        public PlayerSnapshot(double x, double y, double z, float health, int foodLevel, float saturation, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.health = health;
            this.foodLevel = foodLevel;
            this.saturation = saturation;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();
        Deque<PlayerSnapshot> buffer = snapshots.computeIfAbsent(uuid, k -> new ArrayDeque<>());

        buffer.addLast(new PlayerSnapshot(
                player.getX(), player.getY(), player.getZ(),
                player.getHealth(),
                player.getFoodData().getFoodLevel(),
                player.getFoodData().getSaturationLevel(),
                player.getYRot(), player.getXRot()
        ));

        if (buffer.size() > BUFFER_SIZE) {
            buffer.removeFirst();
        }
    }

    /**
     * Get the oldest snapshot in the buffer (up to 5 seconds ago).
     * Returns null if no snapshots are recorded.
     */
    public static PlayerSnapshot getOldestSnapshot(UUID uuid) {
        Deque<PlayerSnapshot> buffer = snapshots.get(uuid);
        if (buffer == null || buffer.isEmpty()) return null;
        return buffer.peekFirst();
    }

    /**
     * Clear the snapshot buffer for a player (called after recall to prevent instant re-recall to same spot).
     */
    public static void clearSnapshots(UUID uuid) {
        Deque<PlayerSnapshot> buffer = snapshots.get(uuid);
        if (buffer != null) {
            buffer.clear();
        }
    }

    /**
     * Remove a player's data entirely (e.g., on disconnect).
     */
    public static void removePlayer(UUID uuid) {
        snapshots.remove(uuid);
    }
}
