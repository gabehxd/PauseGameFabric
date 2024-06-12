package computer.livingroom.pausegame.client;

import computer.livingroom.pausegame.networking.PausePayload;
import computer.livingroom.pausegame.networking.SupportPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PauseGameClient implements ClientModInitializer {
    private boolean connectedToServer = false;
    private boolean isPaused = false;
    private boolean isSupportedOnServer = false;

    public static final Logger LOGGER = LoggerFactory.getLogger("PauseGame");

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(SupportPayload.resource, SupportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PausePayload.resource, PausePayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(SupportPayload.resource, (payload, context) -> {
            isSupportedOnServer = true;
            LOGGER.info("Support is enabled on this server!");
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            LOGGER.info("Joined server");
            if (client.getConnection() != null && client.getConnection().getServerData() != null)
                if (!client.getConnection().getServerData().isRealm() && !client.getConnection().getServerData().isLan())
                    connectedToServer = true;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            LOGGER.info("Left server");
            connectedToServer = false;
            isSupportedOnServer = false;
            isPaused = false;
            LOGGER.debug("Resetting state");
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (!connectedToServer || !isSupportedOnServer)
                return;

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen != null && minecraft.screen.isPauseScreen() || minecraft.getOverlay() != null && minecraft.getOverlay().isPauseScreen())
            {
                if (!isPaused)
                {
                    //Send Message that the player is in the pause menu
                    ClientPlayNetworking.send(new PausePayload(true));
                    isPaused = true;
                }
            }
            else
            {
                if (isPaused)
                {
                    //Send Message that the player no longer in the pause menu
                    ClientPlayNetworking.send(new PausePayload(false));
                    isPaused = false;
                }
            }
        });
    }
}
