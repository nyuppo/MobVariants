package com.github.nyuppo.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ServerRespondBasicVariantPayload(int entityId, String variantId) implements CustomPayload {
    public static final Id<ServerRespondBasicVariantPayload> ID = new Id<>(MMVNetworkingConstants.SERVER_RESPOND_BASIC_VARIANT_ID);
    public static final PacketCodec<PacketByteBuf, ServerRespondBasicVariantPayload> PACKET_CODEC = PacketCodec.of(ServerRespondBasicVariantPayload::write, ServerRespondBasicVariantPayload::read);

    private static ServerRespondBasicVariantPayload read(PacketByteBuf buf) {
        return new ServerRespondBasicVariantPayload(
                buf.readInt(),
                buf.readString()
        );
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeString(variantId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}