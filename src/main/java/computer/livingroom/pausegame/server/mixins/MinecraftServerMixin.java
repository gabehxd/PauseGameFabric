package computer.livingroom.pausegame.server.mixins;

import computer.livingroom.pausegame.PauseGame;
import computer.livingroom.pausegame.server.FreezeUtils;
import computer.livingroom.pausegame.server.accessors.MinecraftServerTimerAccess;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerTimerAccess {
    @Unique
    private long delay;

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (--this.delay == 0L) {
            PauseGame.LOGGER.debug("TASK RUNNING!");
            FreezeUtils.freezeGame((MinecraftServer) (Object) this, true);
        }
    }

    @Unique
    @Override
    public void pauseGameFabric_SetTimer(long delay) {
        this.delay = delay;
    }
}
