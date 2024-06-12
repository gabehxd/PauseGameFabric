package computer.livingroom.pausegame.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SupportPayload() implements CustomPacketPayload {
    private final static SupportPayload INSTANCE = new SupportPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, SupportPayload> CODEC = StreamCodec.unit(INSTANCE);
    public static final CustomPacketPayload.Type<SupportPayload> resource = new Type<>(new ResourceLocation("pausegame", "supported"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return resource;
    }
}
