package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenEntityRenderer.class)
public class ChickenRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/chicken.png");
    private static final Identifier AMBER = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/amber.png");
    private static final Identifier GOLD_CRESTED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/gold_crested.png");
    private static final Identifier BRONZED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/bronzed.png");
    private static final Identifier SKEWBALD = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/skewbald.png");
    private static final Identifier STORMY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/stormy.png");
    private static final Identifier MIDNIGHT = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/midnight.png");
    private static final Identifier BONE = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/chicken/bone.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(ChickenEntity chickenEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        chickenEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(AMBER);
                    break;
                case 2:
                    ci.setReturnValue(GOLD_CRESTED);
                    break;
                case 3:
                    ci.setReturnValue(BRONZED);
                    break;
                case 4:
                    ci.setReturnValue(SKEWBALD);
                    break;
                case 5:
                    ci.setReturnValue(STORMY);
                    break;
                case 6:
                    ci.setReturnValue(MIDNIGHT);
                    break;
                case 7:
                    ci.setReturnValue(BONE);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
