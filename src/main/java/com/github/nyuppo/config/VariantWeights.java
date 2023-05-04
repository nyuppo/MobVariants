package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.util.WeightedRandomBag;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VariantWeights {
    private static HashMap<String, Integer> chickenWeights;
    private static HashMap<String, Integer> cowWeights;
    private static HashMap<String, Integer> pigWeights;
    private static HashMap<String, Integer> sheepWeights;
    private static HashMap<String, Integer> wolfWeights;
    private static HashMap<String, Integer> zombieWeights;

    private static final HashMap<String, Integer> defaultChickenWeights;
    private static final HashMap<String, Integer> defaultCowWeights;
    private static final HashMap<String, Integer> defaultPigWeights;
    private static final HashMap<String, Integer> defaultSheepWeights;
    private static final HashMap<String, Integer> defaultWolfWeights;
    private static final HashMap<String, Integer> defaultZombieWeights;

    public static void setChickenWeights(HashMap<String, Integer> weights) {
        chickenWeights = weights;
    }

    public static HashMap<String, Integer> getChickenWeights() {
        return chickenWeights;
    }

    public static void setCowWeights(HashMap<String, Integer> weights) {
        cowWeights = weights;
    }

    public static HashMap<String, Integer> getCowWeights() {
        return cowWeights;
    }

    public static void setPigWeights(HashMap<String, Integer> weights) {
        pigWeights = weights;
    }

    public static HashMap<String, Integer> getPigWeights() {
        return pigWeights;
    }

    public static void setSheepWeights(HashMap<String, Integer> weights) {
        sheepWeights = weights;
    }

    public static HashMap<String, Integer> getSheepWeights() {
        return sheepWeights;
    }

    public static void setWolfWeights(HashMap<String, Integer> weights) {
        wolfWeights = weights;
    }

    public static HashMap<String, Integer> getWolfWeights() {
        return wolfWeights;
    }

    public static void setZombieWeights(HashMap<String, Integer> weights) {
        zombieWeights = weights;
    }

    public static HashMap<String, Integer> getZombieWeights() {
        return zombieWeights;
    }

    public static void resetChickenWeights() {
        chickenWeights = defaultChickenWeights;
    }

    public static void resetCowWeights() {
        cowWeights = defaultCowWeights;
    }

    public static void resetPigWeights() {
        pigWeights = defaultPigWeights;
    }

    public static void resetSheepWeights() {
        sheepWeights = defaultSheepWeights;
    }

    public static void resetWolfWeights() {
        wolfWeights = defaultWolfWeights;
    }

    public static void resetZombieWeights() {
        zombieWeights = defaultZombieWeights;
    }

    public static void setWeight(String mob, HashMap<String, Integer> weights) {
        switch (mob) {
            case "chicken" -> setChickenWeights(weights);
            case "cow" -> setCowWeights(weights);
            case "pig" -> setPigWeights(weights);
            case "sheep" -> setSheepWeights(weights);
            case "wolf" -> setWolfWeights(weights);
            case "zombie" -> setZombieWeights(weights);
        }
    }

    public static void resetWeight(String mob) {
        switch (mob) {
            case "chicken" -> resetChickenWeights();
            case "cow" -> resetCowWeights();
            case "pig" -> resetPigWeights();
            case "sheep" -> resetSheepWeights();
            case "wolf" -> resetWolfWeights();
            case "zombie" -> resetZombieWeights();
        }
    }

    public static String getRandomVariant(String mob, Random random) {
        HashMap<String, Integer> weights = switch (mob) {
            case "chicken" -> getChickenWeights();
            case "cow" -> getCowWeights();
            case "pig" -> getPigWeights();
            case "sheep" -> getSheepWeights();
            case "wolf" -> getWolfWeights();
            case "zombie" -> getZombieWeights();
            default -> new HashMap<>();
        };

        if (weights.isEmpty()) {
            return "default";
        }

        WeightedRandomBag<String> bag = new WeightedRandomBag<String>(random, weights);
        return bag.getRandom();
    }

    public static void resetWeights() {
        resetChickenWeights();
        resetCowWeights();
        resetPigWeights();
        resetSheepWeights();
        resetWolfWeights();
        resetZombieWeights();
    }

    public static void clearWeights() {
        chickenWeights = new HashMap<String, Integer>();
        cowWeights = new HashMap<String, Integer>();
        pigWeights = new HashMap<String, Integer>();
        sheepWeights = new HashMap<String, Integer>();
        wolfWeights = new HashMap<String, Integer>();
        zombieWeights = new HashMap<String, Integer>();
    }

    public static void applyBlacklists() {
        Iterator<String> i;
        String variant;

        i = chickenWeights.keySet().iterator();
        while (i.hasNext()) {
            variant = i.next();
            if (VariantBlacklist.isBlacklisted("chicken", variant)) {
                i.remove();
            }
        }

        i = cowWeights.keySet().iterator();
        while (i.hasNext()) {
            variant = i.next();
            if (VariantBlacklist.isBlacklisted("cow", variant)) {
                i.remove();
            }
        }

        i = pigWeights.keySet().iterator();
        while (i.hasNext()) {
            variant = i.next();
            if (VariantBlacklist.isBlacklisted("pig", variant)) {
                i.remove();
            }
        }

        i = sheepWeights.keySet().iterator();
        while (i.hasNext()) {
            variant = i.next();
            if (VariantBlacklist.isBlacklisted("sheep", variant)) {
                i.remove();
            }
        }

        i = wolfWeights.keySet().iterator();
        while (i.hasNext()) {
            variant = i.next();
            if (VariantBlacklist.isBlacklisted("wolf", variant)) {
                i.remove();
            }
        }

        i = zombieWeights.keySet().iterator();
        while (i.hasNext()) {
            variant = i.next();
            if (VariantBlacklist.isBlacklisted("zombie", variant)) {
                i.remove();
            }
        }
    }

    static {
        chickenWeights = new HashMap<String, Integer>();
        cowWeights = new HashMap<String, Integer>();
        pigWeights = new HashMap<String, Integer>();
        sheepWeights = new HashMap<String, Integer>();
        wolfWeights = new HashMap<String, Integer>();
        zombieWeights = new HashMap<String, Integer>();

        defaultChickenWeights = new HashMap<String, Integer>();
        defaultChickenWeights.put("midnight", 1);
        defaultChickenWeights.put("amber", 2);
        defaultChickenWeights.put("gold_crested", 2);
        defaultChickenWeights.put("bronzed", 2);
        defaultChickenWeights.put("skewbald", 2);
        defaultChickenWeights.put("stormy", 2);
        defaultChickenWeights.put("default", 3);
        defaultCowWeights = new HashMap<String, Integer>();
        defaultCowWeights.put("umbra", 1);
        defaultCowWeights.put("ashen", 2);
        defaultCowWeights.put("cookie", 2);
        defaultCowWeights.put("dairy", 2);
        defaultCowWeights.put("pinto", 2);
        defaultCowWeights.put("sunset", 2);
        defaultCowWeights.put("wooly", 2);
        defaultCowWeights.put("default", 3);
        defaultPigWeights = new HashMap<String, Integer>();
        defaultPigWeights.put("mottled", 1);
        defaultPigWeights.put("piebald", 1);
        defaultPigWeights.put("pink_footed", 1);
        defaultPigWeights.put("sooty", 1);
        defaultPigWeights.put("spotted", 1);
        defaultPigWeights.put("default", 2);
        defaultSheepWeights = new HashMap<String, Integer>();
        defaultSheepWeights.put("patched", 1);
        defaultSheepWeights.put("fuzzy", 1);
        defaultSheepWeights.put("rocky", 1);
        defaultSheepWeights.put("default", 2);
        defaultWolfWeights = new HashMap<String, Integer>();
        defaultWolfWeights.put("jupiter", 1);
        defaultWolfWeights.put("husky", 1);
        defaultWolfWeights.put("default", 1);
        defaultZombieWeights = new HashMap<String, Integer>();
        defaultZombieWeights.put("alex", 2);
        defaultZombieWeights.put("ari", 1);
        defaultZombieWeights.put("efe", 1);
        defaultZombieWeights.put("kai", 1);
        defaultZombieWeights.put("makena", 1);
        defaultZombieWeights.put("noor", 1);
        defaultZombieWeights.put("sunny", 1);
        defaultZombieWeights.put("zuri", 1);
        defaultZombieWeights.put("default", 3);
    }
}
