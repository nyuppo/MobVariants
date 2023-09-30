package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class SpiderVariant extends MobVariant {
    private final boolean customEyes;

    public SpiderVariant(String namespace, String id, int weight, boolean customEyes) {
        super(namespace, id, weight);
        this.customEyes = customEyes;
    }

    public SpiderVariant(Identifier identifier, int weight, boolean customEyes) {
        super(identifier, weight);
        this.customEyes = customEyes;
    }

    public boolean hasCustomEyes() {
        return this.customEyes;
    }
}
