package com.github.nyuppo.variant;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class WolfVariant extends MobVariant implements BreedingCombination, SpawnInBiome {
    @Nullable private final TagKey<Biome> spawnBiomes;
    private final Identifier parent1Identifier;
    private final Identifier parent2Identifier;

    public WolfVariant(String namespace, String id, int weight, Identifier parent1Identifier, Identifier parent2Identifier) {
        super(namespace, id, weight);
        this.parent1Identifier = parent1Identifier;
        this.parent2Identifier = parent2Identifier;
        this.spawnBiomes = null;
    }

    public WolfVariant(String namespace, String id, int weight, Identifier parent1Identifier, Identifier parent2Identifier, TagKey<Biome> spawnBiomes) {
        super(namespace, id, weight);
        this.parent1Identifier = parent1Identifier;
        this.parent2Identifier = parent2Identifier;
        this.spawnBiomes = spawnBiomes;
    }

    public WolfVariant(Identifier identifier, int weight, Identifier parent1Identifier, Identifier parent2Identifier) {
        super(identifier, weight);
        this.parent1Identifier = parent1Identifier;
        this.parent2Identifier = parent2Identifier;
        this.spawnBiomes = null;
    }

    public WolfVariant(Identifier identifier, int weight, Identifier parent1Identifier, Identifier parent2Identifier, TagKey<Biome> spawnBiomes) {
        super(identifier, weight);
        this.parent1Identifier = parent1Identifier;
        this.parent2Identifier = parent2Identifier;
        this.spawnBiomes = spawnBiomes;
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

    @Nullable
    public TagKey<Biome> getSpawnBiomes() {
        return this.spawnBiomes;
    }
}
