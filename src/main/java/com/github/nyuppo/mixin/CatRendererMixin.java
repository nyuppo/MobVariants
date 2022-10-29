package com.github.nyuppo.mixin;

import com.github.nyuppo.MobVariants;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatEntityRenderer.class)
public class CatRendererMixin {
    private static final Identifier GRAY_TABBY = new Identifier(MobVariants.MOD_ID, "textures/entity/cat/gray_tabby.png");
    private static final Identifier HANDSOME = new Identifier(MobVariants.MOD_ID, "textures/entity/cat/handsome.png");
    private static final Identifier DOUG = new Identifier(MobVariants.MOD_ID, "textures/entity/cat/doug.png");

    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void onGetTexture(CatEntity catEntity, CallbackInfoReturnable<Identifier> ci) {
        CatVariant variant = catEntity.getVariant();

        if (variant == MobVariants.GRAY_TABBY) {
            ci.setReturnValue(GRAY_TABBY);
        } else if (variant == MobVariants.HANDSOME) {
            ci.setReturnValue(HANDSOME);
        } else if (variant == MobVariants.DOUG) {
            ci.setReturnValue(DOUG);
        }
    }
}
