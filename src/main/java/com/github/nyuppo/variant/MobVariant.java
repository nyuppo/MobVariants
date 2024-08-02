package com.github.nyuppo.variant;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class MobVariant {
    private final Identifier identifier;
    private final int weight;
    private final List<VariantModifier> modifiers;

    public MobVariant(Identifier identifier, int weight) {
        this.identifier = identifier;
        this.weight = weight;
        this.modifiers = new ArrayList<VariantModifier>();
    }

    public MobVariant(Identifier identifier, int weight, List<VariantModifier> modifiers) {
        this.identifier = identifier;
        this.weight = weight;
        this.modifiers = modifiers;
    }

    public MobVariant addModifier(VariantModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public Identifier getIdentifier() {
        return this.hasCustomVariantName() ? this.getCustomVariantName() : this.identifier;
    }

    public Identifier getRawIdentifier() {
        return this.identifier;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isShiny() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof ShinyModifier) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCustomVariantName() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof CustomVariantNameModifier) {
                return true;
            }
        }
        return false;
    }

    public Identifier getCustomVariantName() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof CustomVariantNameModifier) {
                return Identifier.of(this.identifier.getNamespace(), ((CustomVariantNameModifier) modifier).variantName());
            }
        }

        return MoreMobVariants.id("default");
    }

    public boolean shouldDiscard(Random random) {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof DiscardableModifier) {
                return ((DiscardableModifier) modifier).shouldDiscard(random);
            }
        }
        return false;
    }

    public boolean isInSpawnBiome(RegistryEntry<Biome> biome) {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof SpawnableBiomesModifier) {
                return ((SpawnableBiomesModifier) modifier).canSpawnInBiome(biome);
            }
        }
        return true;
    }

    public boolean canBreed(MobVariant parent1, MobVariant parent2) {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof BreedingResultModifier) {
                return ((BreedingResultModifier) modifier).validParents(parent1, parent2);
            }
        }
        return false;
    }

    public boolean shouldBreed(Random random) {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof BreedingResultModifier) {
                return ((BreedingResultModifier) modifier).shouldBreed(random);
            }
        }
        return false;
    }

    public boolean hasSpawnableBiomeModifier() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof SpawnableBiomesModifier) {
                return true;
            }
        }

        return false;
    }

    public boolean hasBreedingResultModifier() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof BreedingResultModifier) {
                return true;
            }
        }

        return false;
    }

    public boolean hasCustomEyes() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof CustomEyesModifier) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCustomWool() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof CustomWoolModifier) {
                return true;
            }
        }
        return false;
    }

    public boolean hasColorWhenSheared() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof ShearedWoolColorModifier) {
                return true;
            }
        }
        return false;
    }

    public boolean isNametagOverride() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof NametagOverrideModifier) {
                return true;
            }
        }
        return false;
    }

    public String getNametagOverride() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof NametagOverrideModifier) {
                return ((NametagOverrideModifier)modifier).nametag();
            }
        }
        return "error";
    }

    public boolean hasMinimumMoonSize() {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof MoonPhaseModifier) {
                return true;
            }
        }
        return false;
    }

    public boolean meetsMinimumMoonSize(float moonSize) {
        for (VariantModifier modifier : this.modifiers) {
            if (modifier instanceof MoonPhaseModifier) {
                return ((MoonPhaseModifier) modifier).canSpawn(moonSize);
            }
        }
        return false;
    }
}
