package com.github.nyuppo.client.render.entity.feature;

import com.github.nyuppo.MoreMobVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigMudFeatureRenderer<T extends PigEntity, M extends PigEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final RenderLayer MUD_SKIN = RenderLayer.getEntityTranslucent(Identifier.of(MoreMobVariants.MOD_ID, "textures/entity/pig/mud/mud_overlay.png"));

    public PigMudFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);

        if (nbt.contains(MoreMobVariants.MUDDY_NBT_KEY)) {
            if (nbt.getBoolean(MoreMobVariants.MUDDY_NBT_KEY)) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getMudTexture());
                ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
            }
        }
    }

    private RenderLayer getMudTexture() {
        return MUD_SKIN;
    }
}
