package computer.livingroom.pausegame.server.events;

import computer.livingroom.pausegame.network.PausePayload;
import computer.livingroom.pausegame.network.SupportPayload;
import computer.livingroom.pausegame.server.FreezeUtils;
import computer.livingroom.pausegame.server.PauseGameServer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

import static computer.livingroom.pausegame.PauseGame.LOGGER;

public class ModCompanionEvents {
    public static ArrayList<Player> frozenPlayers = new ArrayList<>();

    public static void init() {
        LOGGER.info("Registering mod support...");
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LOGGER.info("Sending support packet");
            ServerPlayNetworking.send(handler.player, new SupportPayload());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> frozenPlayers.remove(handler.player));

        //playerinteractevent stuff
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.getServer().tickRateManager().isFrozen() || !PauseGameServer.settings.enableModSupport())
                return InteractionResult.PASS;

            if (frozenPlayers.contains(player))
                return InteractionResult.FAIL;

            return InteractionResult.PASS;
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.getServer().tickRateManager().isFrozen() || !PauseGameServer.settings.enableModSupport())
                return InteractionResultHolder.pass(player.getItemInHand(hand));

            if (frozenPlayers.contains(player))
                return InteractionResultHolder.fail(player.getItemInHand(hand));

            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!player.getServer().tickRateManager().isFrozen() || !PauseGameServer.settings.enableModSupport())
                return true;

            return !frozenPlayers.contains(player);
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> !entity.getServer().tickRateManager().isFrozen() || !PauseGameServer.settings.enableModSupport());

        ServerPlayNetworking.registerGlobalReceiver(PausePayload.resource, (payload, context) -> {
            MinecraftServer server = context.player().server;
            if (payload.paused()) {
                frozenPlayers.add(context.player());
                if (server.getPlayerList().getPlayerCount() == frozenPlayers.size()) {
                    FreezeUtils.freezeGame(server, false);
                }
            } else {
                frozenPlayers.remove(context.player());
                TickRateManager tickManager = server.tickRateManager();
                if (tickManager.isFrozen()) {
                    LOGGER.info("Unpausing game...");
                    tickManager.setFrozen(false);
                }
            }
        });
    }
}
