package computer.livingroom.pausegame.server.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import computer.livingroom.pausegame.server.PauseGameServer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @WrapWithCondition(
            method = "setAirSupply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V")
    )
    private boolean doNotUpdateAirSupply(SynchedEntityData instance, EntityDataAccessor<Object> key, Object value) {
        return this.getServer().tickRateManager().isFrozen() && PauseGameServer.settings.enableModSupport();
    }
}
