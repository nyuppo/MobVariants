package com.github.nyuppo.variant;

public record MoonPhaseModifier(float minimumMoonSize) implements VariantModifier {
    public boolean canSpawn(float moonSize) {
        return moonSize > minimumMoonSize;
    }
}
