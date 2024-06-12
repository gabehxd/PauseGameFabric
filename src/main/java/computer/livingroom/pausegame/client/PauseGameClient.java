package computer.livingroom.pausegame.client;

import computer.livingroom.pausegame.PauseGame;
import computer.livingroom.pausegame.network.PausePayload;
import computer.livingroom.pausegame.network.SupportPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class PauseGameClient implements ClientModInitializer {
    private boolean connectedToServer = false;
    private boolean isPaused = false;
    private boolean isSupportedOnServer = false;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SupportPayload.resource, (payload, context) -> {
            isSupportedOnServer = true;
            PauseGame.LOGGER.info("Support is enabled on this server!");
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            PauseGame.LOGGER.info("Joined server");
            if (client.getConnection() != null && client.getConnection().getServerData() != null)
                if (!client.getConnection().getServerData().isRealm() && !client.getConnection().getServerData().isLan() && !client.isLocalServer())
                    connectedToServer = true;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PauseGame.LOGGER.info("Left server");
            if (client.getConnection() != null && client.getConnection().getServerData() != null)
                if (!client.getConnection().getServerData().isRealm() && !client.getConnection().getServerData().isLan() && !client.isLocalServer()) {
                    connectedToServer = false;
                    isSupportedOnServer = false;
                    isPaused = false;
                    PauseGame.LOGGER.info("Resetting state");
                }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (!connectedToServer || !isSupportedOnServer)
                return;

            Minecraft minecraft = Minecraft.getInstance();
            boolean pauseMenuOpen = minecraft.screen != null && minecraft.screen.isPauseScreen() || minecraft.getOverlay() != null && minecraft.getOverlay().isPauseScreen();
            if (pauseMenuOpen) {
                if (!isPaused) {
                    //Send Message that the player is in the pause menu
                    ClientPlayNetworking.send(new PausePayload(true));
                    isPaused = true;
                }
            } else {
                if (isPaused) {
                    //Send Message that the player no longer in the pause menu
                    ClientPlayNetworking.send(new PausePayload(false));
                    isPaused = false;
                }
            }
        });
    }
}
