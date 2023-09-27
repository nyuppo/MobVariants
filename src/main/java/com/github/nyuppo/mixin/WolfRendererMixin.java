package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.render.entity.WolfEntityRenderer;
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
            if (variant.equals("default") || variant.isEmpty()) {
                if (wolfEntity.isTamed()) {
                    ci.setReturnValue(DEFAULT_TAMED);
                } else {
                    ci.setReturnValue(wolfEntity.hasAngerTime() ? DEFAULT_ANGRY : DEFAULT_WILD);
                }
            } else {
                if (wolfEntity.isTamed()) {
                    ci.setReturnValue(new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/" + variant + "_tame.png"));
                } else {
                    ci.setReturnValue(wolfEntity.hasAngerTime() ? new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/" + variant + "_angry.png") : new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/" + variant + "_wild.png"));
                }
            }
        }
    }
}
