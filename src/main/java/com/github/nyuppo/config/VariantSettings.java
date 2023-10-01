package com.github.nyuppo.config;

public class VariantSettings {
    private static boolean enableMuddyPigs; // Should pigs be able to get muddy/wash their mud off
    private static int muddyPigTimeout; // How long pigs should stay muddy for in seconds (0 to disable)
    private static double childRandomVariantChance; // The chance to get a random variant instead of inheriting from parents

    public static void setEnableMuddyPigs(boolean shouldEnableMuddyPigs) {
        enableMuddyPigs = shouldEnableMuddyPigs;
    }

    public static boolean getEnableMuddyPigs() {
        return enableMuddyPigs;
    }

    public static void setMuddyPigTimeout(int seconds) {
        muddyPigTimeout = seconds;
    }

    public static int getMuddyPigTimeout() {
        return muddyPigTimeout;
    }

    public static void setChildRandomVariantChance(double chance) {
        childRandomVariantChance = chance;
    }

    public static double getChildRandomVariantChance() {
        return childRandomVariantChance;
    }

    public static void resetSettings() {
        enableMuddyPigs = true;
        muddyPigTimeout = 0;
        childRandomVariantChance = 0.25d;
    }

    static {
        enableMuddyPigs = true;
        muddyPigTimeout = 0;
        childRandomVariantChance = 0.25d;
    }
}
