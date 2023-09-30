package com.github.nyuppo.variant;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public record SpawnableBiomesModifier(TagKey<Biome> spawnBiomes) implements VariantModifier {
    public boolean canSpawnInBiome(RegistryEntry<Biome> biome) {
        return biome.isIn(this.spawnBiomes);
    }
}
