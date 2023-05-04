package com.github.nyuppo.config;

import com.github.nyuppo.MoreMobVariants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigDataLoader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(MoreMobVariants.MOD_ID, MoreMobVariants.MOD_ID);
    }

    @Override
    public void reload(ResourceManager manager) {
        MoreMobVariants.LOGGER.info("Reloading config...");
        VariantWeights.clearWeights();
        VariantBlacklist.resetBlacklists();

        for (Identifier id : manager.findResources("weights", path -> path.getPath().endsWith(".json")).keySet()) {
            String target = id.getPath().substring(8, id.getPath().length() - 5);
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                applyWeight(id, new InputStreamReader(stream, StandardCharsets.UTF_8));
            } catch (Exception e) {
                MoreMobVariants.LOGGER.error("Error occured while loading weight config " + id.toShortTranslationKey(), e);
                VariantWeights.resetWeight(target);
            }
        }

        for (Identifier id : manager.findResources("blacklist", path -> path.getPath().endsWith(".json")).keySet()) {
            String target = id.getPath().substring(10, id.getPath().length() - 5);
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                applyBlacklist(id, new InputStreamReader(stream, StandardCharsets.UTF_8));
            } catch (Exception e) {
                MoreMobVariants.LOGGER.error("Error occured while loading blacklist config " + id.toShortTranslationKey(), e);
                VariantBlacklist.resetBlacklist(target);
            }
        }

        VariantWeights.applyBlacklists();
    }

    private void applyWeight(Identifier identifier, Reader reader) {
        String target = identifier.getPath().substring(8, identifier.getPath().length() - 5);
        JsonElement element = JsonParser.parseReader(reader);

        if (element.getAsJsonObject().size() != 0) {
            if (element.getAsJsonObject().has("weights")) {
                Map<String, JsonElement> weights = element.getAsJsonObject().get("weights").getAsJsonObject().asMap();
                HashMap<String, Integer> weightsConverted = new HashMap<String, Integer>();
                for (Map.Entry entry : weights.entrySet()) {
                    weightsConverted.put(entry.getKey().toString(), ((JsonElement)entry.getValue()).getAsInt());
                }
                VariantWeights.setWeight(target, weightsConverted);
            }
        }
    }

    private void applyBlacklist(Identifier identifier, Reader reader) {
        String target = identifier.getPath().substring(10, identifier.getPath().length() - 5);
        JsonElement element = JsonParser.parseReader(reader);

        if (element.getAsJsonObject().size() != 0) {
            if (element.getAsJsonObject().has("blacklist")) {
                JsonArray blacklist = element.getAsJsonObject().get("blacklist").getAsJsonArray();
                for (JsonElement entry : blacklist) {
                    VariantBlacklist.blacklistVariant(target, entry.getAsString());
                }
            }
        }
    }
}
