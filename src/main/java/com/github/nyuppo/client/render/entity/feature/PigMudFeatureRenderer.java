package com.github.nyuppo.client.render.entity.feature;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.mixin.PigVariantsMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PigMudFeatureRenderer<T extends PigEntity, M extends PigEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final RenderLayer MUD_SKIN = RenderLayer.getEntityTranslucent(new Identifier(MoreMobVariants.MOD_ID, "textures/entity/pig/mud/mud_overlay.png"));
    private static final String MUDDY_NBT_KEY = "IsMuddy";

    public PigMudFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);

        if (nbt.contains(MUDDY_NBT_KEY)) {
            if (nbt.getBoolean(MUDDY_NBT_KEY)) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getMudTexture());
                ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private RenderLayer getMudTexture() {
        return MUD_SKIN;
    }
}
