package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkeletonEntityRenderer.class)
public class SkeletonRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/skeleton/skeleton.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(AbstractSkeletonEntity skeletonEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        skeletonEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                String[] split = Variants.splitVariant(variant);
                ci.setReturnValue(new Identifier(split[0], "textures/entity/skeleton/" + split[1] + ".png"));
            }
        }
    }
}