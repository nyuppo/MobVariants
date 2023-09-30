package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class MobVariant {
    private final String namespace;
    private final String id;
    private final int weight;

    public MobVariant(String namespace, String id, int weight) {
        this.namespace = namespace;
        this.id = id;
        this.weight = weight;
    }

    public MobVariant(Identifier identifier, int weight) {
        this.namespace = identifier.getNamespace();
        this.id = identifier.getPath();
        this.weight = weight;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getID() {
        return this.id;
    }

    public int getWeight() {
        return this.weight;
    }

    public Identifier getIdentifier() {
        return new Identifier(this.namespace, this.id);
    }
}
