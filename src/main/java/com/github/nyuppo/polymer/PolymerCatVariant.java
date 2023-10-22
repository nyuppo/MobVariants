package com.github.nyuppo.polymer;

import com.github.nyuppo.MoreMobVariants;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.nbt.NbtInt;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PolymerCatVariant extends CatVariant implements PolymerSyncedObject<CatVariant> {

    public PolymerCatVariant(Identifier texture) {
        super(texture);
    }

    @Override
    public CatVariant getPolymerReplacement(ServerPlayerEntity player) {
        if (PolymerServerNetworking.getMetadata(player.networkHandler, MoreMobVariants.MMB_HELLO_PACKET, NbtInt.TYPE) != null) {
            return this;
        }

        return Registries.CAT_VARIANT.get(CatVariant.ALL_BLACK);
    }
}
