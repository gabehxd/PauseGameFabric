package computer.livingroom.pausegame.server.mixins;

import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @ModifyArg(
            method = "applyGravity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0)
    )
    private Vec3 setVelocity(Vec3 original) {
        if ((this.getServer().tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport() && ((Object) this) instanceof Minecart minecart && minecart.hasExactlyOnePlayerPassenger())) {
            return new Vec3(0, 0, 0);
        } else {
            return original;
        }
    }
}
