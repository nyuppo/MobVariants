package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SpiderEntity;
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

        if (nbt.contains(MoreMobVariants.NBT_KEY)) {
            String variant = nbt.getString(MoreMobVariants.NBT_KEY);
            if (variant.equals(MoreMobVariants.id("default").toString()) || variant.isEmpty()) {
                ci.setReturnValue(DEFAULT);
            } else {
                String[] split = Variants.splitVariant(variant);
                ci.setReturnValue(new Identifier(split[0], "textures/entity/spider/" + split[1] + ".png"));
            }
        }

        if (spiderEntity.hasCustomName()) {
            MobVariant variant = Variants.getVariantFromNametag(EntityType.SPIDER, spiderEntity.getName().getString());
            if (variant != null) {
                Identifier identifier = variant.getIdentifier();
                ci.setReturnValue(new Identifier(identifier.getNamespace(), "textures/entity/spider/" + identifier.getPath() + ".png"));
            }
        }
    }
}
