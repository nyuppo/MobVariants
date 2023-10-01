package com.github.nyuppo.util;

import com.github.nyuppo.variant.MobVariant;
import net.minecraft.util.math.random.Random;

public record BreedingResultData(MobVariant parent1, MobVariant parent2) {
    public boolean validParents(MobVariant parent1, MobVariant parent2) {
        return parent1.getIdentifier().equals(this.parent1.getIdentifier()) && parent2.getIdentifier().equals(this.parent2.getIdentifier());
    }
}
