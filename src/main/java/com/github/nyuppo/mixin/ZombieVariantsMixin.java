package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantWeights;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<Integer> VARIANT_ID =
            DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String NBT_KEY = "Variant";
    /*
    *   0 = steve (default)
    *   1 = alex
    *   2 = ari
    *   3 = efe
    *   4 = kai
    *   5 = makena
    *   6 = noor
    *   7 = sunny
    *   8 = zuri
     */

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((ZombieEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, 0);
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt(NBT_KEY, ((ZombieEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((ZombieEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getInt(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        int i = this.getRandomVariant(world.getRandom());
        ((ZombieEntity)(Object)this).getDataTracker().set(VARIANT_ID, i);
    }

    private int getVariantID(String variantName) {
        return switch (variantName) {
            case "alex" -> 1;
            case "ari" -> 2;
            case "efe" -> 3;
            case "kai" -> 4;
            case "makena" -> 5;
            case "noor" -> 6;
            case "sunny" -> 7;
            case "zuri" -> 8;
            default -> 0;
        };
    }

    private int getRandomVariant(Random random) {
        return getVariantID(VariantWeights.getRandomVariant("zombie", random));
    }
}
