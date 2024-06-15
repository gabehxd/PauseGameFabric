package computer.livingroom.pausegame.server.mixins;

import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow
    @Final
    public MinecraftServer server;


    @Inject(
            method = "hurt",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.server.tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport()) {
            cir.setReturnValue(false);
            cir.cancel();
        }

    }
}
