package computer.livingroom.pausegame.server.mixins;

import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "causeFoodExhaustion",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void preventFoodExhaustion(float exhaustion, CallbackInfo ci) {
        if (this.getServer().tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport())
            ci.cancel();
    }
}
