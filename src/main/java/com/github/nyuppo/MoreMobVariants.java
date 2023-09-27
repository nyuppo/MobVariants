package com.github.nyuppo;

import com.github.nyuppo.config.ConfigDataLoader;
import com.github.nyuppo.networking.ICanHasMoreMobVariantsPayload;
import com.github.nyuppo.polymer.PolymerCatVariant;
import eu.pb4.polymer.networking.api.PolymerNetworking;
import eu.pb4.polymer.networking.api.server.PolymerServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreMobVariants implements ModInitializer {
    public static final String MOD_ID = "moremobvariants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier HAS_MORE_MOB_VARIANTS = new Identifier(MOD_ID, "hasmoremobvariants");

    // Cat variants
    public static final CatVariant GRAY_TABBY = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/gray_tabby.png"));
    public static final CatVariant DOUG = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/doug.png"));
    public static final CatVariant HANDSOME = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/handsome.png"));
    public static final CatVariant TORTOISESHELL = new PolymerCatVariant(new Identifier(MOD_ID, "textures/entity/cat/tortoiseshell.png"));

    // Pig mud tag
    public static final TagKey<Block> PIG_MUD_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "pig_mud_blocks"));

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
        PolymerNetworking.registerCommonPayload(HAS_MORE_MOB_VARIANTS, 0, ICanHasMoreMobVariantsPayload::readPacket);

    }

    public static boolean hasClientMod(@Nullable ServerPlayerEntity player) {
        if(player != null &&  player.networkHandler != null) {
            var version = PolymerServerNetworking.getSupportedVersion(player.networkHandler, HAS_MORE_MOB_VARIANTS);
            return version == 0;
        }
        return false;
    }
}
