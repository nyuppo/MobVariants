package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieBaseEntityRenderer.class)
public class ZombieRendererMixin {
    private static final Identifier DEFAULT = new Identifier("textures/entity/zombie/zombie.png");
    private static final Identifier ALEX = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_alex.png");
    private static final Identifier ARI = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_ari.png");
    private static final Identifier EFE = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_efe.png");
    private static final Identifier KAI = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_kai.png");
    private static final Identifier MAKENA = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_makena.png");
    private static final Identifier NOOR = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_noor.png");
    private static final Identifier SUNNY = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_sunny.png");
    private static final Identifier ZURI = new Identifier(MoreMobVariants.MOD_ID, "textures/entity/zombie/zombie_zuri.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(ZombieEntity zombieEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        zombieEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            int i = nbt.getInt("Variant");
            switch (i) {
                case 1:
                    ci.setReturnValue(ALEX);
                    break;
                case 2:
                    ci.setReturnValue(ARI);
                    break;
                case 3:
                    ci.setReturnValue(EFE);
                    break;
                case 4:
                    ci.setReturnValue(KAI);
                    break;
                case 5:
                    ci.setReturnValue(MAKENA);
                    break;
                case 6:
                    ci.setReturnValue(NOOR);
                    break;
                case 7:
                    ci.setReturnValue(SUNNY);
                    break;
                case 8:
                    ci.setReturnValue(ZURI);
                    break;
                case 0:
                default:
                    ci.setReturnValue(DEFAULT);
                    break;
            }
        }
    }
}
