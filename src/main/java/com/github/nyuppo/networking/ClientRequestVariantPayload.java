package com.github.nyuppo.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record ClientRequestVariantPayload(UUID uuid) implements CustomPayload {
    public static final Id<ClientRequestVariantPayload> ID = new Id<>(MMVNetworkingConstants.CLIENT_REQUEST_VARIANT_ID);
    public static final PacketCodec<PacketByteBuf, ClientRequestVariantPayload> PACKET_CODEC = PacketCodec.of(ClientRequestVariantPayload::write, ClientRequestVariantPayload::read);

    private static ClientRequestVariantPayload read(PacketByteBuf buf) {
        UUID uuid = buf.readUuid();
        return new ClientRequestVariantPayload(uuid);
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(uuid);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
