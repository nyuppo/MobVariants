package com.github.nyuppo.variant;

import net.minecraft.util.Identifier;

public interface BreedingCombination {
    Identifier getParent1();
    Identifier getParent2();
    boolean validParents(Identifier parent1, Identifier parent2);
}
