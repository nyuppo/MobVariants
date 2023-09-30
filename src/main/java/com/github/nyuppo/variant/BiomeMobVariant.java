package com.github.nyuppo.variant;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeMobVariant extends MobVariant implements SpawnInBiome {
    private final TagKey<Biome> spawnBiomes;

    public BiomeMobVariant(String namespace, String id, int weight, TagKey<Biome> spawnBiomes) {
        super(namespace, id, weight);
        this.spawnBiomes = spawnBiomes;
    }

    public BiomeMobVariant(Identifier identifier, int weight, TagKey<Biome> spawnBiomes) {
        super(identifier, weight);
        this.spawnBiomes = spawnBiomes;
    }

    public TagKey<Biome> getSpawnBiomes() {
        return this.spawnBiomes;
    }
}
