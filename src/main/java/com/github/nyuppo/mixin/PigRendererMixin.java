package com.github.nyuppo.mixin;

import com.github.nyuppo.MobVariants;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntityRenderer.class)
public class PigRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/pig/pig.png");
    private static final Identifier MOTTLED = new Identifier(MobVariants.MOD_ID, "textures/entity/pig/mottled.png");
    private static final Identifier PIEBALD = new Identifier(MobVariants.MOD_ID, "textures/entity/pig/piebald.png");
    private static final Identifier PINK_FOOTED = new Identifier(MobVariants.MOD_ID, "textures/entity/pig/pink_footed.png");
    private static final Identifier SOOTY = new Identifier(MobVariants.MOD_ID, "textures/entity/pig/sooty.png");
    private static final Identifier SPOTTED = new Identifier(MobVariants.MOD_ID, "textures/entity/pig/spotted.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(PigEntity pigEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        pigEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(MOTTLED);
                    break;
                case 2:
                    ci.setReturnValue(PIEBALD);
                    break;
                case 3:
                    ci.setReturnValue(PINK_FOOTED);
                    break;
                case 4:
                    ci.setReturnValue(SOOTY);
                    break;
                case 5:
                    ci.setReturnValue(SPOTTED);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
