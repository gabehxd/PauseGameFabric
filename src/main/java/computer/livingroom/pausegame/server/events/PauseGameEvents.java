package computer.livingroom.pausegame.server.events;

import computer.livingroom.pausegame.server.FreezeUtils;
import computer.livingroom.pausegame.server.accessors.MinecraftServerTimerAccess;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.TickRateManager;

import static computer.livingroom.pausegame.PauseGame.LOGGER;

public class PauseGameEvents {
    public static void init() {
        LOGGER.info("Setting up PauseGame...");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> FreezeUtils.freezeGame(server, false));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            TickRateManager tickManager = server.tickRateManager();
            if (tickManager.isFrozen()) {
                LOGGER.info("Unpausing game...");
                tickManager.setFrozen(false);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (server.getPlayerList().getPlayerCount() != 1)
                return;

            LOGGER.info("Running freeze task due to a player leaving");
            ((MinecraftServerTimerAccess) server).pauseGameFabric_SetTimer(1L);
        });
    }
}
