package com.github.nyuppo.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
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

    private int getRandomVariant(Random random) {
        int i = random.nextInt(12);
        if (i == 11) {
            return 8;
        } else if (i == 10) {
            return 7;
        }
        else if (i == 9) {
            return 6;
        }
        else if (i == 8) {
            return 5;
        }
        else if (i == 7) {
            return 4;
        }
        else if (i == 6) {
            return 3;
        }
        else if (i == 5) {
            return 2;
        } else if (i == 3 || i == 4) {
            return 1;
        }
        return 0;
    }
}
