package computer.livingroom.pausegame.server.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketMixin {
    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void teleport(double x, double y, double z, float yaw, float pitch);

    @Shadow
    private double lastGoodX;

    @Shadow
    private double lastGoodY;

    @Shadow
    private double lastGoodZ;

    @Shadow
    private double vehicleLastGoodZ;

    @Shadow
    private double vehicleFirstGoodY;

    @Shadow
    private double vehicleFirstGoodX;

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientIsFloating:Z", opcode = Opcodes.GETFIELD)
    )
    private boolean disableFlyCheckWhileFrozen(boolean original) {
        return original && (!this.player.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport());
    }

    @Inject(
            method = "handleMovePlayer",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void preventPlayerMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (player.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport()) {
            teleport(lastGoodX, lastGoodY, lastGoodZ, player.getYRot(), player.getXRot());
            ci.cancel();
        }
    }

    @Inject(
            method = "handleMoveVehicle",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void preventVehicleMove(ServerboundMoveVehiclePacket packet, CallbackInfo ci) {
        if (player.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport()) {
            teleport(vehicleFirstGoodX, vehicleFirstGoodY, vehicleLastGoodZ, player.getYRot(), player.getXRot());
            ci.cancel();
        }
    }
}
