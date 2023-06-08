package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantWeights;
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
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((CowEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, "default");
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        String variant = this.getRandomVariant(world.getRandom());
        ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((CowEntity)(Object)this).getWorld().getRandom());
            ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/CowEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<CowEntity> ci) {
        CowEntity child = (CowEntity)EntityType.COW.create(world);

        String variant = "default";
        if (entity.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((CowEntity)(Object)this).writeNbt(thisNbt);
            NbtCompound parentNbt = new NbtCompound();
            entity.writeNbt(parentNbt);

            if (thisNbt.contains("Variant") && parentNbt.contains("Variant")) {
                String thisVariant = thisNbt.getString("Variant");
                String parentVariant = parentNbt.getString("Variant");

                if (thisVariant.equals(parentVariant)) {
                    // If both parents are the same variant, just pick that one
                    variant = thisVariant;
                } else {
                    // Otherwise, pick a random parent's variant
                    variant = entity.getRandom().nextBoolean() ? thisVariant : parentVariant;
                }
            }
        } else {
            // Give child random variant
            variant = this.getRandomVariant(entity.getRandom());
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant);
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }

    private String getRandomVariant(Random random) {
        //return getVariantID(VariantWeights.getRandomVariant("cow", random));
        return VariantWeights.getRandomVariant("cow", random);
    }
}
