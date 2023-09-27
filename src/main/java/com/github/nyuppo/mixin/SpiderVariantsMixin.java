package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantWeights;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpiderEntity.class)
public class SpiderVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((SpiderEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, "default");
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((SpiderEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((SpiderEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        String variant = this.getRandomVariant(world.getRandom());
        ((SpiderEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
    }

    private String getRandomVariant(Random random) {
        String test = VariantWeights.getRandomVariant("spider", random);
        MoreMobVariants.LOGGER.info(test);
        MoreMobVariants.LOGGER.info(VariantWeights.getSpiderWeights().toString());
        return test;
    }

}
