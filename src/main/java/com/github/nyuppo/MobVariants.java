package com.github.nyuppo;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.tag.CatVariantTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MobVariants implements ModInitializer {
    public static final String MOD_ID = "mobvariants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Cat variants
    public static final CatVariant GRAY_TABBY = new CatVariant(new Identifier(MOD_ID, "textures/entity/cat/gray_tabby.png"));
    public static final CatVariant DOUG = new CatVariant(new Identifier(MOD_ID, "textures/entity/cat/doug.png"));
    public static final CatVariant HANDSOME = new CatVariant(new Identifier(MOD_ID, "textures/entity/cat/handsome.png"));

    @Override
    public void onInitialize() {
        LOGGER.info("Giving mobs a fresh coat of paint...");
        
        // Register cat variants
        Registry.register(Registry.CAT_VARIANT, new Identifier(MOD_ID, "gray_tabby"), GRAY_TABBY);
        Registry.register(Registry.CAT_VARIANT, new Identifier(MOD_ID, "doug"), DOUG);
        Registry.register(Registry.CAT_VARIANT, new Identifier(MOD_ID, "handsome"), HANDSOME);
    }
}
