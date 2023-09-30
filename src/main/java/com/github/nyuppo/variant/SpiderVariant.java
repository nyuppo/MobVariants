package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class SpiderVariant extends MobVariant {
    private final boolean customEyes;

    public SpiderVariant(String namespace, String id, boolean customEyes) {
        super(namespace, id);
        this.customEyes = customEyes;
    }

    public SpiderVariant(Identifier identifier, boolean customEyes) {
        super(identifier);
        this.customEyes = customEyes;
    }

    public boolean hasCustomEyes() {
        return this.customEyes;
    }
}
