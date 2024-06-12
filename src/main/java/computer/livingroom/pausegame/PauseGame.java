package computer.livingroom.pausegame;

import computer.livingroom.pausegame.network.PausePayload;
import computer.livingroom.pausegame.network.SupportPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PauseGame implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("PauseGame");

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SupportPayload.resource, SupportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PausePayload.resource, PausePayload.CODEC);
    }
}
