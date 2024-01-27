package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.EntityType;
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

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(CowEntity cowEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        cowEntity.writeNbt(nbt);

        if (nbt.contains("Variant")) {
            String variant = nbt.getString("Variant");
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                String[] split = Variants.splitVariant(variant);
                ci.setReturnValue(new Identifier(split[0], "textures/entity/cow/" + split[1] + ".png"));
            }
        }

        if (cowEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.COW, cowEntity.getName().getString());
            if (variant != null) {
                Identifier identifier = variant.getIdentifier();
                ci.setReturnValue(new Identifier(identifier.getNamespace(), "textures/entity/cow/" + identifier.getPath() + ".png"));
            }
        }
    }
}
