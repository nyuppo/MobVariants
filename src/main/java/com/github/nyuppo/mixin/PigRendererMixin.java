package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntityRenderer.class)
public class PigRendererMixin {
    private static final Identifier DEFAULT = Identifier.ofVanilla("textures/entity/pig/pig.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(PigEntity pigEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        pigEntity.writeNbt(nbt);

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            String variant = nbt.getString(MoreMobVariants.NBT_KEY);
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                String[] split = Variants.splitVariant(variant);
                ci.setReturnValue(Identifier.of(split[0], "textures/entity/pig/" + split[1] + ".png"));
            }
        }

        if (pigEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.PIG, pigEntity.getName().getString());
            if (variant != null) {
                Identifier identifier = variant.getIdentifier();
                ci.setReturnValue(Identifier.of(identifier.getNamespace(), "textures/entity/pig/" + identifier.getPath() + ".png"));
            }
        }
    }
}
