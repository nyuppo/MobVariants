package com.github.nyuppo.variant;

import net.minecraft.util.math.random.Random;

public record BreedingResultModifier(MobVariant parent1, MobVariant parent2, double breedingChance) implements VariantModifier {
    public boolean validParents(MobVariant parent1, MobVariant parent2) {
        return parent1.getIdentifier().equals(this.parent1.getIdentifier()) && parent2.getIdentifier().equals(this.parent2.getIdentifier());
    }

    public boolean shouldBreed(Random random) {
        return random.nextDouble() < this.breedingChance;
    }
}
