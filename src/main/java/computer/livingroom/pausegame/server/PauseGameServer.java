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
        public int getSteps() {
            int ticks = config.getOrDefault("task-delay-in-ticks", -1);
            if (ticks < 0) {
                ticks = config.getOrDefault("step-ticks", 1);
                if (ticks < 0) {
                    return 0;
                }
            }
            return ticks;
        }

        public boolean shouldSaveGame() {
            return config.getOrDefault("save-game", false);
        }

        public boolean enableModSupport() {
            return config.getOrDefault("enable-mod-companion", true);
        }
    }

    private static String defaultConfig(String filename) {
        return """
                #Whether to save the game when the server pauses, usually fine without saving.
                save-game: false

                #How many ticks to step after the server has been paused. After 1 tick the server will usually unload chunks and save them.
                step-ticks: 1

                #Whether to let clients let the server know they are in the pause menu
                enable-mod-companion: true
                """;
    }
}
