package com.github.nyuppo.config;

import java.util.ArrayList;

public class VariantBlacklist {
    private static ArrayList<String> catBlacklist;
    private static ArrayList<String> chickenBlacklist;
    private static ArrayList<String> cowBlacklist;
    private static ArrayList<String> pigBlacklist;
    private static ArrayList<String> sheepBlacklist;
    private static ArrayList<String> spiderBlacklist;
    private static ArrayList<String> wolfBlacklist;
    private static ArrayList<String> zombieBlacklist;

    public static void blacklistVariant(String mob, String variant) {
        switch (mob) {
            case "cat" -> {
                if (!catBlacklist.contains(variant)) catBlacklist.add(variant);
            }
            case "chicken" -> {
                if (!chickenBlacklist.contains(variant)) chickenBlacklist.add(variant);
            }
            case "cow" -> {
                if (!cowBlacklist.contains(variant)) cowBlacklist.add(variant);
            }
            case "pig" -> {
                if (!pigBlacklist.contains(variant)) pigBlacklist.add(variant);
            }
            case "sheep" -> {
                if (!sheepBlacklist.contains(variant)) sheepBlacklist.add(variant);
            }
            case "spider" -> {
                if (!spiderBlacklist.contains(variant)) spiderBlacklist.add(variant);
            }
            case "wolf" -> {
                if (!wolfBlacklist.contains(variant)) wolfBlacklist.add(variant);
            }
            case "zombie" -> {
                if (!zombieBlacklist.contains(variant)) zombieBlacklist.add(variant);
            }
        }
    }

    public static boolean isBlacklisted(String mob, String variant) {
        return switch (mob) {
            case "cat" -> catBlacklist.contains(variant);
            case "chicken" -> chickenBlacklist.contains(variant);
            case "cow" -> cowBlacklist.contains(variant);
            case "pig" -> pigBlacklist.contains(variant);
            case "sheep" -> sheepBlacklist.contains(variant);
            case "spider" -> spiderBlacklist.contains(variant);
            case "wolf" -> wolfBlacklist.contains(variant);
            case "zombie" -> zombieBlacklist.contains(variant);
            default -> false;
        };
    }

    public static void resetBlacklist(String mob) {
        switch (mob) {
            case "cat" -> catBlacklist.clear();
            case "chicken" -> chickenBlacklist.clear();
            case "cow" -> cowBlacklist.clear();
            case "pig" -> pigBlacklist.clear();
            case "sheep" -> sheepBlacklist.clear();
            case "spider" -> spiderBlacklist.clear();
            case "wolf" -> wolfBlacklist.clear();
            case "zombie" -> zombieBlacklist.clear();
        }
    }

    public static void resetBlacklists() {
        catBlacklist.clear();
        chickenBlacklist.clear();
        cowBlacklist.clear();
        pigBlacklist.clear();
        sheepBlacklist.clear();
        spiderBlacklist.clear();
        wolfBlacklist.clear();
        zombieBlacklist.clear();
    }

    static {
        catBlacklist = new ArrayList<String>();
        chickenBlacklist = new ArrayList<String>();
        cowBlacklist = new ArrayList<String>();
        pigBlacklist = new ArrayList<String>();
        sheepBlacklist = new ArrayList<String>();
        spiderBlacklist = new ArrayList<String>();
        wolfBlacklist = new ArrayList<String>();
        zombieBlacklist = new ArrayList<String>();
    }
}
