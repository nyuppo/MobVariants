package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SheepWoolFeatureRenderer.class)
public class SheepWoolFeatureMixin {
    private static final Identifier DEFAULT_FUR = new Identifier("textures/entity/sheep/sheep_fur.png");
    private static final Identifier TEST = new Identifier("moremobvariants", "textures/entity/sheep/wool/fuzzy.png");

    @ModifyArgs(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/SheepWoolFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V")
    )
    private void mixinSheepFurTexture(Args args) {
        SheepEntity sheepEntity = args.get(6);
        NbtCompound nbt = new NbtCompound();
        sheepEntity.writeCustomDataToNbt(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.isEmpty()) {
                return;
            }

            String[] split = Variants.splitVariant(variant);

            if (Variants.getVariant(Variants.Mob.SHEEP, Identifier.of(split[0], split[1])).hasCustomWool()) {
                args.set(2, new Identifier(split[0], "textures/entity/sheep/wool/" + split[1] + ".png"));
            }
        }
    }
}
