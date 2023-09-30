package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class MobVariant {
    private final String namespace;
    private final String id;

    public MobVariant(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
    }

    public MobVariant(Identifier identifier) {
        this.namespace = identifier.getNamespace();
        this.id = identifier.getPath();
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getID() {
        return this.id;
    }

    public Identifier getIdentifier() {
        return new Identifier(this.namespace, this.id);
    }
}
