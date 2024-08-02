package com.github.nyuppo.client.render.entity.feature;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.mixin.QuadrupedEntityModelPartAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SheepHornsFeatureRenderer<T extends SheepEntity, M extends SheepEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final RenderLayer HORNS_BROWN = RenderLayer.getEntityCutoutNoCull(Identifier.of(MoreMobVariants.MOD_ID, "textures/entity/sheep/horns/horns_brown.png"));
    private static final RenderLayer HORNS_GRAY = RenderLayer.getEntityCutoutNoCull(Identifier.of(MoreMobVariants.MOD_ID, "textures/entity/sheep/horns/horns_gray.png"));
    private static final RenderLayer HORNS_BLACK = RenderLayer.getEntityCutoutNoCull(Identifier.of(MoreMobVariants.MOD_ID, "textures/entity/sheep/horns/horns_black.png"));
    private static final RenderLayer HORNS_BEIGE = RenderLayer.getEntityCutoutNoCull(Identifier.of(MoreMobVariants.MOD_ID, "textures/entity/sheep/horns/horns_beige.png"));
    private final ModelPart horns = getTexturedModelData().createModel();

    public SheepHornsFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);

        if (nbt.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)) {
            if (!nbt.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()) {
                matrices.push();

                ModelPart sheepHead = ((QuadrupedEntityModelPartAccessor)this.getContextModel()).getHead();
                horns.copyTransform(sheepHead);

                String hornColour = nbt.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);

                if (entity.isBaby()) {
                    matrices.push();
                    matrices.translate(0.0f, 0.5f, 0.25f);

                    if (hornColour.equalsIgnoreCase("brown")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BROWN), light, OverlayTexture.DEFAULT_UV);
                    } else if (hornColour.equalsIgnoreCase("gray")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_GRAY), light, OverlayTexture.DEFAULT_UV);
                    } else if (hornColour.equalsIgnoreCase("black")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BLACK), light, OverlayTexture.DEFAULT_UV);
                    } else if (hornColour.equalsIgnoreCase("beige")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BEIGE), light, OverlayTexture.DEFAULT_UV);
                    }

                    matrices.pop();
                } else {
                    if (hornColour.equalsIgnoreCase("brown")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BROWN), light, OverlayTexture.DEFAULT_UV);
                    } else if (hornColour.equalsIgnoreCase("gray")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_GRAY), light, OverlayTexture.DEFAULT_UV);
                    } else if (hornColour.equalsIgnoreCase("black")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BLACK), light, OverlayTexture.DEFAULT_UV);
                    } else if (hornColour.equalsIgnoreCase("beige")) {
                        horns.render(matrices, vertexConsumers.getBuffer(HORNS_BEIGE), light, OverlayTexture.DEFAULT_UV);
                    }
                }

                matrices.pop();
            }
        }
    }

    private static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild(EntityModelPartNames.HEAD,
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(3.0f, -3.5f, -3.0f, 4.0f, 7.0f, 6.0f)
                        .uv(0, 13).cuboid(3.0f, 0.5f, -6.0f, 4.0f, 3.0f, 3.0f)
                        .uv(0, 19).cuboid(-7.0f, -3.5f, -3.0f, 4.0f, 7.0f, 6.0f)
                        .uv(14, 13).cuboid(-7.0f, 0.5f, -6.0f, 4.0f, 3.0f, 3.0f),
                ModelTransform.pivot(0.0f, -1.5f, -2.1f));
        return TexturedModelData.of(modelData, 32, 32);
    }
}
