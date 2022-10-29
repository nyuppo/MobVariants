package com.github.nyuppo.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.random.Random;

@Mixin(CowEntity.class)
public abstract class CowVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<Integer> VARIANT_ID =
            DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String NBT_KEY = "Variant";
    // 0 = default
    // 1 = ashen
    // 2 = cookie
    // 3 = dairy
    // 4 = pinto
    // 5 = sunset
    // 6 = wooly
    // 7 = umbra

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((CowEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, 0);
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt(NBT_KEY, ((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getInt(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        int i = this.getRandomVariant(world.getRandom());
        ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, i);
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/CowEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<CowEntity> ci) {
        CowEntity child = (CowEntity)EntityType.COW.create(world);

        int i = 0;
        if (entity.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((CowEntity)(Object)this).writeNbt(thisNbt);
            NbtCompound parentNbt = new NbtCompound();
            entity.writeNbt(parentNbt);

            if (thisNbt.contains("Variant") && parentNbt.contains("Variant")) {
                int thisVariant = thisNbt.getInt("Variant");
                int parentVariant = parentNbt.getInt("Variant");

                if (thisVariant == parentVariant) {
                    // If both parents are the same variant, just pick that one
                    i = thisVariant;
                } else {
                    // Otherwise, pick a random parent's variant
                    i = entity.getRandom().nextBoolean() ? thisVariant : parentVariant;
                }
            }
        } else {
            // Give child random variant
            i = this.getRandomVariant(entity.getRandom());
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putInt("Variant", i);
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }

    private int getRandomVariant(Random random) {
        int i = random.nextInt(16);
        if (i == 0) {
            // Umbra
            return 7;
        } else if (i > 0 && i <= 2) {
            // Ashen
            return 1;
        } else if (i > 2 && i <= 4) {
            // Cookie
            return 2;
        } else if (i > 4 && i <= 6) {
            // Dairy
            return 3;
        } else if (i > 6 && i <= 8) {
            // Pinto
            return 4;
        } else if (i > 8 && i <= 10) {
            // Sunset
            return 5;
        } else if (i > 10 && i <= 12) {
            // Wooly
            return 6;
        }
        // Default
        return 0;
    }
}
