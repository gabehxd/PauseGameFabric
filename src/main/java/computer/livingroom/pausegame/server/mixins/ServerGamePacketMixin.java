package computer.livingroom.pausegame.server.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true, print = true)
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketMixin {
    @Unique
    private double prevX = Double.MAX_VALUE;
    @Unique
    private double prevY = Double.MAX_VALUE;
    @Unique
    private double prevZ = Double.MAX_VALUE;
    @Unique
    private float prevPitch = Float.MAX_VALUE;
    @Unique
    private float prevYaw = Float.MAX_VALUE;

    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void teleport(double x, double y, double z, float yaw, float pitch);

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientIsFloating:Z", opcode = Opcodes.GETFIELD)
    )
    private boolean disableFlyCheckWhileFrozen(boolean original) {
        return original && (!this.player.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport());
    }

    @Inject(
            method = "handleMovePlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getX()D", ordinal = 2, shift = At.Shift.BEFORE)
    )
    private void storeLastLocPlayer(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        prevX = player.getX();
        prevY = player.getY();
        prevZ = player.getZ();
        prevYaw = player.getYRot();
        prevPitch = player.getXRot();
    }

    @Inject(
            method = "handleMovePlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V", ordinal = 1, shift = At.Shift.BEFORE),
            cancellable = true
    )
    private void preventPlayerMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (player.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport()) {
            this.teleport(prevX, prevY, prevZ, prevYaw, prevPitch);
            ci.cancel();
        }
    }

    @Inject(
            method = "handleMoveVehicle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;serverLevel()Lnet/minecraft/server/level/ServerLevel;", ordinal = 1, shift = At.Shift.BEFORE)
    )
    private void storeLastLocPlayer(ServerboundMoveVehiclePacket packet, CallbackInfo ci) {
        prevX = player.getX();
        prevY = player.getY();
        prevZ = player.getZ();
        prevYaw = player.getYRot();
        prevPitch = player.getXRot();
    }

    @Inject(
            method = "handleMoveVehicle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;serverLevel()Lnet/minecraft/server/level/ServerLevel;", ordinal = 1, shift = At.Shift.BEFORE),
            cancellable = true
    )
    private void preventVehicleMove(ServerboundMoveVehiclePacket packet, CallbackInfo ci) {
        if (player.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport()) {
            this.teleport(prevX, prevY, prevZ, prevYaw, prevPitch);
            ci.cancel();
        }
    }

}
