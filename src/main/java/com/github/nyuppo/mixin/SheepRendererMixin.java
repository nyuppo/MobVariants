package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepEntityRenderer.class)
public class SheepRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/sheep/sheep.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(SheepEntity sheepEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        sheepEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals("default") || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                ci.setReturnValue(new Identifier(MoreMobVariants.MOD_ID, "textures/entity/sheep/" + variant + ".png"));
            }
        }
    }
}
