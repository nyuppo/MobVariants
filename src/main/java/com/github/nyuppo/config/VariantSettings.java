package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;

public class VariantSettings {
    private static boolean enableMuddyPigs;
    private static int wolfBreedingChance;

    public static void setEnableMuddyPigs(boolean shouldEnableMuddyPigs) {
        enableMuddyPigs = shouldEnableMuddyPigs;
    }

    public static boolean getEnableMuddyPigs() {
        return enableMuddyPigs;
    }

    public static void setWolfBreedingChance(int chance) {
        wolfBreedingChance = chance;
        if (wolfBreedingChance < 0) {
            wolfBreedingChance = 0;
        } else if (wolfBreedingChance > 10) {
            wolfBreedingChance = 10;
        }
    }

    public static int getWolfBreedingChance() {
        return wolfBreedingChance;
    }

    public static void resetSettings() {
        enableMuddyPigs = true;
        wolfBreedingChance = 5;
    }

    static {
        enableMuddyPigs = true;
        wolfBreedingChance = 5;
    }
}
