package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EyesFeatureRenderer.class)
public class CustomEyesFeatureMixin<T extends Entity> {
    @ModifyArgs(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;")
    )
    private void mixinEyesFeatureTexture(Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        SpiderEntity spider = null;
        if (entity instanceof SpiderEntity) {
            spider = (SpiderEntity)entity;
        }

        if (spider != null) {
            NbtCompound nbt = new NbtCompound();
            spider.writeCustomDataToNbt(nbt);

            if (nbt.contains(MoreMobVariants.NBT_KEY)) {
                String variant = nbt.getString(MoreMobVariants.NBT_KEY);
                if (variant.isEmpty()) {
                    return;
                }

                String[] split = Variants.splitVariant(variant);

                if (Variants.getVariant(EntityType.SPIDER, Identifier.of(split[0], split[1])).hasCustomEyes()) {
                    args.set(0, RenderLayer.getEyes(Identifier.of(split[0], "textures/entity/spider/eyes/" + split[1] + ".png")));
                }
            }
        }
    }
}
