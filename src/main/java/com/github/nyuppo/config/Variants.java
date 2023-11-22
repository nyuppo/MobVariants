package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.util.BreedingResultData;
import com.github.nyuppo.util.VariantBag;
import com.github.nyuppo.variant.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Variants {
    private static HashMap<EntityType<?>, ArrayList<MobVariant>> variants;
    private static HashMap<EntityType<?>, ArrayList<MobVariant>> defaultVariants;

    public static void addVariant(EntityType<?> mob, MobVariant variant) {
        if (variants.get(mob) == null) {
            variants.put(mob, new ArrayList<MobVariant>());
        }

        variants.get(mob).add(variant);
    }

    public static ArrayList<MobVariant> getVariants(EntityType<?> mob) {
        if (variants.get(mob) != null) {
            return new ArrayList<>(variants.get(mob));
        }

        return new ArrayList<>(defaultVariants.get(mob));
    }

    public static ArrayList<MobVariant> getDefaultVariants(EntityType<?> mob) {
        return new ArrayList<>(defaultVariants.get(mob));
    }

    public static HashMap<EntityType<?>, ArrayList<MobVariant>> getAllVariants() {
        return variants;
    }

    public static HashMap<EntityType<?>, ArrayList<MobVariant>> getAllDefaultVariants() {
        return defaultVariants;
    }

    public static MobVariant getDefaultVariant(EntityType<?> mob) {
        ArrayList<MobVariant> variants = getVariants(mob);
        for (MobVariant variant : variants) {
            if (variant.getIdentifier().equals(MoreMobVariants.id("default"))) {
                return variant;
            }
        }
        return new MobVariant(MoreMobVariants.id("default"), 1);
    }

    public static MobVariant getVariant(EntityType<?> mob, Identifier identifier) {
        ArrayList<MobVariant> variants = getVariants(mob);

        for (MobVariant variant : variants) {
            if (variant.getIdentifier().equals(identifier)) {
                return variant;
            }
        }
        return getDefaultVariant(mob);
    }

    public static void resetVariants(EntityType<?> mob) {
        variants.remove(mob);
        variants.put(mob, defaultVariants.get(mob));
    }

    public static void clearVariants(EntityType<?> mob) {
        variants.remove(mob);
        variants.put(mob, new ArrayList<>());
    }

    public static void clearAllVariants() {
        variants.clear();
    }

    public static void validateEmptyVariants() {
        if (!variants.keySet().isEmpty()) {
            variants.keySet().forEach((mob) -> {
                if (variants.get(mob).isEmpty()) {
                    resetVariants(mob);
                }
            });
        }
    }

    public static void applyBlacklists() {
        variants.keySet().forEach((EntityType<?> mob) -> {
            if (mob.equals(EntityType.CAT) || variants.get(mob) == null) {
                return;
            }

            List<MobVariant> variantsList = variants.get(mob);
            if (variantsList.isEmpty()) {
                return;
            }

            Iterator<MobVariant> i = variantsList.iterator();
            MobVariant variant;
            while (i.hasNext()) {
                variant = i.next();
                if (VariantBlacklist.isBlacklisted(mob, variant.getIdentifier())) {
                    i.remove();
                }
            }
        });
    }

    public static EntityType<?> getMob(String mobId) {
        Optional<EntityType<?>> entityType = EntityType.get(mobId);
        if (entityType.isPresent()) {
            return entityType.get();
        }

        throw new IllegalArgumentException("Unknown mob identifier: " + mobId);
    }

    public static MobVariant getRandomVariant(EntityType<?> mob, Random random, @Nullable RegistryEntry<Biome> spawnBiome, @Nullable BreedingResultData breedingResultData) {
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
            if (variant.hasBreedingResultModifier()) {
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
            for (MobVariant v : getVariants(mob)) {
                if (v.hasBreedingResultModifier() && v.canBreed(breedingResultData.parent1(), breedingResultData.parent2()) && v.shouldBreed(random)) {
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

    public static MobVariant getChildVariant(EntityType<?> mob, ServerWorld world, PassiveEntity parent1, PassiveEntity parent2) {
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

    public static String[] splitVariant(String namespacedVariant) {
        String[] split = namespacedVariant.split(":");

        if (split.length == 1) {
            MoreMobVariants.LOGGER.warn("Passed in non-namespaced variant id '" + namespacedVariant + "'. Auto-assigning to moremobvariants:" + namespacedVariant);

            return new String[]{"moremobvariants", split[0]};
        }

        return split;
    }

    static {
        variants = new HashMap<EntityType<?>, ArrayList<MobVariant>>();

        defaultVariants = new HashMap<EntityType<?>, ArrayList<MobVariant>>();
        defaultVariants.put(EntityType.CHICKEN, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("midnight"), 1),
                new MobVariant(MoreMobVariants.id("amber"), 2),
                new MobVariant(MoreMobVariants.id("gold_crested"), 2),
                new MobVariant(MoreMobVariants.id("bronzed"), 2),
                new MobVariant(MoreMobVariants.id("skewbald"), 2),
                new MobVariant(MoreMobVariants.id("stormy"), 2),
                new MobVariant(MoreMobVariants.id("bone"), 2)
                        .addModifier(new SpawnableBiomesModifier(BiomeTags.IS_NETHER)),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
        defaultVariants.put(EntityType.COW, new ArrayList<>(List.of(
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
                new MobVariant(MoreMobVariants.id("cream"), 1)
                        .addModifier(new ShinyModifier())
                        .addModifier(new DiscardableModifier(0.8)),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
        defaultVariants.put(EntityType.PIG, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("mottled"), 1),
                new MobVariant(MoreMobVariants.id("piebald"), 1),
                new MobVariant(MoreMobVariants.id("pink_footed"), 1),
                new MobVariant(MoreMobVariants.id("sooty"), 1),
                new MobVariant(MoreMobVariants.id("spotted"), 1),
                new MobVariant(MoreMobVariants.id("default"), 2)
        )));
        defaultVariants.put(EntityType.SHEEP, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("patched"), 1)
                        .addModifier(new CustomWoolModifier()),
                new MobVariant(MoreMobVariants.id("fuzzy"), 1)
                        .addModifier(new CustomWoolModifier()),
                new MobVariant(MoreMobVariants.id("rocky"), 1)
                        .addModifier(new CustomWoolModifier()),
                new MobVariant(MoreMobVariants.id("default"), 3)
        )));
        defaultVariants.put(EntityType.SKELETON, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("dungeons"), 3),
                new MobVariant(MoreMobVariants.id("weathered"), 2),
                new MobVariant(MoreMobVariants.id("sandy"), 1),
                new MobVariant(MoreMobVariants.id("mossy"), 2)
                        .addModifier(new SpawnableBiomesModifier(MoreMobVariants.SPAWN_MOSSY_SKELETONS)),
                new MobVariant(MoreMobVariants.id("sandy_increased_spawns_in_deserts"), 3)
                        .addModifier(new SpawnableBiomesModifier(MoreMobVariants.INCREASED_SANDY_SKELETONS))
                        .addModifier(new CustomVariantNameModifier("sandy")),
                new MobVariant(MoreMobVariants.id("default"), 4)
        )));
        defaultVariants.put(EntityType.SPIDER, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("bone"), 1)
                        .addModifier(new ShinyModifier())
                        .addModifier(new DiscardableModifier(0.8))
                        .addModifier(new CustomEyesModifier()),
                new MobVariant(MoreMobVariants.id("brown"), 3)
                        .addModifier(new CustomEyesModifier()),
                new MobVariant(MoreMobVariants.id("tarantula"), 2),
                new MobVariant(MoreMobVariants.id("black_widow"), 1),
                new MobVariant(MoreMobVariants.id("default"), 5)
        )));
        defaultVariants.put(EntityType.WOLF, new ArrayList<>(List.of(
                new MobVariant(MoreMobVariants.id("jupiter"), 1),
                new MobVariant(MoreMobVariants.id("husky"), 1),
                new MobVariant(MoreMobVariants.id("default"), 1),
                new MobVariant(MoreMobVariants.id("german_shepherd"), 1)
                        .addModifier(new BreedingResultModifier(
                                MoreMobVariants.id("husky"),
                                MoreMobVariants.id("jupiter"),
                                0.5)),
                new MobVariant(MoreMobVariants.id("golden_retriever"), 1)
                        .addModifier(new BreedingResultModifier(
                                MoreMobVariants.id("jupiter"),
                                MoreMobVariants.id("default"),
                                0.5)),
                new MobVariant(MoreMobVariants.id("french_bulldog"), 1)
                        .addModifier(new BreedingResultModifier(
                                MoreMobVariants.id("husky"),
                                MoreMobVariants.id("golden_retriever"),
                                0.5)),
                new MobVariant(MoreMobVariants.id("black"), 1)
                        .addModifier(new BreedingResultModifier(
                                MoreMobVariants.id("husky"),
                                MoreMobVariants.id("german_shepherd"),
                                0.5)),
                new MobVariant(MoreMobVariants.id("basenji"), 1)
                        .addModifier(new BreedingResultModifier(
                                MoreMobVariants.id("jupiter"),
                                MoreMobVariants.id("golden_retriever"),
                                0.5)),
                new MobVariant(MoreMobVariants.id("white_terrier"), 1)
                        .addModifier(new BreedingResultModifier(
                                MoreMobVariants.id("default"),
                                MoreMobVariants.id("golden_retriever"),
                                0.5))
        )));
        defaultVariants.put(EntityType.ZOMBIE, new ArrayList<>(List.of(
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
