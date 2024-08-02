package com.github.nyuppo.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ServerRespondVariantPayload(int entityId, String variant, boolean sittingOrMuddy, int muddyTimeout, String sheepHornColour) implements CustomPayload {
    public static final Id<ServerRespondVariantPayload> ID = new Id<>(MMVNetworkingConstants.SERVER_RESPOND_VARIANT_ID);
    public static final PacketCodec<PacketByteBuf, ServerRespondVariantPayload> PACKET_CODEC = PacketCodec.of(ServerRespondVariantPayload::write, ServerRespondVariantPayload::read);

    private static ServerRespondVariantPayload read(PacketByteBuf buf) {
        return new ServerRespondVariantPayload(
                buf.readInt(),
                buf.readString(),
                buf.readBoolean(),
                buf.readVarInt(),
                buf.readString()
        );
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeString(variant);
        buf.writeBoolean(sittingOrMuddy);
        buf.writeVarInt(muddyTimeout);
        buf.writeString(sheepHornColour);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}