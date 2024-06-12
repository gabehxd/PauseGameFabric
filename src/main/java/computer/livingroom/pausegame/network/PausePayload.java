package computer.livingroom.pausegame.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record PausePayload(boolean paused) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, PausePayload> CODEC = CustomPacketPayload.codec(PausePayload::write, PausePayload::new);
    public static final CustomPacketPayload.Type<PausePayload> resource = new Type<>(new ResourceLocation("pausegame", "sync"));

    public PausePayload(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(paused);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return resource;
    }
}
