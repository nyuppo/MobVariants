package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpiderEntityRenderer.class)
public class SpiderRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/spider/spider.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(SpiderEntity spiderEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        spiderEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals("default")) {
                ci.setReturnValue(DEFAULT);
            } else {
                ci.setReturnValue(new Identifier(MoreMobVariants.MOD_ID, "textures/entity/spider/" + variant + ".png"));
            }
        }
    }
}
