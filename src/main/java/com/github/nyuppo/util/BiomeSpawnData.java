package com.github.nyuppo.util;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public record BiomeSpawnData(TagKey<Biome> validSpawnBiomes, RegistryEntry<Biome> spawnBiome) {
    public boolean canSpawn() {
        return spawnBiome.isIn(validSpawnBiomes);
    }
}
