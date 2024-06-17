package computer.livingroom.pausegame.server.mixins;

import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "heal",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelHealing(float healAmount, CallbackInfo ci) {
        if (this.getServer().tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport())
            ci.cancel();
    }
}
