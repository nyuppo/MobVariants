package com.github.nyuppo.mixin;

import com.github.nyuppo.MobVariants;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.entity.passive.ChickenEntity;
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
    private static final Identifier PATCHED = new Identifier(MobVariants.MOD_ID, "textures/entity/sheep/patched.png");
    private static final Identifier FUZZY = new Identifier(MobVariants.MOD_ID, "textures/entity/sheep/fuzzy.png");
    private static final Identifier ROCKY = new Identifier(MobVariants.MOD_ID, "textures/entity/sheep/rocky.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(SheepEntity sheepEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        sheepEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(PATCHED);
                    break;
                case 2:
                    ci.setReturnValue(FUZZY);
                    break;
                case 3:
                    ci.setReturnValue(ROCKY);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
