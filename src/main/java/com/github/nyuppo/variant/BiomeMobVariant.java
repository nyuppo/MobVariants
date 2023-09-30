package com.github.nyuppo.variant;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeMobVariant extends MobVariant implements SpawnInBiome {
    private final TagKey<Biome> spawnBiomes;

    public BiomeMobVariant(String namespace, String id, TagKey<Biome> spawnBiomes) {
        super(namespace, id);
        this.spawnBiomes = spawnBiomes;
    }

    public BiomeMobVariant(Identifier identifier, TagKey<Biome> spawnBiomes) {
        super(identifier);
        this.spawnBiomes = spawnBiomes;
    }

    public TagKey<Biome> getSpawnBiomes() {
        return this.spawnBiomes;
    }
}
