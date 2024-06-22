package computer.livingroom.pausegame.server;

import computer.livingroom.pausegame.PauseGame;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.TickRateManager;

public class FreezeUtils {
    public static void freezeGame(MinecraftServer server, boolean isQuit) {
        if (isQuit && PauseGameServer.settings.shouldSaveGame()) {
            //this should unload everything for us
            server.saveEverything(false, true, false);
        }

        PauseGame.LOGGER.info("Pausing game...");
        TickRateManager manager = server.tickRateManager();
        manager.setFrozen(true);
    }
}
