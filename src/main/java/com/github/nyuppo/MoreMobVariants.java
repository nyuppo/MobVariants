package com.github.nyuppo;

import com.github.nyuppo.config.ConfigDataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


public class MoreMobVariants implements ModInitializer {
    public static final String MOD_ID = "moremobvariants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Cat variants
    public static final CatVariant GRAY_TABBY = new CatVariant(new Identifier(MOD_ID, "textures/entity/cat/gray_tabby.png"));
    public static final CatVariant DOUG = new CatVariant(new Identifier(MOD_ID, "textures/entity/cat/doug.png"));
    public static final CatVariant HANDSOME = new CatVariant(new Identifier(MOD_ID, "textures/entity/cat/handsome.png"));

    @Override
    public void onInitialize() {
        LOGGER.info("Giving mobs a fresh coat of paint...");

        // Config
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ConfigDataLoader());
        
        // Register cat variants
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "gray_tabby"), GRAY_TABBY);
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "doug"), DOUG);
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "handsome"), HANDSOME);
    }
}
