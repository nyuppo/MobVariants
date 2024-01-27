package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntityRenderer.class)
public class WolfRendererMixin {
    private static final Identifier DEFAULT_WILD = new Identifier("textures/entity/wolf/wolf.png");
    private static final Identifier DEFAULT_TAMED = new Identifier("textures/entity/wolf/wolf_tame.png");
    private static final Identifier DEFAULT_ANGRY = new Identifier("textures/entity/wolf/wolf_angry.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(WolfEntity wolfEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        wolfEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                if (wolfEntity.isTamed()) {
                    ci.setReturnValue(DEFAULT_TAMED);
                } else {
                    ci.setReturnValue(wolfEntity.hasAngerTime() ? DEFAULT_ANGRY : DEFAULT_WILD);
                }
            } else {
                String[] split = Variants.splitVariant(variant);
                if (wolfEntity.isTamed()) {
                    ci.setReturnValue(new Identifier(split[0], "textures/entity/wolf/" + split[1] + "_tame.png"));
                } else {
                    ci.setReturnValue(wolfEntity.hasAngerTime() ? new Identifier(split[0], "textures/entity/wolf/" + split[1] + "_angry.png") : new Identifier(split[0], "textures/entity/wolf/" + split[1] + "_wild.png"));
                }
            }
        }

        if (wolfEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.WOLF, wolfEntity.getName().getString());
            if (variant != null) {
                Identifier identifier = variant.getIdentifier();
                if (wolfEntity.isTamed()) {
                    ci.setReturnValue(new Identifier(identifier.getNamespace(), "textures/entity/wolf/" + identifier.getPath() + "_tame.png"));
                } else {
                    ci.setReturnValue(wolfEntity.hasAngerTime() ? new Identifier(identifier.getNamespace(), "textures/entity/wolf/" + identifier.getPath() + "_angry.png") : new Identifier(identifier.getNamespace(), "textures/entity/wolf/" + identifier.getPath() + "_wild.png"));
                }
            }
        }
    }
}
