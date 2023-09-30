package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class MobVariant {
    private final Identifier identifier;
    private final int weight;

    public MobVariant(String namespace, String id, int weight) {
        this.identifier = new Identifier(namespace, id);
        this.weight = weight;
    }

    public MobVariant(Identifier identifier, int weight) {
        this.identifier = identifier;
        this.weight = weight;
    }

    public String getNamespace() {
        return this.identifier.getNamespace();
    }

    public String getID() {
        return this.identifier.getPath();
    }

    public int getWeight() {
        return this.weight;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }
}
