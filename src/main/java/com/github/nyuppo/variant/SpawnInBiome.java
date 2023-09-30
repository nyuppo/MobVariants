package com.github.nyuppo.variant;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public interface SpawnInBiome {
    TagKey<Biome> getSpawnBiomes();
}
