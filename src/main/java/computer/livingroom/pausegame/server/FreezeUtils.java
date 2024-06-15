package computer.livingroom.pausegame.server;

import computer.livingroom.pausegame.PauseGame;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.TickRateManager;

public class FreezeUtils {
    public static void freezeGameWithStep(MinecraftServer server) {
        freezeGame(server, true);
    }

    public static void freezeGameNoStep(MinecraftServer server) {
        freezeGame(server, false);
    }

    private static void freezeGame(MinecraftServer server, boolean step) {
        TickRateManager manager = server.tickRateManager();

        if (PauseGameServer.settings.shouldSaveGame())
            server.saveEverything(false, false, false);

        PauseGame.LOGGER.info("Pausing game...");
        manager.setFrozen(true);
        if (step)
            manager.setFrozenTicksToRun(PauseGameServer.settings.getSteps());
    }
}
