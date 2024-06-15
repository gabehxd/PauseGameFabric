package computer.livingroom.pausegame.server.mixins;

import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(
            method = "setAirSupply",
            at = @At("HEAD"),
            cancellable = true
    )
    private void doNotUpdateAirSupply(int air, CallbackInfo ci) {
        if (this.getServer().tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport())
            ci.cancel();
    }
}
