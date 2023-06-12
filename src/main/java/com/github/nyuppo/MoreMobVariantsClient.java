package com.github.nyuppo;

import com.github.nyuppo.client.render.entity.feature.PigMudFeatureRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;

public class MoreMobVariantsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Add render layers
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PigEntityRenderer) {
                registrationHelper.register(new PigMudFeatureRenderer<PigEntity, PigEntityModel<PigEntity>>((FeatureRendererContext)entityRenderer));
            }
        });
    }
}
