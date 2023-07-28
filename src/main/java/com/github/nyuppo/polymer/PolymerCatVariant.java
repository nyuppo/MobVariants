package com.github.nyuppo.polymer;

import com.github.nyuppo.MoreMobVariants;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PolymerCatVariant extends CatVariant implements PolymerSyncedObject<CatVariant> {

    public PolymerCatVariant(Identifier texture) {
        super(texture);
    }

    @Override
    public CatVariant getPolymerReplacement(ServerPlayerEntity player) {
        if(MoreMobVariants.hasClientMod(player))return this;
        return Registries.CAT_VARIANT.get(CatVariant.ALL_BLACK);
    }
}
