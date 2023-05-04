package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantBlacklist;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.CatVariantTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatEntity.class)
public class CatBlacklistMixin {
    @Inject(method = "initialize", at = @At("TAIL"))
    private void handleBlacklistedVariants(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        boolean isValidVariant = false;

        while (!isValidVariant) {
            CatVariant currentVariant = ((CatEntity)(Object)this).getVariant();
            if ((currentVariant.equals(MoreMobVariants.DOUG) && VariantBlacklist.isBlacklisted("cat", "doug"))
                || (currentVariant.equals(MoreMobVariants.HANDSOME) && VariantBlacklist.isBlacklisted("cat", "handsome"))
                || (currentVariant.equals(MoreMobVariants.GRAY_TABBY) && VariantBlacklist.isBlacklisted("cat", "gray_tabby"))
                || (currentVariant.equals(MoreMobVariants.TORTOISESHELL) && VariantBlacklist.isBlacklisted("cat", "tortoiseshell"))) {
                boolean bl = world.getMoonSize() > 0.9F;
                TagKey<CatVariant> tagKey = bl ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
                Registries.CAT_VARIANT.getEntryList(tagKey).flatMap((list) -> {
                    return list.getRandom(world.getRandom());
                }).ifPresent((variant) -> {
                    ((CatEntity)(Object)this).setVariant((CatVariant)variant.value());
                });
                MoreMobVariants.LOGGER.info("Cat had blacklisted variant \"" + currentVariant.toString() + "\", trying \"" + ((CatEntity)(Object)this).getVariant().toString() + "\"");
            } else {
                isValidVariant = true;
            }
        }
    }
}
