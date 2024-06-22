package computer.livingroom.pausegame.server;

import computer.livingroom.pausegame.server.config.lib.SimpleConfig;
import computer.livingroom.pausegame.server.events.ModCompanionEvents;
import computer.livingroom.pausegame.server.events.PauseGameEvents;
import net.fabricmc.api.DedicatedServerModInitializer;

import static computer.livingroom.pausegame.PauseGame.LOGGER;


public class PauseGameServer implements DedicatedServerModInitializer {
    private static final SimpleConfig config = SimpleConfig.of("pausegame").provider(PauseGameServer::defaultConfig).request();
    public static Settings settings = new Settings();

    @Override
    public void onInitializeServer() {
        if (settings.enableModSupport()) {
            ModCompanionEvents.init();
        }
        PauseGameEvents.init();
        LOGGER.info("Started!");
    }

    public static class Settings {
        public boolean shouldSaveGame() {
            return config.getOrDefault("save-game-on-quit", true);
        }

        public boolean enableModSupport() {
            return config.getOrDefault("enable-mod-companion", true);
        }
    }

    private static String defaultConfig(String filename) {
        return """
                #Whether to save and unloads chunks when all players have left the server
                save-game-on-quit: true

                #Whether to let clients let the server know they are in the pause menu
                enable-mod-companion: true
                """;
    }
}
