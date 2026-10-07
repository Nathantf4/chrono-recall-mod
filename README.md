# Chrono Recall

A Minecraft Forge mod: **press R to rewind 5 seconds.** Restores your position, health, hunger, and saturation — with a purple particle burst. 30-second cooldown.

- **Minecraft:** 1.20.1 – 1.21
- **Forge:** 47+
- **License:** MIT

## How it works

`PlayerStateTracker` keeps a rolling per-player buffer of the last 100 ticks (5 s at 20 tps): position, health, food level, saturation, yaw/pitch. On recall, the client sends a `RecallPacket` over the mod's `SimpleChannel`; the server validates the cooldown, pops the oldest snapshot, teleports the player, and restores their state.

| File | Role |
|---|---|
| `ChronoRecall.java` | Mod entrypoint — event bus + network channel registration |
| `PlayerStateTracker.java` | Server-side rolling snapshot buffer (per-UUID `ArrayDeque`) |
| `RecallHandler.java` | Cooldown check, snapshot restore, particle burst |
| `RecallPacket.java` | Client→server recall request |
| `KeyBindings.java` | R key registration (client) |

## Build

```bash
gradle build
# jar lands in build/libs/
```

Requires the ForgeGradle 6 plugin (see `build.gradle`) and a Java 17 toolchain. The Gradle wrapper is not checked in — use a system Gradle 8.x, or run `gradle wrapper` once to generate it.

## Install

Drop the built jar into the `mods/` folder of a Forge 47+ (MC 1.20.1) instance — client and server both need it.
