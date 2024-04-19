package com.github.nyuppo;

import com.github.nyuppo.config.ConfigDataLoader;
import com.github.nyuppo.networking.MMVNetworkingConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MoreMobVariants implements ModInitializer {
    public static final String MOD_ID = "moremobvariants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // NBT keys
    public static final String NBT_KEY = "VariantID";
    public static final String MUDDY_NBT_KEY = "IsMuddy"; // Muddy pigs
    public static final String MUDDY_TIMEOUT_NBT_KEY = "MuddyTimeLeft"; // Muddy pigs
    public static final String SHEEP_HORN_COLOUR_NBT_KEY = "HornColour";

    // Pig mud tag
    public static final TagKey<Block> PIG_MUD_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "pig_mud_blocks"));

    // Biome tags
    public static final TagKey<Biome> SPAWN_MOSSY_SKELETONS = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "spawn_mossy_skeletons"));
    public static final TagKey<Biome> INCREASED_SANDY_SKELETONS = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "increased_sandy_skeletons"));
    public static final TagKey<Biome> SHEEP_SPAWN_WITH_HORNS = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "sheep_spawn_with_horns"));
    public static final TagKey<Biome> SPAWN_PALE_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_pale_spawns"));
    public static final TagKey<Biome> SPAWN_RUSTY_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_rusty_spawns"));
    public static final TagKey<Biome> SPAWN_SPOTTED_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_spotted_spawns"));
    public static final TagKey<Biome> SPAWN_BLACK_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_black_spawns"));
    public static final TagKey<Biome> SPAWN_STRIPED_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_striped_spawns"));
    public static final TagKey<Biome> SPAWN_SNOWY_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_snowy_spawns"));
    public static final TagKey<Biome> SPAWN_ASHEN_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_ashen_spawns"));
    public static final TagKey<Biome> SPAWN_WOODS_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_woods_spawns"));
    public static final TagKey<Biome> SPAWN_CHESTNUT_WOLF = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "wolf_chestnut_spawns"));
    public static final TagKey<Biome> ADDITIONAL_WOLF_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "additional_wolf_spawns"));

    @Override
    public void onInitialize() {
        LOGGER.info("Giving mobs a fresh coat of paint...");

        // Config
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ConfigDataLoader());

        // Make wolves spawn in more biomes
        BiomeModifications.addSpawn(BiomeSelectors.tag(ADDITIONAL_WOLF_SPAWNS), SpawnGroup.CREATURE, EntityType.WOLF, 5, 4, 4);

        // Server event to respond to client request for a variant
        ServerPlayNetworking.registerGlobalReceiver(MMVNetworkingConstants.CLIENT_REQUEST_VARIANT_ID, ((server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            Entity entity = server.getOverworld().getEntity(uuid);

            // If we couldn't find the mob in the overworld, start checking all other worlds
            if (entity == null) {
                for (ServerWorld serverWorld : server.getWorlds()) {
                    Entity entity2 = serverWorld.getEntity(uuid);
                    if (entity2 != null) {
                        entity = entity2;
                    }
                }
            }

            if (entity != null) {
                NbtCompound nbt = new NbtCompound();
                entity.writeNbt(nbt);

                if (nbt.contains(NBT_KEY)) {
                    PacketByteBuf responseBuf = PacketByteBufs.create();
                    responseBuf.writeInt(entity.getId());
                    responseBuf.writeString(nbt.getString(NBT_KEY));

                    // For some reason, "Sitting" syncing breaks, so send that too I guess
                    if (entity instanceof TameableEntity) {
                        responseBuf.writeBoolean(nbt.getBoolean("Sitting"));
                    }

                    // Muddy pigs
                    if (entity instanceof PigEntity) {
                        responseBuf.writeBoolean(nbt.getBoolean(MUDDY_NBT_KEY));
                        responseBuf.writeInt(nbt.getInt(MUDDY_TIMEOUT_NBT_KEY));
                    }

                    // Sheep horns
                    if (entity instanceof SheepEntity) {
                        responseBuf.writeString(nbt.getString(SHEEP_HORN_COLOUR_NBT_KEY));
                    }

                    ServerPlayNetworking.send(handler.getPlayer(), MMVNetworkingConstants.SERVER_RESPOND_VARIANT_ID, responseBuf);
                }
            }
        }));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
