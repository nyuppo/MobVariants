package com.github.nyuppo.config;

public class VariantSettings {
    private static boolean enableMuddyPigs;
    private static double childRandomVariantChance;
    private static float wolfBreedingChance;

    public static void setEnableMuddyPigs(boolean shouldEnableMuddyPigs) {
        enableMuddyPigs = shouldEnableMuddyPigs;
    }

    public static boolean getEnableMuddyPigs() {
        return enableMuddyPigs;
    }

    public static void setWolfBreedingChance(int chance) {
        wolfBreedingChance = chance;
        if (wolfBreedingChance < 0.0f) {
            wolfBreedingChance = 0.0f;
        } else if (wolfBreedingChance > 1.0f) {
            wolfBreedingChance = 1.0f;
        }
    }

    public static float getWolfBreedingChance() {
        return wolfBreedingChance;
    }

    public static double getChildRandomVariantChance() {
        return childRandomVariantChance;
    }

    public static void resetSettings() {
        enableMuddyPigs = true;
        wolfBreedingChance = 0.5f;
        childRandomVariantChance = 0.25d;
    }

    static {
        enableMuddyPigs = true;
        wolfBreedingChance = 0.5f;
        childRandomVariantChance = 0.25d;
    }
}
