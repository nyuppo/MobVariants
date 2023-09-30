package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public class WolfVariant extends MobVariant implements BreedingCombination {
    private final Identifier parent1Identifier;
    private final Identifier parent2Identifier;

    public WolfVariant(String namespace, String id, Identifier parent1Identifier, Identifier parent2Identifier) {
        super(namespace, id);
        this.parent1Identifier = parent1Identifier;
        this.parent2Identifier = parent2Identifier;
    }

    public WolfVariant(Identifier identifier, Identifier parent1Identifier, Identifier parent2Identifier) {
        super(identifier);
        this.parent1Identifier = parent1Identifier;
        this.parent2Identifier = parent2Identifier;
    }

    public Identifier getParent1() {
        return this.parent1Identifier;
    }

    public Identifier getParent2() {
        return this.parent2Identifier;
    }

    public boolean validParents(Identifier parent1, Identifier parent2) {
        return parent1.equals(this.parent1Identifier) && parent2.equals(this.parent2Identifier);
    }
}
