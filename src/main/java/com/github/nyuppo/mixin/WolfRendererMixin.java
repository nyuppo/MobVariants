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
    private static final Identifier JUPITER_WILD = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/jupiter_wild.png");
    private static final Identifier JUPITER_TAMED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/jupiter_tame.png");
    private static final Identifier JUPITER_ANGRY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/jupiter_angry.png");
    private static final Identifier HUSKY_WILD = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/husky_wild.png");
    private static final Identifier HUSKY_TAMED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/husky_tame.png");
    private static final Identifier HUSKY_ANGRY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/husky_angry.png");
    private static final Identifier GERMAN_SHEPHERD_WILD = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/german_shepherd_wild.png");
    private static final Identifier GERMAN_SHEPHERD_TAMED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/german_shepherd_tame.png");
    private static final Identifier GERMAN_SHEPHERD_ANGRY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/german_shepherd_angry.png");
    private static final Identifier GOLDEN_RETRIEVER_WILD = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/golden_retriever_wild.png");
    private static final Identifier GOLDEN_RETRIEVER_TAMED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/golden_retriever_tame.png");
    private static final Identifier GOLDEN_RETRIEVER_ANGRY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/golden_retriever_angry.png");
    private static final Identifier FRENCH_BULLDOG_WILD = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/french_bulldog_wild.png");
    private static final Identifier FRENCH_BULLDOG_TAMED = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/french_bulldog_tame.png");
    private static final Identifier FRENCH_BULLDOG_ANGRY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/wolf/french_bulldog_angry.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(WolfEntity wolfEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        wolfEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    if (wolfEntity.isTamed()) {
                        ci.setReturnValue(JUPITER_TAMED);
                    } else {
                        ci.setReturnValue(wolfEntity.hasAngerTime() ? JUPITER_ANGRY : JUPITER_WILD);
                    }
                    break;
                case 2:
                    if (wolfEntity.isTamed()) {
                        ci.setReturnValue(HUSKY_TAMED);
                    } else {
                        ci.setReturnValue(wolfEntity.hasAngerTime() ? HUSKY_ANGRY : HUSKY_WILD);
                    }
                    break;
                case 3:
                    if (wolfEntity.isTamed()) {
                        ci.setReturnValue(GERMAN_SHEPHERD_TAMED);
                    } else {
                        ci.setReturnValue(wolfEntity.hasAngerTime() ? GERMAN_SHEPHERD_ANGRY : GERMAN_SHEPHERD_WILD);
                    }
                    break;
                case 4:
                    if (wolfEntity.isTamed()) {
                        ci.setReturnValue(GOLDEN_RETRIEVER_TAMED);
                    } else {
                        ci.setReturnValue(wolfEntity.hasAngerTime() ? GOLDEN_RETRIEVER_ANGRY : GOLDEN_RETRIEVER_WILD);
                    }
                    break;
                case 5:
                    if (wolfEntity.isTamed()) {
                        ci.setReturnValue(FRENCH_BULLDOG_TAMED);
                    } else {
                        ci.setReturnValue(wolfEntity.hasAngerTime() ? FRENCH_BULLDOG_ANGRY : FRENCH_BULLDOG_WILD);
                    }
                    break;
                case 0:
                default:
                    if (wolfEntity.isTamed()) {
                        ci.setReturnValue(DEFAULT_TAMED);
                    } else {
                        ci.setReturnValue(wolfEntity.hasAngerTime() ? DEFAULT_ANGRY : DEFAULT_WILD);
                    }
                    break;
            }
        }
    }
}
