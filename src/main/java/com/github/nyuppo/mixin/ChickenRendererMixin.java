package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenEntityRenderer.class)
public class ChickenRendererMixin {
    private static final Identifier DEFAULT = Identifier.ofVanilla("textures/entity/chicken.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(ChickenEntity chickenEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        chickenEntity.writeNbt(nbt);

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            String variant = nbt.getString(MoreMobVariants.NBT_KEY);
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                String[] split = Variants.splitVariant(variant);
                ci.setReturnValue(Identifier.of(split[0], "textures/entity/chicken/" + split[1] + ".png"));
            }
        }

        if (chickenEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.CHICKEN, chickenEntity.getName().getString());
            if (variant != null) {
                Identifier identifier = variant.getIdentifier();
                ci.setReturnValue(Identifier.of(identifier.getNamespace(), "textures/entity/chicken/" + identifier.getPath() + ".png"));
            }
        }
    }
}
