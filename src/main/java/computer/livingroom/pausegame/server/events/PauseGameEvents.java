package computer.livingroom.pausegame.server.events;

import computer.livingroom.pausegame.server.FreezeUtils;
import computer.livingroom.pausegame.server.PauseGameServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.TickRateManager;

import static computer.livingroom.pausegame.PauseGame.LOGGER;

public class PauseGameEvents {
    public static void init() {
        LOGGER.info("Setting up PauseGame...");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("Pausing game...");
            server.tickRateManager().setFrozen(true);
        });

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

            TickRateManager tickManager = server.tickRateManager();

            if (tickManager.isFrozen()) {
                tickManager.setFrozenTicksToRun(PauseGameServer.settings.getSteps());
                return;
            }

            LOGGER.info("Running freeze task due to a player leaving");
            FreezeUtils.freezeGameWithStep(server);
        });
    }
}
