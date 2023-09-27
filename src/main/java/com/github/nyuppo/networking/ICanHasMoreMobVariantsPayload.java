package com.github.nyuppo.networking;

import eu.pb4.polymer.networking.api.payload.VersionedPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public record ICanHasMoreMobVariantsPayload() implements VersionedPayload {
    public static final Identifier ID = new Identifier("moremobvariants", "icanhas");

    @Override
    public void write(PacketContext context, int version, PacketByteBuf buf) {
    }

    @Override
    public Identifier id() {
        return ID;
    }

    public static ICanHasMoreMobVariantsPayload read(PacketContext context, Identifier identifier, int version, PacketByteBuf buf) {
        return new ICanHasMoreMobVariantsPayload();
    }
}
