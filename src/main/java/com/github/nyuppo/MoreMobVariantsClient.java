package com.github.nyuppo;

import com.github.nyuppo.client.render.entity.feature.PigMudFeatureRenderer;
import com.github.nyuppo.networking.MMVNetworkingConstants;
import eu.pb4.polymer.networking.api.client.PolymerClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.network.PacketByteBuf;

import java.util.Set;

public class MoreMobVariantsClient implements ClientModInitializer {
    Set<EntityType> validEntities = Set.of(EntityType.CHICKEN, EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.SKELETON, EntityType.SPIDER, EntityType.WOLF, EntityType.ZOMBIE);

    @Override
    public void onInitializeClient() {
        // Add render layers
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PigEntityRenderer) {
                registrationHelper.register(new PigMudFeatureRenderer<PigEntity, PigEntityModel<PigEntity>>((FeatureRendererContext)entityRenderer));
            }
        });

        // Polymer handshake
        PolymerClientNetworking.setClientMetadata(MoreMobVariants.MMB_HELLO_PACKET, NbtInt.of(1));

        // Client event to request variant when a mob is loaded
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // Check if entity can have variants so we don't make useless requests
            if (validEntities.contains(entity.getType())) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(entity.getUuid());

                ClientPlayNetworking.send(MMVNetworkingConstants.CLIENT_REQUEST_VARIANT_ID, buf);
            }
        });

        // Client event to handle response from server about mob variant
        ClientPlayNetworking.registerGlobalReceiver(MMVNetworkingConstants.SERVER_RESPOND_VARIANT_ID, ((client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            String variantId = buf.readString();

            if (client.world != null) {
                Entity entity = client.world.getEntityById(id);
                if (entity != null) {
                    NbtCompound nbt = new NbtCompound();
                    entity.writeNbt(nbt);
                    nbt.putString(MoreMobVariants.NBT_KEY, variantId);
                    entity.readNbt(nbt);
                }
            }
        }));
    }

}
