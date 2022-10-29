package com.github.nyuppo.mixin;

import com.github.nyuppo.MobVariants;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntityRenderer.class)
public class CowRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/cow/cow.png");
    private static final Identifier ASHEN = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/ashen.png");
    private static final Identifier COOKIE = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/cookie.png");
    private static final Identifier DAIRY = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/dairy.png");
    private static final Identifier PINTO = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/pinto.png");
    private static final Identifier SUNSET = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/sunset.png");
    private static final Identifier WOOLY = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/wooly.png");
    private static final Identifier UMBRA = new Identifier(MobVariants.MOD_ID, "textures/entity/cow/umbra.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(CowEntity cowEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        cowEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(ASHEN);
                    break;
                case 2:
                    ci.setReturnValue(COOKIE);
                    break;
                case 3:
                    ci.setReturnValue(DAIRY);
                    break;
                case 4:
                    ci.setReturnValue(PINTO);
                    break;
                case 5:
                    ci.setReturnValue(SUNSET);
                    break;
                case 6:
                    ci.setReturnValue(WOOLY);
                    break;
                case 7:
                    ci.setReturnValue(UMBRA);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
