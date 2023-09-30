package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class SheepVariant extends MobVariant {
    private final boolean customWool;

    public SheepVariant(String namespace, String id, int weight, boolean customWool) {
        super(namespace, id, weight);
        this.customWool = customWool;
    }

    public SheepVariant(Identifier identifier, int weight, boolean customWool) {
        super(identifier, weight);
        this.customWool = customWool;
    }

    public boolean hasCustomEyes() {
        return this.customWool;
    }
}
