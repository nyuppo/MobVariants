package com.github.nyuppo;

import com.github.nyuppo.config.ConfigDataLoader;
import com.github.nyuppo.polymer.PolymerCatVariant;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreMobVariants implements ModInitializer {
    public static final String MOD_ID = "moremobvariants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier MMB_HELLO_PACKET = new Identifier(MOD_ID, "hello");

    // Cat variants
    public static final CatVariant GRAY_TABBY = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/gray_tabby.png"));
    public static final CatVariant DOUG = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/doug.png"));
    public static final CatVariant HANDSOME = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/handsome.png"));
    public static final CatVariant TORTOISESHELL = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/tortoiseshell.png"));

    // Pig mud tag
    public static final TagKey<Block> PIG_MUD_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "pig_mud_blocks"));

    // Biome tags
    public static final TagKey<Biome> SPAWN_MOSSY_SKELETONS = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "spawn_mossy_skeletons"));
    public static final TagKey<Biome> INCREASED_SANDY_SKELETONS = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "increased_sandy_skeletons"));


    @Override
    public void onInitialize() {
        LOGGER.info("Giving mobs a fresh coat of paint...");

        // Config
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ConfigDataLoader());

        // Register cat variants
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "gray_tabby"), GRAY_TABBY);
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "doug"), DOUG);
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "handsome"), HANDSOME);
        Registry.register(Registries.CAT_VARIANT, new Identifier(MOD_ID, "tortoiseshell"), TORTOISESHELL);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
