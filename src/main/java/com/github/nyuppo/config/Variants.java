package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.util.BiomeSpawnData;
import com.github.nyuppo.util.BreedingResultData;
import com.github.nyuppo.util.VariantBag;
import com.github.nyuppo.variant.*;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Variants {
    private static HashMap<Mob, ArrayList<MobVariant>> variants;
    private static HashMap<Mob, ArrayList<MobVariant>> defaultVariants;

    public static void addVariant(Mob mob, MobVariant variant) {
        if (variants.get(mob) == null) {
            variants.put(mob, new ArrayList<MobVariant>());
        }

        variants.get(mob).add(variant);
    }

    public static ArrayList<MobVariant> getVariants(Mob mob) {
        if (variants.get(mob) != null) {
            return variants.get(mob);
        }
        return defaultVariants.get(mob);
    }

    public static ArrayList<MobVariant> getDefaultVariants(Mob mob) {
        return defaultVariants.get(mob);
    }

    public static MobVariant getDefaultVariant(Mob mob) {
        ArrayList<MobVariant> variants = getVariants(mob);
        for (MobVariant variant : variants) {
            if (variant.getIdentifier().equals(MoreMobVariants.id("default"))) {
                return variant;
            }
        }
        return new MobVariant(MoreMobVariants.id("default"), 1);
    }

    public static MobVariant getVariant(Mob mob, Identifier identifier) {
        ArrayList<MobVariant> variants = getVariants(mob);

        for (MobVariant variant : variants) {
            if (variant.getIdentifier().equals(identifier)) {
                return variant;
            }
        }
        return getDefaultVariant(mob);
    }

    public static void resetVariants(Mob mob) {
        variants.remove(mob);
        variants.put(mob, defaultVariants.get(mob));
    }

    public static void clearVariants(Mob mob) {
        variants.remove(mob);
        variants.put(mob, new ArrayList<>());
    }

    public static void clearAllVariants() {
        for (Mob mob : Mob.values()) {
            clearVariants(mob);
        }
    }

    public static void validateEmptyVariants() {
        for (Mob mob : Mob.values()) {
            if (variants.get(mob).isEmpty()) {
                resetVariants(mob);
            }
        }
    }

    public static void applyBlacklists() {
        for (Mob mob : Mob.values()) {
            List<MobVariant> variants = getVariants(mob);
            if (variants.isEmpty()) {
                continue;
            }

            Iterator<MobVariant> i = variants.iterator();
            MobVariant variant;
            while (i.hasNext()) {
                variant = i.next();
                if (VariantBlacklist.isBlacklisted(mob, variant.getIdentifier())) {
                    i.remove();
                }
            }
        }
    }

    public static Mob getMob(String mobId) {
        for (Mob mob : Mob.values()) {
            if (mob.getId().equalsIgnoreCase(mobId)) {
                return mob;
            }
        }
        return Mob.NULL;
    }

    public static MobVariant getRandomVariant(Mob mob, Random random, @Nullable RegistryEntry<Biome> spawnBiome, @Nullable BreedingResultData breedingResultData) {
        ArrayList<MobVariant> variants = getVariants(mob);
        if (variants.isEmpty()) {
            return getDefaultVariant(mob);
        }

        // Handle modifiers
        Iterator<MobVariant> i = variants.iterator();
        MobVariant variant;
        while (i.hasNext()) {
            variant = i.next();

            // Discard if not in spawn biome
            if (spawnBiome != null && variant.hasSpawnableBiomeModifier()) {
                if (!variant.isInSpawnBiome(spawnBiome)) {
                    i.remove();
                    continue;
                }
            }

            // Discard if special breeding result (handled later)
            if (breedingResultData != null && variant.hasBreedingResultModifier()) {
                i.remove();
                continue;
            }

            // Discard if variant is discardable
            if (variant.shouldDiscard(random)) {
                i.remove();
            }
        }

        // Create weighted bag from variants
        VariantBag bag = new VariantBag(mob, random, variants);

        // If we've been provided 2 parents
        if (breedingResultData != null) {
            // Collect all specialized breeding combination results
            List<MobVariant> possibleVariants = new ArrayList<>();
            for (MobVariant v : variants) {
                if (v.hasBreedingResultModifier() && v.canBreed(breedingResultData.parent1(), breedingResultData.parent2())) {
                    possibleVariants.add(v);
                }
            }

            // If there are no specialized results, handle generic breeding case
            if (possibleVariants.isEmpty()) {
                if (random.nextDouble() >= VariantSettings.getChildRandomVariantChance()) {
                    return random.nextBoolean() ? breedingResultData.parent1() : breedingResultData.parent2();
                }
            } else { // If there are specialized results, switch to that pool
                bag = new VariantBag(mob, random, possibleVariants);
            }
        }

        return bag.getRandomEntry();
    }

    public static MobVariant getChildVariant(Mob mob, ServerWorld world, PassiveEntity parent1, PassiveEntity parent2) {
        // Collect data about parents
        NbtCompound parent1Nbt = new NbtCompound();
        parent1.writeNbt(parent1Nbt);
        NbtCompound parent2Nbt = new NbtCompound();
        parent2.writeNbt(parent2Nbt);

        if (parent1Nbt.contains("Variant") && parent2Nbt.contains("Variant")) {
            String[] parent1VariantId = parent1Nbt.getString("Variant").split(":");
            MobVariant parent1Variant = Variants.getVariant(mob, new Identifier(parent1VariantId[0], parent1VariantId[1]));
            String[] parent2VariantId = parent2Nbt.getString("Variant").split(":");
            MobVariant parent2Variant = Variants.getVariant(mob, new Identifier(parent2VariantId[0], parent2VariantId[1]));

            return Variants.getRandomVariant(mob, world.getRandom(), world.getBiome(parent1.getBlockPos()), new BreedingResultData(parent1Variant, parent2Variant));
        } else {
            return Variants.getRandomVariant(mob, world.getRandom(), world.getBiome(parent1.getBlockPos()), null);
        }
    }

    public enum Mob {
        CAT("cat"),
        CHICKEN("chicken"),
        COW("cow"),
        PIG("pig"),
        SHEEP("sheep"),
        SPIDER("spider"),
        WOLF("wolf"),
        ZOMBIE("zombie"),
        NULL("null");

        private final String id;

        Mob(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    static {
        defaultVariants = new HashMap<Mob, ArrayList<MobVariant>>();
        defaultVariants.put(Mob.CHICKEN, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("midnight"), 1),
                new MobVariant(MoreMobVariants.id("amber"), 2),
                new MobVariant(MoreMobVariants.id("gold_crested"), 2),
                new MobVariant(MoreMobVariants.id("bronzed"), 2),
                new MobVariant(MoreMobVariants.id("skewbald"), 2),
                new MobVariant(MoreMobVariants.id("stormy"), 2),
                new MobVariant(MoreMobVariants.id("bone"), 1)
                        .addModifier(new SpawnableBiomesModifier(BiomeTags.IS_NETHER))
                        .addModifier(new DiscardableModifier(0.8333)),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
        defaultVariants.put(Mob.COW, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("umbra"), 1),
                new MobVariant(MoreMobVariants.id("ashen"), 2),
                new MobVariant(MoreMobVariants.id("cookie"), 2),
                new MobVariant(MoreMobVariants.id("dairy"), 2),
                new MobVariant(MoreMobVariants.id("pinto"), 2),
                new MobVariant(MoreMobVariants.id("sunset"), 2),
                new MobVariant(MoreMobVariants.id("wooly"), 2),
                new MobVariant(MoreMobVariants.id("albino"), 1)
                        .addModifier(new ShinyModifier())
                        .addModifier(new DiscardableModifier(0.9))
                        .addModifier(new SpawnableBiomesModifier(BiomeTags.IS_TAIGA)),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
        defaultVariants.put(Mob.PIG, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("mottled"), 1),
                new MobVariant(MoreMobVariants.id("piebald"), 1),
                new MobVariant(MoreMobVariants.id("pink_footed"), 1),
                new MobVariant(MoreMobVariants.id("sooty"), 1),
                new MobVariant(MoreMobVariants.id("spotted"), 1),
                new MobVariant(MoreMobVariants.id("default"), 2)
        )));
        defaultVariants.put(Mob.SHEEP, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("patched"), 1)
                        .addModifier(new CustomWoolModifier()),
                new MobVariant(MoreMobVariants.id("fuzzy"), 1)
                        .addModifier(new CustomWoolModifier()),
                new MobVariant(MoreMobVariants.id("rocky"), 1)
                        .addModifier(new CustomWoolModifier()),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
        defaultVariants.put(Mob.SPIDER, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("brown"), 3)
                        .addModifier(new CustomEyesModifier()),
                new MobVariant(MoreMobVariants.id("tarantula"), 2),
                new MobVariant(MoreMobVariants.id("black_widow"), 1),
                new MobVariant(MoreMobVariants.id("default"), 5)
        )));
        defaultVariants.put(Mob.WOLF, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("jupiter"), 1),
                new MobVariant(MoreMobVariants.id("husky"), 1),
                new MobVariant(MoreMobVariants.id("default"), 1),
                new MobVariant(MoreMobVariants.id("german_shepherd"), 1)
                        .addModifier(new BreedingResultModifier(
                                getVariant(Mob.WOLF, MoreMobVariants.id("husky")),
                                getVariant(Mob.WOLF, MoreMobVariants.id("jupiter")),
                                0.5)),
                new MobVariant(MoreMobVariants.id("golden_retriever"), 1)
                        .addModifier(new BreedingResultModifier(
                                getVariant(Mob.WOLF, MoreMobVariants.id("jupiter")),
                                getVariant(Mob.WOLF, MoreMobVariants.id("default")),
                                0.5)),
                new MobVariant(MoreMobVariants.id("french_bulldog"), 1)
                        .addModifier(new BreedingResultModifier(
                                getVariant(Mob.WOLF, MoreMobVariants.id("husky")),
                                getVariant(Mob.WOLF, MoreMobVariants.id("golden_retriever")),
                                0.5))
        )));
        defaultVariants.put(Mob.ZOMBIE, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("alex"), 2),
                new MobVariant(MoreMobVariants.id("ari"), 1),
                new MobVariant(MoreMobVariants.id("efe"), 1),
                new MobVariant(MoreMobVariants.id("kai"), 1),
                new MobVariant(MoreMobVariants.id("makena"), 1),
                new MobVariant(MoreMobVariants.id("noor"), 1),
                new MobVariant(MoreMobVariants.id("sunny"), 1),
                new MobVariant(MoreMobVariants.id("zuri"), 1),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
    }
}
